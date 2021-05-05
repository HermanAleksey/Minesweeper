package com.example.sapper.controller.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sapper.model.entity.local.CompanyGameDB

@Database(entities = arrayOf(CompanyGameDB::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getCompanyGameDao(): CompanyGameDAO

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            Log.e("TAG", "getDatabase: -----------------------", )
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

    fun callOnCreateDatabase(context: Context){
        Log.e("TAG", "callOnCreateDatabase: -----------------------", )
        val dao = Room.databaseBuilder(
            context,
            AppDatabase::class.java, "database-name"
        ).build().getCompanyGameDao()
        dao.deleteAll()
        dao.insert(CompanyGameDB(1, 4, 4, 2, 10, 0, false))
        dao.insert(CompanyGameDB(2, 4, 4, 3, 10, 0, false))
        dao.insert(CompanyGameDB(3, 4, 4, 4, 10, 0, false))

        dao.insert(CompanyGameDB(4, 6, 6, 4, 10, 0, false))
        dao.insert(CompanyGameDB(5, 6, 6, 6, 10, 0, false))
        dao.insert(CompanyGameDB(6, 6, 6, 10, 10, 0, false))
        dao.insert(CompanyGameDB(7, 6, 6, 12, 10, 0, false))

        dao.insert(CompanyGameDB(8, 8, 8, 10, 10, 0, false))
        dao.insert(CompanyGameDB(9, 8, 8, 14, 10, 0, false))
        dao.insert(CompanyGameDB(10, 8, 8, 16, 10, 0, false))
        dao.insert(CompanyGameDB(11, 8, 8, 18, 10, 0, false))
        dao.insert(CompanyGameDB(12, 8, 8, 20, 10, 0, false))

        dao.insert(CompanyGameDB(13, 10, 10, 20, 10, 0, false))
        dao.insert(CompanyGameDB(14, 10, 10, 24, 10, 0, false))
        dao.insert(CompanyGameDB(15, 10, 10, 30, 10, 0, false))
        dao.insert(CompanyGameDB(16, 10, 10, 30, 6, 0, false))
        dao.insert(CompanyGameDB(17, 10, 10, 32, 6, 0, false))
        dao.insert(CompanyGameDB(18, 10, 10, 34, 6, 0, false))
        dao.insert(CompanyGameDB(19, 10, 10, 36, 6, 0, false))
        dao.insert(CompanyGameDB(20, 10, 10, 40, 6, 0, false))
        dao.insert(CompanyGameDB(21, 10, 10, 40, 4, 0, false))
    }
}