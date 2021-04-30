package com.example.sapper.model.entity.local

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class MultiplayerGame(
    field: Field,
    minutes: Int,
    seconds: Int,
    @SerializedName("sameField")
    @Expose
    val sameField: Boolean
) : Game(field, minutes, seconds), Serializable {
}