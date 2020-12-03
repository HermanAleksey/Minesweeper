package com.example.sapper.entity

//import androidx.room.Entity
//import androidx.room.PrimaryKey
import java.io.Serializable

//@Entity(tableName = "company_game")
class CompanyGame(
//    @PrimaryKey(autoGenerate = false)
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
