package com.example.android.tj.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface SongMetadataDao {

    @Query("SELECT * FROM song_metadata")
    fun getAll(): List<SongMetadata>

    @Insert(onConflict = REPLACE)
    fun insert(metadata: SongMetadata)
}