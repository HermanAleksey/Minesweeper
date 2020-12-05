package com.example.sapper.constant.entity

import java.io.Serializable

open class Field(
    val width: Int,
    val height: Int,
    val minesCount: Int
): Serializable
