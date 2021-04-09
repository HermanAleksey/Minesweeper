package com.bsuir.herman.saper

import java.io.Serializable

data class UserField(
    val width: Int,
    val height: Int,
    val minesCount: Int
) : Serializable {

    //field for interacting with user
    var content: Array<Array<Char>> = Array(width) { Array(height) { Saper().UNKNOWN_SPOT } }

}