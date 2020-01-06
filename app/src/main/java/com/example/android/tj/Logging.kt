package com.example.android.tj

import android.util.Log

interface Logging {
    fun log(msg: String) {
        Log.d(this::class.qualifiedName, msg)
    }
}