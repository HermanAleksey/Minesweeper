package com.example.sapper.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.sapper.entity.CompanyGameDB

@Dao
interface CompanyGameDAO {

    @Insert
    fun insert(game: CompanyGameDB)

    @Update
    fun update(game: CompanyGameDB)

    @Delete
    fun delete(game: CompanyGameDB)

    @Query("DELETE FROM company_level")
    fun deleteAll()

    @Query("SELECT * FROM company_level WHERE id = (:id)")
    fun get(id: Int): LiveData<List<CompanyGameDB>>

    @Query("SELECT * FROM company_level")
    fun getAll(): List<CompanyGameDB>

}