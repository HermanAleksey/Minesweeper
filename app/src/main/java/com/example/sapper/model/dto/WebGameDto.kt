package com.example.sapper.model.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class WebGameDto(
    @SerializedName("width")
    @Expose
    val width: Int,
    @SerializedName("height")
    @Expose
    val height: Int,
    @SerializedName("minesCount")
    @Expose
    val minesCount: Int,
    @SerializedName("minutes")
    @Expose
    val minutes: Int,
    @SerializedName("seconds")
    @Expose
    val seconds: Int,
    @SerializedName("sameField")
    @Expose
    val sameField: Boolean
) : Serializable {

    override fun toString(): String {
        return "WebGameDto(width=$width, height=$height, minesCount=$minesCount, minutes=$minutes, seconds=$seconds, sameField=$sameField)"
    }
}