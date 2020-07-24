package com.example.sapper.activity

import Saper
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.example.sapper.MinefieldAdapter
import com.example.sapper.R
import kotlinx.android.synthetic.main.activity_game_settings.*
import kotlinx.android.synthetic.main.activity_minefield.*

class MinefieldActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minefield)

        val width = intent.getIntExtra(
            CustomGameSettingsActivity().WIDTH_TAG, 0
        )
        val height = intent.getIntExtra(
            CustomGameSettingsActivity().HEIGHT_TAG, 0
        )
        val minesCount = intent.getIntExtra(
            CustomGameSettingsActivity().MINES_COUNT_TAG, 0
        )

        textview_minefield_field_width.text = "$width"
        textview_minefield_field_height.text = "$height"
        textview_minefield_mines_counter.text = "$minesCount"

        val linearLayoutMinefield =
            findViewById<LinearLayout>(R.id.linear_layout_minefield)

        val arrayButtonsField =
            MinefieldAdapter().createMinefield(
                width, height, linearLayoutMinefield, this
            )

        val hostField =
            Saper().generateHostMinefield(width, height, minesCount)
        val userField =
            Saper().generateUserMinefield(hostField)

        MinefieldAdapter().setupMinefield(userField, arrayButtonsField)


        togglebutton_minefield_open
            .setOnClickListener(onToggleButtonClickListener)
        togglebutton_minefield_flag
            .setOnClickListener(onToggleButtonClickListener)

        setOnClickListenerForField(arrayButtonsField, hostField, userField)
    }

    private fun setOnClickListenerForField(
        arrayButtonsField: Array<Array<Button>>,
        hostField: Array<Array<Char>>,
        userField: Array<Array<Char>>
    ) {
        for (y in arrayButtonsField.indices) {
            for (x in arrayButtonsField[y].indices) {
                arrayButtonsField[x][y].setOnClickListener {
                    if (togglebutton_minefield_open.isChecked) {
                        val loose = Saper().openCoordinate(x, y, hostField, userField)
                        if (!loose) {
                            Toast.makeText(this, "You проиграл", Toast.LENGTH_LONG).show()
                        }
                        MinefieldAdapter().setupMinefield(userField, arrayButtonsField)
                    }
                    if (togglebutton_minefield_flag.isChecked) {
                        val win = Saper().useFlagOnSpot(x, y, hostField, userField)
                        MinefieldAdapter().setupMinefield(userField, arrayButtonsField)
                        if (win){
                            startActivity(Intent(this,MainActivity::class.java))
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
            savedInstanceState.getInt(CustomGameSettingsActivity().WIDTH_TAG).toString()
        textview_minefield_field_height.text =
            savedInstanceState.getInt(CustomGameSettingsActivity().HEIGHT_TAG).toString()
        textview_minefield_mines_counter.text =
            savedInstanceState.getInt(CustomGameSettingsActivity().MINES_COUNT_TAG).toString()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(
            CustomGameSettingsActivity().WIDTH_TAG,
            textview_minefield_field_width.text.toString().toInt()
        )
        outState.putInt(
            CustomGameSettingsActivity().HEIGHT_TAG,
            textview_minefield_field_height.text.toString().toInt()
        )
        outState.putInt(
            CustomGameSettingsActivity().MINES_COUNT_TAG,
            textview_minefield_mines_counter.text.toString().toInt()
        )
    }
}