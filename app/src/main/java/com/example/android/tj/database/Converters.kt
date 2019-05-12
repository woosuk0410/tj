package com.example.android.tj.database

import androidx.room.TypeConverter
import java.time.Instant

class Converters {

    @TypeConverter
    fun fromInstant(instant: Instant): Long {
        return instant.epochSecond
    }

    @TypeConverter
    fun toInstant(timestamp: Long): Instant {
        return Instant.ofEpochMilli(timestamp)
    }
}