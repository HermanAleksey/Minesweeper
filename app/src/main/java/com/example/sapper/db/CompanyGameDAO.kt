//package com.example.sapper.db
//
//import androidx.lifecycle.LiveData
//import androidx.room.*
//import com.example.sapper.entity.CompanyGame
//
//@Dao
//interface CompanyGameDAO {
//
//    @Insert
//    fun insert(game: CompanyGame)
//
//    @Update
//    fun update(game: CompanyGame)
//
//    @Delete
//    fun delete(game: CompanyGame)
//
//    @Query("SELECT * FROM company_game WHERE id = (:id)")
//    fun get(id: Int): LiveData<List<CompanyGame>>
//
//    @Query("SELECT * FROM company_game")
//    fun getAll(): List<CompanyGame>
//
//}