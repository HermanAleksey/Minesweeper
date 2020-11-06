package com.example.sapper.activity

import HostField
import Saper
import UserField
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.SystemClock
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.sapper.Constant
import com.example.sapper.GameConstant
import com.example.sapper.MinefieldAdapter
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

    lateinit var handler: Handler

    var millisecondTime: Long = 0
    var startTime: Long = 0
    var timeBuff: Long = 0
    var updateTime: Long = 0
    var seconds: Int = 0
    var minutes: Int = 0
    var milliSeconds: Int = 0
    var runnable: Runnable = object : Runnable {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minefield)

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

        val gameTimerMilli = translateToMilli("$gameTimeMinutes:$gameTimeSeconds")
        if (gameTimerMilli == 0L) {
            startTime = SystemClock.uptimeMillis()
            runnable.run()
        } else {
            object : CountDownTimer(gameTimerMilli, 1000) {
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
                    val mIntent = Intent(this@MinefieldActivity, GameResultsActivity::class.java)
                    mIntent.putExtra(GameConstant().GAME_RESULT, GameConstant().GAME_RESULT_DEFEAT)
                    startActivity(mIntent)
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
        textview_minefield_field_width.text = "$width"
        textview_minefield_field_height.text = "$height"
        tv_minefield_minutes.text = "$gameTimeMinutes"
        tv_minefield_seconds.text = "$gameTimeSeconds"
        textview_minefield_mines_count.text = "$minesCount"

        val linearLayoutMinefield =
            findViewById<LinearLayout>(R.id.linear_layout_minefield)

        /*Visual minefield (from buttons)*/
        val arrayButtonsField =
            MinefieldAdapter().createMinefield(
                width, height, linearLayoutMinefield, this
            )

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
                    if (togglebutton_minefield_open.isChecked) {

                        if (hostField == null) {
                            hostField = HostField(width, height, minesCount, x, y)
                        }

                        val keepGame = Saper().openCoordinate(x, y, hostField!!.content, userField)
                        if (!keepGame) {
                            val mIntent = Intent(this, GameResultsActivity::class.java)
                            mIntent.putExtra(
                                GameConstant().GAME_RESULT,
                                GameConstant().GAME_RESULT_DEFEAT
                            )
                            startActivity(mIntent)
                        } else {
                            /*если не проиграл - проверить, возможно теперь условия выполняются.*/
                            /*т.к. openCoordinate возвращает false только если проиграл и не отличает
                            * продолжение игры от победы*/
                            if (Saper().checkWinCondition(hostField!!.content, userField)) {
                                val mIntent = Intent(this, GameResultsActivity::class.java)
                                mIntent.putExtra(
                                    GameConstant().GAME_RESULT,
                                    GameConstant().GAME_RESULT_WIN
                                )
                                startActivity(mIntent)
                            }
                        }
                        MinefieldAdapter().setupMinefield(userField, arrayButtonsField)
                    }

                    if (togglebutton_minefield_flag.isChecked) {
                        val win = Saper().useFlagOnSpot(x, y, hostField!!.content, userField)
                        MinefieldAdapter().setupMinefield(userField, arrayButtonsField)
                        if (win) {
                            val mIntent = Intent(this, GameResultsActivity::class.java)
                            mIntent.putExtra(
                                GameConstant().GAME_RESULT,
                                GameConstant().GAME_RESULT_WIN
                            )
                            startActivity(mIntent)
                        }
                    }
                }
            }
        }
    }


    //чтобы одновременно включена была только 1 кнопка
    val onToggleButtonClickListener = View.OnClickListener {
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
        textview_minefield_field_width.text =
            savedInstanceState.getInt(GameConstant().WIDTH_TAG).toString()
        textview_minefield_field_height.text =
            savedInstanceState.getInt(GameConstant().HEIGHT_TAG).toString()
        textview_minefield_mines_count.text =
            savedInstanceState.getInt(GameConstant().MINES_COUNT_TAG).toString()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(
            GameConstant().WIDTH_TAG,
            textview_minefield_field_width.text.toString().toInt()
        )
        outState.putInt(
            GameConstant().HEIGHT_TAG,
            textview_minefield_field_height.text.toString().toInt()
        )
        outState.putInt(
            GameConstant().MINES_COUNT_TAG,
            textview_minefield_mines_count.text.toString().toInt()
        )
    }

    override fun onBackPressed() {
        finish()
    }
}