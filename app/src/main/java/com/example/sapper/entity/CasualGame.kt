package com.example.sapper.entity

import java.io.Serializable

class CasualGame(
    field: Field,
    minutes: Int,
    seconds: Int,
    val firstClickMine: Boolean
) : Game(field, minutes, seconds), Serializable