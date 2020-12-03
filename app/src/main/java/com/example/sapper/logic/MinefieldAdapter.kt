package com.example.sapper.logic

import com.bsuir.saper.Saper
import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.example.sapper.R

class MinefieldAdapter {

    /*Установка видимого минного поля в соответствии с переданным массивом*/
    fun setupMinefield(
        minefield: Array<Array<Char>>,
        arrayButtonsField: Array<Array<Button>>
    ) {
        for (x in minefield.indices) {
            for (y in minefield[x].indices) {
                if (minefield[x][y].isDigit()) {
                    when (minefield[x][y]){
                        '0' -> {
                            arrayButtonsField[x][y].text = "0"
                            arrayButtonsField[x][y].setBackgroundResource(R.drawable.number_0)
                        }
                        '1' -> {
                            arrayButtonsField[x][y].text = "1"
                            arrayButtonsField[x][y].setBackgroundResource(R.drawable.number_1)
                        }
                        '2' -> {
                            arrayButtonsField[x][y].text = "2"
                            arrayButtonsField[x][y].setBackgroundResource(R.drawable.number_2)
                        }
                        '3' -> {
                            arrayButtonsField[x][y].text = "3"
                            arrayButtonsField[x][y].setBackgroundResource(R.drawable.number_3)
                        }
                        '4' -> {
                            arrayButtonsField[x][y].text = "4"
                            arrayButtonsField[x][y].setBackgroundResource(R.drawable.number_4)
                        }
                        '5' -> {
                            arrayButtonsField[x][y].text = "5"
                            arrayButtonsField[x][y].setBackgroundResource(R.drawable.number_5)
                        }
                        '6' -> {
                            arrayButtonsField[x][y].text = "6"
                            arrayButtonsField[x][y].setBackgroundResource(R.drawable.number_6)
                        }
                        '7' -> {
                            arrayButtonsField[x][y].text = "7"
                            arrayButtonsField[x][y].setBackgroundResource(R.drawable.number_7)
                        }
                        '8' -> {
                            arrayButtonsField[x][y].text = "8"
                            arrayButtonsField[x][y].setBackgroundResource(R.drawable.number_8)
                        }
                    }
                }
                when (minefield[x][y]) {
                    Saper().EMPTY_SPOT -> {
                        arrayButtonsField[x][y].text = "."
                        arrayButtonsField[x][y].setBackgroundResource(0)
                    }
                    Saper().UNKNOWN_SPOT -> {
                        arrayButtonsField[x][y]
                            .setBackgroundResource(R.drawable.ic_baseline_remove_red_eye_24)
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

    /*generating visual (buttons) minefield*/
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

            val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            linearLayoutMinefield.addView(linearLayoutString, layoutParams)

            for (x in 0 until width) {
                val button = Button(context)
                arrayButtonsField[x][y] = button

                val param: ViewGroup.LayoutParams = LinearLayout.LayoutParams(
                    90,
                    90
                )
                button.layoutParams = param
                button.minWidth
                button.textSize = 0f
                linearLayoutString.addView(button, param)
            }
        }

        return arrayButtonsField
    }

}