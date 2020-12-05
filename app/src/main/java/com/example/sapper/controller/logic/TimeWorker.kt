package com.example.sapper.controller.logic

import android.os.CountDownTimer
import android.os.Handler
import android.os.SystemClock
import android.widget.TextView
import com.example.sapper.view.activity.MinefieldActivity.IMinefieldActivity

class TimeWorker(private val interf: IMinefieldActivity, private val handler: Handler) {

    var millisecondTime: Long = 0
    var timeBuff: Long = 0
    var updateTime: Long = 0
    var seconds: Int = 0
    var minutes: Int = 0
    var milliSeconds: Int = 0
    fun getStopWatchRunnable (startTime: Long): Runnable {
        return object : Runnable {
            override fun run() {
                millisecondTime = SystemClock.uptimeMillis() - startTime
                updateTime = timeBuff + millisecondTime
                seconds = (updateTime / 1000).toInt()
                minutes = seconds / 60
                seconds %= 60
                milliSeconds = (updateTime % 1000).toInt()
                interf.textViewSecondsSetText("${
                    if (seconds < 10) {
                        "0$seconds"
                    } else seconds
                }")
                interf.textViewMinutesSetText("${
                    if (minutes < 10) {
                        "0$minutes"
                    } else minutes
                }")
                handler.postDelayed(this, 0)
            }
        }
    }

    fun translateToMilli(str: String): Long {
        val hours = str.substringBefore(":").toInt()
        val minutes = str.substringAfter(":").toInt()

        return ((hours * 60) + minutes) * 1000.toLong()
    }

    fun stopCountDownTimer(countDownTimer: CountDownTimer?){
        countDownTimer?.cancel()
    }

    fun getCountDownTimer(
        tv_minefield_minutes: TextView,
        tv_minefield_seconds: TextView,
        gameTimerMilli: Long
    ): CountDownTimer {
        return object : CountDownTimer(gameTimerMilli, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (tv_minefield_seconds.text != "00") {

                    tv_minefield_seconds.text =
                        if (tv_minefield_seconds.text.toString().toInt() < 10) {
                            "0${tv_minefield_seconds.text.toString().toInt() - 1}"
                        } else "${tv_minefield_seconds.text.toString().toInt() - 1}"
                } else {
                    tv_minefield_minutes.text =
                        if (tv_minefield_minutes.text.toString().toInt() < 10) {
                            "0${tv_minefield_minutes.text.toString().toInt() - 1}"
                        } else "${tv_minefield_minutes.text.toString().toInt() - 1}"
                    tv_minefield_seconds.text = "59"
                }
            }

            override fun onFinish() {
                interf.intentToResultActivity(false)
            }
        }
    }
}