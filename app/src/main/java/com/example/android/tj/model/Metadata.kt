package com.example.android.tj.model

import com.google.gson.Gson

class Metadata(
        val md5Hash: String,
        var priority: Int,
        val name: String
) {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}
