package com.example.sapper.model.entity.local

import java.io.Serializable

open class Field(
    val width: Int,
    val height: Int,
    val minesCount: Int
): Serializable
