package com.example.sapper.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
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

    fun pack(): CompanyGameDB {
        return CompanyGameDB(
            this.id, this.field.width,
            this.field.height, this.field.minesCount,
            this.minutes, this.seconds, this.completed
        )
    }
}
