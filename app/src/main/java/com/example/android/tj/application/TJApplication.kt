package com.example.android.tj.application

import android.app.Application

class TJApplication : Application() {

    companion object {
        lateinit var instance: Application
    }

    init {
        instance = this
    }
}