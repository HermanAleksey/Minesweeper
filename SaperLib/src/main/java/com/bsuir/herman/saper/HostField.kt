package com.bsuir.herman.saper

import java.io.Serializable

data class HostField(
    val width: Int,
    val height: Int,
    val minesCount: Int,
    val x: Int = -1,
    val y: Int = -1
) : Serializable {

    //for storing filled mine field
    val content: Array<Array<Char>> = Saper().generateMinefield(width, height, minesCount, x, y)

}