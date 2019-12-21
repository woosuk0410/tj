package com.example.android.tj.model

import android.util.Pair
import com.google.gson.Gson

@Deprecated("to be removed")
class TJServiceSearchResult(pairs: List<Pair<String, String>>) {
    var fileNames: List<String>
    var hashes: List<String>


    init {
        this.hashes = pairs.map { it.first }
        this.fileNames = pairs.map { it.second }
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }

    companion object {

        fun fromJson(jsonStr: String): TJServiceStatus {
            return Gson().fromJson(jsonStr, TJServiceStatus::class.java)
        }
    }
}
