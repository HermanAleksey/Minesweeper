package com.example.sapper.model.entity.local

import java.io.Serializable

class BluetoothGame(
    field: Field,
    minutes: Int,
    seconds: Int,
    val sameField: Boolean
) : Game(field, minutes, seconds), Serializable {
}