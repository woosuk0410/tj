package com.example.android.tj.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.time.Instant

@Entity(tableName = "histories", primaryKeys = ["id", "played_at"])
data class History(
        val id: String,
        @ColumnInfo(name = "played_at")
        val playedAt: Instant
)