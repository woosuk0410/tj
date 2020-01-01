package com.example.android.tj.model

import com.google.gson.Gson

class TJServiceStatus(
        val duration: Int, val currentPosition: Int,
        val nowPlaying: String, val isPlaying: Boolean, val md5: String) {

    override fun toString(): String {
        return Gson().toJson(this)
    }

    companion object {

        fun fromJson(jsonStr: String): TJServiceStatus {
            return Gson().fromJson(jsonStr, TJServiceStatus::class.java)
        }
    }
}
