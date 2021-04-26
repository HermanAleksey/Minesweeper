package com.example.sapper.model.entity.local

import androidx.room.Entity
import androidx.room.PrimaryKey

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