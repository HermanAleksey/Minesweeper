package com.example.sapper.activity.MinefieldActivity

import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

interface IMinefieldActivity {

    fun intentToResultActivity(result: Boolean)

    fun textViewMinutesSetText(str: String)

    fun textViewSecondsSetText(str: String)

}