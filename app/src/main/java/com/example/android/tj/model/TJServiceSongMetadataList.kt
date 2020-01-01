package com.example.android.tj.model

import com.example.android.tj.database.SongMetadata
import com.google.gson.Gson

class TJServiceSongMetadataList(val list: List<SongMetadata>) {

    override fun toString(): String {
        return Gson().toJson(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TJServiceSongMetadataList

        return list.size == other.list.size &&
               list.zip(other.list).all { (m1, m2) -> m1.id == m2.id }
    }

    fun fileNamesWithIdx(): List<String> {
        return IntRange(0, list.size - 1).map { i ->
            if (i == 0) {
                "Playing: ${list[i].title}"
            } else {
                "$i.${list[i].title}"

            }
        }
    }

    override fun hashCode(): Int {
        return list.hashCode()
    }

    companion object {

        fun fromJson(jsonStr: String): TJServiceSongMetadataList {
            return Gson().fromJson(jsonStr, TJServiceSongMetadataList::class.java)
        }
    }
}
