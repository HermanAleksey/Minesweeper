package com.example.sapper.model.entity.local

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class Game (
    @SerializedName("field")
    @Expose
    val field: Field,
    @SerializedName("minutes")
    @Expose
    val minutes: Int,
    @SerializedName("seconds")
    @Expose
    val seconds: Int
): Serializable
