package com.example.android.tj.database

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ImageDao {

    @Query("SELECT * FROM images WHERE song_id=:songId")
    fun getBySongId(songId: String): List<Image>
}