package com.example.sapper.constant.entity

import java.io.Serializable

open class Game (
    val field: Field,
    val minutes: Int,
    val seconds: Int
): Serializable
