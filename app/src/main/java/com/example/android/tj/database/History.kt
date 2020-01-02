package com.example.android.tj.database

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "histories", primaryKeys = ["id", "played_at"])
data class History(
        val id: String,
        @ColumnInfo(name = "played_at")
        val playedAt: String // Instant.toString() result
)