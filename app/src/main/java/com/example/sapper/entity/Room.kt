package com.example.sapper.entity

import java.io.Serializable

data class Room(
    val width: Int,
    val height: Int,
    val minesCount: Int,
    val minutes: Int,
    val seconds: Int,
    val sameField: Boolean
) : Serializable {
}
