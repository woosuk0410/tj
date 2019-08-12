package com.example.android.tj.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson

@Entity(tableName = "song_metadata")
data class SongMetadata(
        @PrimaryKey
        val id: String,
        val title: String,
        var priority: Int = 0
) {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}