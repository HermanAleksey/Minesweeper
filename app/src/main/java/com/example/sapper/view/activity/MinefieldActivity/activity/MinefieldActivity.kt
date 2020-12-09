package com.example.sapper.view.activity.MinefieldActivity.activity

import com.bsuir.saper.HostField
import com.bsuir.saper.Saper
import com.bsuir.saper.UserField
import android.content.Context
import android.content.Intent
import android.media.SoundPool
import android.os.*
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sapper.model.constant.GameConstant
import com.example.sapper.controller.logic.MinefieldAdapter
import com.example.sapper.R
import com.example.sapper.view.activity.GameResultsActivity
import com.example.sapper.view.activity.MinefieldActivity.IMinefieldActivity
import com.example.sapper.model.constant.Constant
import com.example.sapper.constant.entity.CasualGame
import com.example.sapper.constant.entity.CompanyGame
import com.example.sapper.constant.entity.Field
import com.example.sapper.constant.entity.Game
import com.example.sapper.controller.logic.SoundPoolWorker
import com.example.sapper.controller.logic.TimeWorker
import com.example.sapper.view.Utils
import kotlinx.android.synthetic.main.activity_minefield.*


class MinefieldActivity : AppCompatActivity(), IMinefieldActivity {
    var hostField: HostField? = null

    private lateinit var game: CasualGame
    private var gameId: Int = 0
    private lateinit var mode: String

    private var gameTimerMilli: Long = 0
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
        Utils.onActivityCreateSetTheme(this)
        setContentView(R.layout.activity_minefield)
        /*sound pool settings for diff versions*/
        soundPoolWorker = SoundPoolWorker()
        soundPool = soundPoolWorker.getSoundPool()
        loadSounds()

        /*For timer*/
        handler = Handler()
        timeWorker = TimeWorker(this, handler)

        /*getting data about game depends on game mode*/
        mode = intent.getStringExtra(Constant().EXTRA_GAME_MODE)
        when (mode) {
            Constant().EXTRA_GAME_MODE_COMPANY -> {
                val a = intent.getSerializableExtra(GameConstant().EXTRA_GAME_OBJECT) as CompanyGame
                game = a.toCasualGame()
                gameId = a.id
            }
            Constant().EXTRA_GAME_MODE_CREATIVE -> {
                game = intent.getSerializableExtra(GameConstant().EXTRA_GAME_OBJECT) as CasualGame
            }
        }

        startStopWatch()

        fillViewElements()

        /*Visual minefield (from buttons)*/
        val arrayButtonsField =
            MinefieldAdapter().createMinefield(
                game.field.width, game.field.height, linear_layout_minefield, this
            )

        configureSeekBar(arrayButtonsField)

        /*generating field only if first click can be on mine*/
        if (game.firstClickMine) {
            hostField = HostField(game.field.width, game.field.height, game.field.minesCount)
        }
        val userField = UserField(game.field.width, game.field.height, game.field.minesCount)

        MinefieldAdapter().setupMinefield(userField.content, arrayButtonsField)

        setOnClickListenerForField(
            arrayButtonsField,
            userField.content
        )
    }

    private fun startStopWatch() {
        /*stopwatch / timer starting*/
        gameTimerMilli = timeWorker.translateToMilli("${game.minutes}:${game.seconds}")
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
        tv_minefield_field_width.text = "${game.field.width}"
        tv_minefield_field_height.text = "${game.field.height}"
        tv_minefield_minutes.text = if (game.minutes < 10) {
            "0${game.minutes}"
        } else "${game.minutes}"
        tv_minefield_seconds.text = if (game.seconds < 10) {
            "0${game.seconds}"
        } else "${game.seconds}"
        tv_minefield_mines.text = "${game.field.minesCount}"
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
                            hostField = HostField(
                                game.field.width,
                                game.field.height,
                                game.field.minesCount,
                                x,
                                y
                            )
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
                            hostField = HostField(
                                game.field.width,
                                game.field.height,
                                game.field.minesCount,
                                x,
                                y
                            )
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
            Constant().EXTRA_GAME_MODE,
            mode
        )
        mIntent.putExtra(
            GameConstant().GAME_RESULT,
            result
        )
        val width = tv_minefield_field_width.text.toString().toInt()
        val height = tv_minefield_field_height.text.toString().toInt()
        val minesCount = tv_minefield_mines.text.toString().toInt()
        var timeMin: Int
        var timeSec: Int

        if (gameTimerMilli == 0L) {
            timeMin = tv_minefield_minutes.text.toString().toInt()
            timeSec = tv_minefield_seconds.text.toString().toInt()
        } else {
            timeMin = game.minutes - tv_minefield_minutes.text.toString().toInt()
            timeSec = game.seconds - tv_minefield_seconds.text.toString().toInt()
            if (timeSec < 0) {
                timeSec += 60
                timeMin -= 1
            }
        }

        val game = Game(Field(width, height, minesCount), timeMin, timeSec)

        mIntent.putExtra(GameConstant().EXTRA_GAME_OBJECT, game)
        mIntent.putExtra(GameConstant().EXTRA_GAME_ID, gameId)

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