package com.example.sapper.logic

import android.os.CountDownTimer
import android.os.SystemClock
import android.widget.TextView
import com.example.sapper.IIntent

class TimeWorker(private val iIntent: IIntent) {

    fun translateToMilli(str: String): Long {
        val hours = str.substringBefore(":").toInt()
        val minutes = str.substringAfter(":").toInt()

        return ((hours * 60) + minutes) * 1000.toLong()
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
                iIntent.intentToResultActivity(false)
            }
        }
    }
}