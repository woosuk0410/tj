package com.example.android.tj.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "song_metadata")
data class SongMetadata(
        @PrimaryKey
        val id: String,
        val title: String,
        val priority: Int = 0
)