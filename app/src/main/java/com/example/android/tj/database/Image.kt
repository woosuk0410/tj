package com.example.android.tj.database

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "images", primaryKeys = ["song_id", "frame_order"])
data class Image(
        @ColumnInfo(name = "song_id")
        val songId: String,
        @ColumnInfo(name = "frame_order")
        val frameOrder: Int,
        @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
        val data: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Image

        if (songId != other.songId) return false
        if (frameOrder != other.frameOrder) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = songId.hashCode()
        result = 31 * result + frameOrder
        result = 31 * result + data.contentHashCode()
        return result
    }
}