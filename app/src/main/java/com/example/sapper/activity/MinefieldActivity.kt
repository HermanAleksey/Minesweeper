package com.example.sapper.activity

import HostField
import Saper
import UserField
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sapper.Constant
import com.example.sapper.MinefieldAdapter
import com.example.sapper.R
import kotlinx.android.synthetic.main.activity_minefield.*

class MinefieldActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minefield)

        /*getting data about game depends on game mode*/
        val gameMode = intent.getStringExtra(
            Constant().GAME_MODE
        )
        val height = intent.getIntExtra(
            Constant().HEIGHT_TAG, 0
        )
        val width = intent.getIntExtra(
            Constant().WIDTH_TAG, 0
        )
        val minesCount = intent.getIntExtra(
            Constant().MINES_COUNT_TAG, 0
        )
        val gameTime = intent.getStringExtra(
            Constant().GAME_TIME_TAG
        )
        val firstClickCanBeOnAMine = intent.getBooleanExtra(
            Constant().FIRST_CLICK_MINE_TAG, false
        )
        val minerPlayer: Int
        val closeAfterGame: Boolean
        if (gameMode == Constant().GAME_MODE_BLUETOOTH) {
            minerPlayer = intent.getIntExtra(
                Constant().WHO_MINER_TAG, 1
            )
            closeAfterGame = intent.getBooleanExtra(
                Constant().CLOSE_AFTER_GAME_TAG, false
            )
        }

        /*filling view*/
        textview_minefield_field_width.text = "$width"
        textview_minefield_field_height.text = "$height"
        textview_minefield_time_passed.text = gameTime
        textview_minefield_mines_count.text = "$minesCount"

        val linearLayoutMinefield =
            findViewById<LinearLayout>(R.id.linear_layout_minefield)

        /*Visual minefield (from buttons)*/
        val arrayButtonsField =
            MinefieldAdapter().createMinefield(
                width, height, linearLayoutMinefield, this
            )

        val hostField: HostField = HostField(width, height, minesCount)
        val userField: UserField = UserField(width, height, minesCount)
//        var HostField: Array<Array<Char>>? = null
//        val userField = Saper().(width, height)

        MinefieldAdapter().setupMinefield(userField.content, arrayButtonsField)

        togglebutton_minefield_open
            .setOnClickListener(onToggleButtonClickListener)
        togglebutton_minefield_flag
            .setOnClickListener(onToggleButtonClickListener)

        setOnClickListenerForField(
            arrayButtonsField, userField.content, hostField.content//, minesCount, firstClickCanBeOnAMine
        )

    }

    /*define how each cell gonna react to click with flag/open selected*/
    private fun setOnClickListenerForField(
        arrayButtonsField: Array<Array<Button>>,
        userField: Array<Array<Char>>,
        hostField: Array<Array<Char>>
//        ,        minesCount: Int,
//        firstClickCanBeOnAMine: Boolean
    ) {
        for (y in arrayButtonsField.indices) {
            for (x in arrayButtonsField[y].indices) {
                arrayButtonsField[x][y].setOnClickListener {

                    if (togglebutton_minefield_open.isChecked) {

//                        if (hostField == null) {
//
//                            val newField: Array<Array<Char>>
//                            if (!firstClickCanBeOnAMine) {
//
//                                newField = Saper().generateMinefield(
//                                    userField[0].size,
//                                    userField.size,
//                                    minesCount, x, y
//                                )
//                                Log.d("s", Saper().getFieldAsString(newField))
//                            } else {
//                                newField = Saper().generateMinefield(
//                                    userField[0].size,
//                                    userField.size,
//                                    minesCount
//                                )
//                            }
//
//                            setOnClickListenerForField(
//                                arrayButtonsField,
//                                userField,
//                                newField,
//                                minesCount,
//                                firstClickCanBeOnAMine
//                            )
//                            arrayButtonsField[x][y].callOnClick()
//
//                            return@setOnClickListener
//
//                        }

                        val keepGame = Saper().openCoordinate(x, y, hostField, userField)
                        if (!keepGame) {
                            Toast.makeText(this, "You проиграл", Toast.LENGTH_LONG).show()
                        } else {
                            /*если не проиграл - проверить, возможно теперь условия выполняются.*/
                            /*т.к. openCoordinate возвращает false только если проиграл и не отличает
                            * продолжение игры от победы*/
                            if (Saper().checkWinCondition(hostField,userField)){
                                startActivity(Intent(this, MainActivity::class.java))
                            }
                        }
                        MinefieldAdapter().setupMinefield(userField, arrayButtonsField)
                    }

                    if (togglebutton_minefield_flag.isChecked) {
                        val win = Saper().useFlagOnSpot(x, y, hostField, userField)
                        MinefieldAdapter().setupMinefield(userField, arrayButtonsField)
                        if (win) {
                            startActivity(Intent(this, MainActivity::class.java))
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
            savedInstanceState.getInt(Constant().WIDTH_TAG).toString()
        textview_minefield_field_height.text =
            savedInstanceState.getInt(Constant().HEIGHT_TAG).toString()
        textview_minefield_mines_count.text =
            savedInstanceState.getInt(Constant().MINES_COUNT_TAG).toString()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(
            Constant().WIDTH_TAG,
            textview_minefield_field_width.text.toString().toInt()
        )
        outState.putInt(
            Constant().HEIGHT_TAG,
            textview_minefield_field_height.text.toString().toInt()
        )
        outState.putInt(
            Constant().MINES_COUNT_TAG,
            textview_minefield_mines_count.text.toString().toInt()
        )
    }
}