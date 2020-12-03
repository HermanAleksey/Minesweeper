package com.example.sapper.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "company_level")
class CompanyGameDB(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val width: Int,
    val height: Int,
    val minesCount: Int,
    val minutes: Int,
    val seconds: Int,
    var completed: Boolean
) {
    fun unpack(): CompanyGame {
        return CompanyGame(
            this.id,
            Field(this.width, this.height, this.minesCount),
            this.minutes, this.seconds, this.completed
        )
    }
}