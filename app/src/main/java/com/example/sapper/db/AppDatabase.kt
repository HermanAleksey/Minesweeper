package com.example.sapper.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.sapper.constant.entity.CompanyGameDB

@Database(entities = arrayOf(CompanyGameDB::class), version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getCompanyGameDao(): CompanyGameDAO
}