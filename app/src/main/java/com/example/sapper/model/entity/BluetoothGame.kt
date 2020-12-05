package com.example.sapper.constant.entity

import java.io.Serializable

class BluetoothGame(
    field: Field,
    minutes: Int,
    seconds: Int,
    val sameField: Boolean
) : Game(field, minutes, seconds), Serializable {
}