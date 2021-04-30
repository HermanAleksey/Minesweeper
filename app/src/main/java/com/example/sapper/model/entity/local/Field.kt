package com.example.sapper.model.entity.local

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class Field(
    @SerializedName("width")
    @Expose
    val width: Int,
    @SerializedName("height")
    @Expose
    val height: Int,
    @SerializedName("minesCount")
    @Expose
    val minesCount: Int
): Serializable
