package com.example.sapper.activity.MinefieldActivity

import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

interface IMinefieldActivity {

//    fun fillViewElements()
//
//    fun loadSounds()

//    fun setOnClickListenerForField(
//        arrayButtonsField: Array<Array<Button>>,
//        userField: Array<Array<Char>>
//    )

//    fun performEndEvents(result: Boolean)
//
    fun intentToResultActivity(result: Boolean)

    fun textViewMinutesSetText(str: String)

    fun textViewSecondsSetText(str: String)

}