package com.example.sapper.entity

import java.io.Serializable

class CompanyGame(
    val id: Int,
    field: Field,
    minutes: Int,
    seconds: Int,
    var completed: Boolean
) : Game(field, minutes, seconds), Serializable {

    fun toCasualGame(): CasualGame {
        return CasualGame(this.field, this.minutes, this.seconds, false)
    }
}
