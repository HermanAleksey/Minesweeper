package com.example.sapper.activity

import HostField
import Saper
import UserField
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.*
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sapper.constant.Constant
import com.example.sapper.constant.GameConstant
import com.example.sapper.logic.MinefieldAdapter
import com.example.sapper.R
import kotlinx.android.synthetic.main.activity_minefield.*


class MinefieldActivity : AppCompatActivity() {

    var hostField: HostField? = null
    var height: Int = 0
    var width: Int = 0
    var minesCount: Int = 0
    var gameTimeMinutes: Int = 0
    var gameTimeSeconds: Int = 0

    lateinit var tv_minefield_seconds: TextView
    lateinit var tv_minefield_minutes: TextView

    var gameTimerMilli: Long = 0
    private var countDownTimer: CountDownTimer? = null

    lateinit var handler: Handler

    var millisecondTime: Long = 0
    var startTime: Long = 0
    var timeBuff: Long = 0
    var updateTime: Long = 0
    var seconds: Int = 0
    var minutes: Int = 0
    var milliSeconds: Int = 0
    private var runnable: Runnable = object : Runnable {
        override fun run() {
            millisecondTime = SystemClock.uptimeMillis() - startTime
            updateTime = timeBuff + millisecondTime
            seconds = (updateTime / 1000).toInt()
            minutes = seconds / 60
            seconds %= 60
            milliSeconds = (updateTime % 1000).toInt()
            tv_minefield_seconds.text = "${
                if (seconds < 10) {
                    "0$seconds"
                } else seconds
            }"
            tv_minefield_minutes.text = "${
                if (minutes < 10) {
                    "0$minutes"
                } else minutes
            }"
            handler.postDelayed(this, 0)
        }
    }

    private lateinit var soundPool: SoundPool
    private var soundExplosion: Int = 0
    private var soundWin: Int = 0
    private var soundFlagDrop: Int = 0
    private var soundTap: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minefield)
        /*sound pool settings for diff versions*/
        soundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build()
        } else {
            SoundPool(7, AudioManager.STREAM_MUSIC, 0)
        }
        /*loading sounds */
        soundExplosion = soundPool.load(this, R.raw.explosion_8bit, 1)
        soundWin = soundPool.load(this, R.raw.win_01, 1)
        soundFlagDrop = soundPool.load(this, R.raw.flag_drop, 1)
        soundTap = soundPool.load(this, R.raw.new_tap, 1)

        /*For timer*/
        tv_minefield_seconds = findViewById(R.id.tv_minefield_seconds)
        tv_minefield_minutes = findViewById(R.id.tv_minefield_minutes)
        handler = Handler()

        /*getting data about game depends on game mode*/
        val gameMode = intent.getStringExtra(Constant().GAME_MODE)
        height = intent.getIntExtra(GameConstant().HEIGHT_TAG, 0)
        width = intent.getIntExtra(GameConstant().WIDTH_TAG, 0)
        minesCount = intent.getIntExtra(GameConstant().MINES_COUNT_TAG, 0)
        gameTimeMinutes = intent.getIntExtra(GameConstant().GAME_TIME_MINUTES_TAG, 0)
        gameTimeSeconds = intent.getIntExtra(GameConstant().GAME_TIME_SECONDS_TAG, 0)
        val firstClickCanBeOnAMine =
            intent.getBooleanExtra(GameConstant().FIRST_CLICK_MINE_TAG, false)

        /*stopwatch / timer starting*/
        gameTimerMilli = translateToMilli("$gameTimeMinutes:$gameTimeSeconds")
        if (gameTimerMilli == 0L) {
            startTime = SystemClock.uptimeMillis()
            runnable.run()
        } else {
            countDownTimer = object : CountDownTimer(gameTimerMilli, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    if (tv_minefield_seconds.text != "0") {
                        tv_minefield_seconds.text =
                            "${tv_minefield_seconds.text.toString().toInt() - 1}"
                    } else {
                        tv_minefield_minutes.text =
                            "${tv_minefield_minutes.text.toString().toInt() - 1}"
                        tv_minefield_seconds.text = "59"
                    }
                }

                override fun onFinish() {
                    intentToResultActivity(false)
                }
            }.start()
        }

        var useSameField: Boolean = false
        var closeAfterGame: Boolean = false
        if (gameMode == Constant().GAME_MODE_BLUETOOTH) {
            useSameField =
                intent.getBooleanExtra(GameConstant().USE_SAME_FIELD_TAG, false)
            closeAfterGame =
                intent.getBooleanExtra(GameConstant().CLOSE_AFTER_GAME_TAG, false)
        }


        /*filling view*/
        tv_minefield_field_width.text = "$width"
        tv_minefield_field_height.text = "$height"
        tv_minefield_minutes.text = "$gameTimeMinutes"
        tv_minefield_seconds.text = "$gameTimeSeconds"
        tv_minefield_mines.text = "$minesCount"
        val linearLayoutMinefield =
            findViewById<LinearLayout>(R.id.linear_layout_minefield)
        /*Visual minefield (from buttons)*/
        val arrayButtonsField =
            MinefieldAdapter().createMinefield(
                width, height, linearLayoutMinefield, this
            )

        /*spinner have to restore previous selected*/
        val adapterMinefieldCellSize: ArrayAdapter<String> = ArrayAdapter(
            this, R.layout.spinner_layout_game_settings,
            R.id.textview_spinner_layout_text, resources.getStringArray(R.array.minefieldCellSizes)
        )
        spin_minefield_cell_size.adapter = adapterMinefieldCellSize
        /*getting previous selected size from sharedPreferences*/
        val sharedPreferences = getPreferences(MODE_PRIVATE)
        val selectedCellSize = sharedPreferences.getInt(Constant().CELL_SIZE, 0)
        spin_minefield_cell_size.setSelection(selectedCellSize)
        spin_minefield_cell_size.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, itemSelected: View?,
                    selectedItemPosition: Int, selectedId: Long
                ) {
                    //saving changes on sharedPrefs
                    sharedPreferences.edit()
                        .putInt(Constant().CELL_SIZE, selectedItemPosition)
                        .apply()
                    //for each size defined constant values
                    val sizeParam: Int
                    val textParam: Float
                    when (selectedItemPosition) {
                        0 -> {
                            sizeParam = 60
                            textParam = 9f
                        }
                        1 -> {
                            sizeParam = 80
                            textParam = 14f
                        }
                        2 -> {
                            sizeParam = 100
                            textParam = 22f
                        }
                        3 -> {
                            sizeParam = 130
                            textParam = 26f
                        }
                        else -> {
                            sizeParam = 0
                            textParam = 0f
                        }

                    }
                    //if size was changed - changing views
                    arrayButtonsField.forEach {
                        it.forEach { btn ->
                            btn.textSize = textParam
                            val params = btn.layoutParams
                            params.width = sizeParam
                            params.height = sizeParam
                            btn.layoutParams = params
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        /*generating field only if first click can be on mine*/
        if (firstClickCanBeOnAMine) {
            hostField = HostField(width, height, minesCount)
        }
        val userField = UserField(width, height, minesCount)

        MinefieldAdapter().setupMinefield(userField.content, arrayButtonsField)

        /*for turning on only 1 button in the same time*/
        togglebutton_minefield_open
            .setOnClickListener(onToggleButtonClickListener)
        togglebutton_minefield_flag
            .setOnClickListener(onToggleButtonClickListener)

        setOnClickListenerForField(
            arrayButtonsField,
            userField.content
        )
    }

    private fun translateToMilli(str: String): Long {
        val hours = str.substringBefore(":").toInt()
        val minutes = str.substringAfter(":").toInt()

        return ((hours * 60) + minutes) * 1000.toLong()
    }

    /*define how each cell gonna react to click with flag/open selected*/
    private fun setOnClickListenerForField(
        arrayButtonsField: Array<Array<Button>>,
        userField: Array<Array<Char>>
    ) {
        for (y in arrayButtonsField.indices) {
            for (x in arrayButtonsField[y].indices) {
                arrayButtonsField[x][y].setOnClickListener {
                    /*Opener*/
                    if (togglebutton_minefield_open.isChecked) {
                        if (hostField == null) {
                            hostField = HostField(width, height, minesCount, x, y)
                        }
                        playSound(soundTap)

                        val keepGame = Saper().openCoordinate(x, y, hostField!!.content, userField)
                        if (!keepGame) {
                            playSound(soundExplosion)
                            tv_minefield_seconds.postDelayed({
                                intentToResultActivity(false)
                                if (countDownTimer != null) {
                                    countDownTimer!!.cancel()
                                }
                            }, 500)
                        } else {
                            /*если не проиграл - проверить, возможно теперь условия выполняются.*/
                            /*т.к. openCoordinate возвращает false только если проиграл и не отличает
                            * продолжение игры от победы*/
                            if (Saper().checkWinCondition(hostField!!.content, userField)) {
                                playSound(soundWin)
                                tv_minefield_seconds.postDelayed({
                                    intentToResultActivity(true)
                                    if (countDownTimer != null) {
                                        countDownTimer!!.cancel()
                                    }
                                },500)
                            }
                        }
                        MinefieldAdapter().setupMinefield(userField, arrayButtonsField)
                    }
                    /*FLAG*/
                    if (togglebutton_minefield_flag.isChecked) {
                        if (hostField == null) {
                            hostField = HostField(width, height, minesCount, x, y)
                        }
                        playSound(soundFlagDrop)
                        val win = Saper().useFlagOnSpot(x, y, hostField!!.content, userField)
                        MinefieldAdapter().setupMinefield(userField, arrayButtonsField)
                        if (win) {
                            playSound(soundWin)
                            tv_minefield_seconds.postDelayed({
                                intentToResultActivity(true)
                                if (countDownTimer != null) {
                                    countDownTimer!!.cancel()
                                }
                            },500)
                        }
                    }
                }
            }
        }
    }

    private fun playSound(sound: Int){
        soundPool.play(
            sound,
            1.0.toFloat(), 1.0.toFloat(),
            1, 0, 1.0.toFloat()
        )
    }

    private fun intentToResultActivity(result: Boolean) {
        val mIntent = Intent(this, GameResultsActivity::class.java)

        mIntent.putExtra(
            GameConstant().GAME_RESULT,
            result
        )
        mIntent.putExtra(
            GameConstant().WIDTH_TAG,
            tv_minefield_field_width.text.toString().toInt()
        )
        mIntent.putExtra(
            GameConstant().HEIGHT_TAG,
            tv_minefield_field_height.text.toString().toInt()
        )
        mIntent.putExtra(
            GameConstant().MINES_COUNT_TAG,
            tv_minefield_mines.text.toString().toInt()
        )
        if (gameTimerMilli == 0L) {
            mIntent.putExtra(
                GameConstant().GAME_TIME_MINUTES_TAG,
                tv_minefield_minutes.text.toString().toInt()
            )
            mIntent.putExtra(
                GameConstant().GAME_TIME_SECONDS_TAG,
                tv_minefield_seconds.text.toString().toInt()
            )
        } else {
            mIntent.putExtra(
                GameConstant().GAME_TIME_MINUTES_TAG,
                (gameTimeMinutes - tv_minefield_minutes.text.toString().toInt())
            )
            mIntent.putExtra(
                GameConstant().GAME_TIME_SECONDS_TAG,
                (gameTimeSeconds - tv_minefield_seconds.text.toString().toInt())
            )
        }
        startActivity(mIntent)
        finish()
    }

    //чтобы одновременно включена была только 1 кнопка
    private val onToggleButtonClickListener = View.OnClickListener {
        when (it.id) {
            togglebutton_minefield_open.id -> {
                if (togglebutton_minefield_open.isChecked) {
                    togglebutton_minefield_flag.isChecked = false
                }
            }
            togglebutton_minefield_flag.id -> {
                if (togglebutton_minefield_flag.isChecked) {
                    togglebutton_minefield_open.isChecked = false
                }
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        tv_minefield_field_width.text =
            savedInstanceState.getInt(GameConstant().WIDTH_TAG).toString()
        tv_minefield_field_height.text =
            savedInstanceState.getInt(GameConstant().HEIGHT_TAG).toString()
        tv_minefield_mines.text =
            savedInstanceState.getInt(GameConstant().MINES_COUNT_TAG).toString()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(
            GameConstant().WIDTH_TAG,
            tv_minefield_field_width.text.toString().toInt()
        )
        outState.putInt(
            GameConstant().HEIGHT_TAG,
            tv_minefield_field_height.text.toString().toInt()
        )
        outState.putInt(
            GameConstant().MINES_COUNT_TAG,
            tv_minefield_mines.text.toString().toInt()
        )
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_toolbar_rules) {
            showGameRulesAlertDialog(this)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showGameRulesAlertDialog(context: Context) {
        val dialog = AlertDialog.Builder(context)
            .setTitle(R.string.rules)
            .setMessage("R.string.rulesOfTheGame")
            .setPositiveButton(R.string.understand, null)
            .show()

        val positiveButton: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setTextColor(context.resources.getColor(R.color.colorPrimaryDark))
    }

    override fun onDestroy() {
        soundPool.release()
        super.onDestroy()
    }
}