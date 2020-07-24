package com.example.sapper

import Saper
import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout

class MinefieldAdapter {

    fun setupMinefield(
        minefield: Array<Array<Char>>,
        arrayButtonsField: Array<Array<Button>>
    ) {
        for (x in minefield.indices) {
            for (y in minefield[x].indices) {
                if (minefield[x][y].isDigit()) {
                    arrayButtonsField[x][y].setBackgroundResource(0)
                    arrayButtonsField[x][y].text = minefield[x][y].toString()
                    continue
                }
                when (minefield[x][y]) {
                    Saper().EMPTY_SPOT -> {
                        arrayButtonsField[x][y].text = "."
                        arrayButtonsField[x][y].setBackgroundResource(0)
                    }
                    Saper().UNKNOWN_SPOT -> {
                        arrayButtonsField[x][y]
                            .setBackgroundResource(R.drawable.ic_baseline_remove_red_eye_24)
                        //.setText("")
                    }
                    Saper().FLAG -> {
                        arrayButtonsField[x][y]
                            .setBackgroundResource(R.drawable.ic_baseline_flag_24)
                    }
                    Saper().MINE -> {
                        arrayButtonsField[x][y]
                            .setBackgroundResource(R.drawable.ic_baseline_brightness_7_24)
                    }
                }
            }
        }
    }

    fun openCell(
        x: Int, y: Int,
        hostField: Array<Array<Char>>,
        userField: Array<Array<Char>>,
        arrayButtonsField: Array<Array<Button>>
    ) {
        Saper().openCoordinate(x, y, hostField, userField)
        setupMinefield(userField, arrayButtonsField)
    }

    fun useFlagOnSpot(
        x: Int,
        y: Int,
        hostField: Array<Array<Char>>,
        userField: Array<Array<Char>>,
        arrayButtonsField: Array<Array<Button>>
    ) {
        Saper().useFlagOnSpot(x, y, hostField, userField)
        setupMinefield(userField, arrayButtonsField)
    }

    fun createMinefield(
        width: Int,
        height: Int,
        linearLayoutMinefield: LinearLayout,
        context: Context
    ): Array<Array<Button>> {
        val arrayButtonsField: Array<Array<Button>> =
            Array(width) { Array(height) { Button(context) } }

        for (y in 0 until height) {

            val linearLayoutString = LinearLayout(context)
            linearLayoutString.id //=

            val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            )

            linearLayoutMinefield.addView(linearLayoutString, layoutParams)

            for (x in 0 until width) {
                val button = Button(context)
//                button.id = "$width$height".toInt()
//                button.text = "$x$y"
                arrayButtonsField[x][y] = button

                val param: ViewGroup.LayoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1.0f
                )
                button.layoutParams = param
                linearLayoutString.addView(button, param)
            }

        }

        return arrayButtonsField
    }

}