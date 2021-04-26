package com.example.sapper.model.entity.local

import java.io.Serializable

open class Game (
    val field: Field,
    val minutes: Int,
    val seconds: Int
): Serializable
