package com.example.sapper.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.sapper.R
import kotlinx.android.synthetic.main.activity_game_settings.*
import kotlinx.android.synthetic.main.activity_minefield.*

class MinefieldActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minefield)

        val width = intent.getIntExtra(CustomGameSettingsActivity().WIDTH_TAG, 0)
        val height = intent.getIntExtra(CustomGameSettingsActivity().HEIGHT_TAG, 0)
        val minesCount = intent.getIntExtra(CustomGameSettingsActivity().MINES_COUNT_TAG, 0)

        textview_minefield_field_width.text = "$width"
        textview_minefield_field_height.text = "$height"
        textview_minefield_mines_counter.text = "$minesCount"
    }

    fun createMinefield (width: Int, height: Int){
//        linear_layout_minefield.addView
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