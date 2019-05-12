package com.example.android.tj.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryDao {

    @Query("SELECT * FROM histories WHERE id=:id")
    fun getById(id: String): List<History>

    @Query("SELECT * FROM histories")
    fun getAll(): List<History>

    @Insert
    fun insert(history: History)
}