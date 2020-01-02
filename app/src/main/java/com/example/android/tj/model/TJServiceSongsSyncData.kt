package com.example.android.tj.model

import com.example.android.tj.database.History
import com.example.android.tj.database.SongMetadata
import com.google.gson.Gson

class TJServiceSongsSyncData(
        val list: List<SongMetadata>,
        val histories: Map<String, List<History>>) {

    fun toJsonString(): String {
        return Gson().toJson(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TJServiceSongsSyncData

        val listEqual = list.size == other.list.size &&
                        list.zip(other.list).all { (m1, m2) -> m1.id == m2.id }

        val historyEqual = histories.size == other.histories.size && histories.asSequence().all { entry ->
            val id = entry.key
            val histories = entry.value
            val otherHistories = other.histories.getOrElse(id) { emptyList() }
            otherHistories.size == histories.size && histories.all { history ->
                val otherHistory = otherHistories
                        .find { otherHistory -> otherHistory.id == history.id }
                if (otherHistory == null) false else otherHistory.playedAt == history.playedAt
            }
        }

        return listEqual && historyEqual
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
        return list.hashCode() + histories.hashCode()
    }

    companion object {

        fun fromJson(jsonStr: String): TJServiceSongsSyncData {
            return Gson().fromJson(jsonStr, TJServiceSongsSyncData::class.java)
        }
    }
}
