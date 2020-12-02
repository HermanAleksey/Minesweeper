package com.example.sapper.activity.MinefieldActivity.activity

import HostField
import Saper
import UserField
import android.content.Context
import android.content.Intent
import android.media.SoundPool
import android.os.*
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sapper.constant.GameConstant
import com.example.sapper.logic.MinefieldAdapter
import com.example.sapper.R
import com.example.sapper.activity.GameResultsActivity
import com.example.sapper.activity.MinefieldActivity.IMinefieldActivity
import com.example.sapper.logic.SoundPoolWorker
import com.example.sapper.logic.TimeWorker
import kotlinx.android.synthetic.main.activity_minefield.*


class MinefieldActivity : AppCompatActivity(), IMinefieldActivity {
    var hostField: HostField? = null
    var height: Int = 0
    var width: Int = 0
    var minesCount: Int = 0
    var gameTimeMinutes: Int = 0
    var gameTimeSeconds: Int = 0

    var gameTimerMilli: Long = 0
    private var countDownTimer: CountDownTimer? = null

    lateinit var handler: Handler

//    private var startTime: Long = 0
    private lateinit var timeWorker: TimeWorker
    private lateinit var stopWatchRunnable: Runnable
    private lateinit var soundPoolWorker: SoundPoolWorker

    private lateinit var soundPool: SoundPool
    private var soundExplosion: Int = 0
    private var soundWin: Int = 0
    private var soundFlagDrop: Int = 0
    private var soundTap: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minefield)
        /*sound pool settings for diff versions*/
        soundPoolWorker = SoundPoolWorker()
        soundPool = soundPoolWorker.getSoundPool()
        loadSounds()

        /*For timer*/
        handler = Handler()
        timeWorker = TimeWorker(this, handler)

        /*getting data about game depends on game mode*/
        height = intent.getIntExtra(GameConstant().EXTRA_HEIGHT, 0)
        width = intent.getIntExtra(GameConstant().EXTRA_WIDTH, 0)
        minesCount = intent.getIntExtra(GameConstant().EXTRA_MINES_COUNT, 0)
        gameTimeMinutes = intent.getIntExtra(GameConstant().EXTRA_GAME_TIME_MINUTES, 0)
        gameTimeSeconds = intent.getIntExtra(GameConstant().EXTRA_GAME_TIME_SECONDS, 0)
        val firstClickCanBeOnAMine =
            intent.getBooleanExtra(GameConstant().EXTRA_FIRST_CLICK_MINE, false)

        startStopWatch()

        fillViewElements()

        /*Visual minefield (from buttons)*/
        val arrayButtonsField =
            MinefieldAdapter().createMinefield(
                width, height, linear_layout_minefield, this
            )

        configureSeekBar(arrayButtonsField)

        /*generating field only if first click can be on mine*/
        if (firstClickCanBeOnAMine) {
            hostField = HostField(width, height, minesCount)
        }
        val userField = UserField(width, height, minesCount)

        MinefieldAdapter().setupMinefield(userField.content, arrayButtonsField)

        setOnClickListenerForField(
            arrayButtonsField,
            userField.content
        )
    }

    private fun startStopWatch() {
        /*stopwatch / timer starting*/
        gameTimerMilli = timeWorker.translateToMilli("$gameTimeMinutes:$gameTimeSeconds")
        if (gameTimerMilli == 0L) {
            val startTime = SystemClock.uptimeMillis()
            stopWatchRunnable = timeWorker.getStopWatchRunnable(startTime)
            stopWatchRunnable.run()
        } else {
            timeWorker.getCountDownTimer(
                tv_minefield_minutes,
                tv_minefield_seconds, gameTimerMilli
            ).start()
        }
    }

    private fun configureSeekBar(arrayButtonsField: Array<Array<Button>>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seek_bar_minefield_cell_size.min = 30
        }
        seek_bar_minefield_cell_size.max = 200
        seek_bar_minefield_cell_size.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                arrayButtonsField.forEach {
                    it.forEach { btn ->
                        val params = btn.layoutParams
                        params.width = progress
                        params.height = progress
                        btn.layoutParams = params
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun fillViewElements() {
        tv_minefield_field_width.text = "$width"
        tv_minefield_field_height.text = "$height"
        tv_minefield_minutes.text = if (gameTimeMinutes < 10) {
            "0$gameTimeMinutes"
        } else "$gameTimeMinutes"
        tv_minefield_seconds.text = if (gameTimeSeconds < 10) {
            "0$gameTimeSeconds"
        } else "$gameTimeSeconds"
        tv_minefield_mines.text = "$minesCount"
    }

    private fun loadSounds() {
        soundExplosion = soundPool.load(this, R.raw.explosion_8bit, 1)
        soundWin = soundPool.load(this, R.raw.win_01, 1)
        soundFlagDrop = soundPool.load(this, R.raw.flag_drop, 1)
        soundTap = soundPool.load(this, R.raw.new_tap, 1)
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
                    if (!togglebutton_minefield_flag.isChecked) {
                        if (hostField == null) {
                            hostField = HostField(width, height, minesCount, x, y)
                        }
                        soundPoolWorker.playSound(soundPool, soundTap)

                        val keepGame = Saper().openCoordinate(x, y, hostField!!.content, userField)
                        if (!keepGame) {
                            performEndEvents(false)
                        } else {
                            /*если не проиграл - проверить, возможно теперь условия выполняются.*/
                            /*т.к. openCoordinate возвращает false только если проиграл и не отличает
                            * продолжение игры от победы*/
                            if (Saper().checkWinCondition(hostField!!.content, userField)) {
                                performEndEvents(true)
                            }
                        }
                        MinefieldAdapter().setupMinefield(userField, arrayButtonsField)
                    }
                    /*FLAG*/
                    if (togglebutton_minefield_flag.isChecked) {
                        if (hostField == null) {
                            hostField = HostField(width, height, minesCount, x, y)
                        }
                        soundPoolWorker.playSound(soundPool, soundFlagDrop)
                        val win = Saper().useFlagOnSpot(x, y, hostField!!.content, userField)
                        MinefieldAdapter().setupMinefield(userField, arrayButtonsField)
                        if (win) {
                            performEndEvents(true)
                        }
                    }
                }
            }
        }
    }

    private fun performEndEvents(result: Boolean) {
        val sound = if (result) soundWin else soundExplosion
        soundPoolWorker.playSound(soundPool, sound)
        tv_minefield_seconds.postDelayed({
            intentToResultActivity(result)
            timeWorker.stopCountDownTimer(countDownTimer)
        }, 500)
    }

    override fun intentToResultActivity(result: Boolean) {
        val mIntent = Intent(this, GameResultsActivity::class.java)

        mIntent.putExtra(
            GameConstant().GAME_RESULT,
            result
        )
        mIntent.putExtra(
            GameConstant().EXTRA_WIDTH,
            tv_minefield_field_width.text.toString().toInt()
        )
        mIntent.putExtra(
            GameConstant().EXTRA_HEIGHT,
            tv_minefield_field_height.text.toString().toInt()
        )
        mIntent.putExtra(
            GameConstant().EXTRA_MINES_COUNT,
            tv_minefield_mines.text.toString().toInt()
        )
        if (gameTimerMilli == 0L) {
            mIntent.putExtra(
                GameConstant().EXTRA_GAME_TIME_MINUTES,
                tv_minefield_minutes.text.toString().toInt()
            )
            mIntent.putExtra(
                GameConstant().EXTRA_GAME_TIME_SECONDS,
                tv_minefield_seconds.text.toString().toInt()
            )
        } else {
            var seconds = gameTimeSeconds - tv_minefield_seconds.text.toString().toInt()
            var minutes = gameTimeMinutes - tv_minefield_minutes.text.toString().toInt()
            if (seconds < 0) {
                seconds += 60
                minutes--
            }
            mIntent.putExtra(
                GameConstant().EXTRA_GAME_TIME_SECONDS,
                seconds
            )
            mIntent.putExtra(
                GameConstant().EXTRA_GAME_TIME_MINUTES,
                minutes
            )
        }
        startActivity(mIntent)
        finish()
    }

    override fun textViewMinutesSetText(str: String) {
        tv_minefield_minutes.text = str
    }

    override fun textViewSecondsSetText(str: String) {
        tv_minefield_seconds.text = str
    }

    /**-----------------------overriding standard methods------------------------*/
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        tv_minefield_field_width.text =
            savedInstanceState.getInt(GameConstant().EXTRA_WIDTH).toString()
        tv_minefield_field_height.text =
            savedInstanceState.getInt(GameConstant().EXTRA_HEIGHT).toString()
        tv_minefield_mines.text =
            savedInstanceState.getInt(GameConstant().EXTRA_MINES_COUNT).toString()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(
            GameConstant().EXTRA_WIDTH,
            tv_minefield_field_width.text.toString().toInt()
        )
        outState.putInt(
            GameConstant().EXTRA_HEIGHT,
            tv_minefield_field_height.text.toString().toInt()
        )
        outState.putInt(
            GameConstant().EXTRA_MINES_COUNT,
            tv_minefield_mines.text.toString().toInt()
        )
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

    override fun onBackPressed() {
        timeWorker.stopCountDownTimer(countDownTimer)
        soundPool.release()
        finish()
    }

    override fun onDestroy() {
        timeWorker.stopCountDownTimer(countDownTimer)
        soundPool.release()
        super.onDestroy()
    }
}