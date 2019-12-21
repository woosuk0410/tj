package com.example.android.tj.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager

interface TJServiceBroadcastReceiver {

    val receiver: BroadcastReceiver

    // specifies which actions to receive
    val intentActions: List<String>

    // must be called in order to receive message
    fun registerBroadCastReceiver(context: Context?) {
        val intentFilter = IntentFilter()
        intentActions.forEach { intentFilter.addAction(it) }

        context?.let { LocalBroadcastManager.getInstance(it).registerReceiver(receiver, intentFilter) }
    }
}