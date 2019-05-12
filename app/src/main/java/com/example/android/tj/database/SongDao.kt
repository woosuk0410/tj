package com.example.android.tj.database

import androidx.room.Dao
import androidx.room.Query

@Dao
interface SongDao {

    @Query("SELECT * FROM songs WHERE id =:id")
    fun getById(id: String): Song
}