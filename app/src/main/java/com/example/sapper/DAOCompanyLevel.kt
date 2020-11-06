package com.example.sapper

import android.database.sqlite.SQLiteDatabase
import android.util.Log

class DAOCompanyLevel(private val db: SQLiteDatabase) {

    fun getAllCompanyLevels(): List<CompanyLevel> {
        val cursor = db.rawQuery("""select * from COMPANY_LEVEL""", null)
        val list = mutableListOf<CompanyLevel>()
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val height = cursor.getInt(1)
                val width = cursor.getInt(2)
                val minesCount = cursor.getInt(3)
                val minutes = cursor.getInt(4)
                val seconds = cursor.getInt(5)
                val completed = (cursor.getInt(6) != 0)

                val companyLevel: CompanyLevel =
                    CompanyLevel(id, height, width, minesCount, minutes, seconds, completed)
                list.add(companyLevel)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun getCompanyLevelById(id: Int): CompanyLevel? {
        val cursor = db.rawQuery("""select * from COMPANY_LEVEL where _id = $id""", null)
        val level = if (cursor.moveToFirst()) {
            val _id = cursor.getInt(0)
            val height = cursor.getInt(1)
            val width = cursor.getInt(2)
            val minesCount = cursor.getInt(3)
            val minutes = cursor.getInt(4)
            val seconds = cursor.getInt(5)
            val completed = (cursor.getInt(6) != 0)

            CompanyLevel(_id, height, width, minesCount, minutes, seconds, completed)
        } else null
        cursor.close()
        return level
    }

    fun insertCompanyLevel(companyLevel: CompanyLevel) {
        db.execSQL(
            """insert into COMPANY_LEVEL(
                    _id, height, width, mines_count,
                    minutes, seconds, completed)
                    values(
                    ${companyLevel.id},${companyLevel.height},
                    ${companyLevel.width},${companyLevel.minesCount},
                    ${companyLevel.minutes},${companyLevel.seconds},
                    ${if (companyLevel.completed) 1 else 0}
                    );"""
        )
    }

    fun removeAllCompanyLevels () {
        db.execSQL(
            """delete from COMPANY_LEVEL where _id > -1;"""
        )
    }

    fun getTheNumberOfRecords (): Int{
        val cursor = db.rawQuery(
            """select count(*) from COMPANY_LEVEL;""", null
        )
        cursor.moveToFirst()
        val amount = cursor.getInt(0)
        cursor.close()
        return amount
    }

}