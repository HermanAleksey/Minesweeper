package com.example.sapper.controller.db

import androidx.room.*
import com.example.sapper.model.entity.local.CompanyGameDB

@Dao
interface CompanyGameDAO {

    @Insert
    fun insert(game: CompanyGameDB)

    @Update
    fun update(game: CompanyGameDB)

    @Query("UPDATE company_level SET completed = 1 where id = (:id)")
    fun setCompleted(id: Int)

    @Delete
    fun delete(game: CompanyGameDB)

    @Query("DELETE FROM company_level")
    fun deleteAll()

    @Query("SELECT * FROM company_level WHERE id = (:id)")
    fun get(id: Int): CompanyGameDB

    @Query("SELECT * FROM company_level")
    fun getAll(): List<CompanyGameDB>

    @Query("SELECT COUNT(*) FROM company_level")
    fun getCount(): Int

}