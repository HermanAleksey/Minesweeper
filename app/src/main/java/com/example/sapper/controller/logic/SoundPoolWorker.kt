package com.example.sapper.controller.logic

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build

class SoundPoolWorker {

    fun getSoundPool(): SoundPool {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
    }

    fun playSound(soundPool: SoundPool, sound: Int) {
        soundPool.play(
            sound,
            1.0.toFloat(), 1.0.toFloat(),
            1, 0, 1.0.toFloat()
        )
    }
}