package com.example.android.tj.service

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager

interface TJServiceUtil {
    fun announceBroadcast(context: Context, intent: Intent) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }
}