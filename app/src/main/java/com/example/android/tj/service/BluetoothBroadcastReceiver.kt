package com.example.android.tj.service

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import java.util.*

class BluetoothBroadcastReceiver internal constructor(private val nodes: Nodes) :
        BroadcastReceiver() {
    private val handler: Handler = Handler()

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action!!
        when (Objects.requireNonNull(action)) {
            BluetoothDevice.ACTION_ACL_CONNECTED    -> this.handler.postDelayed(
                    { this.nodes.play() }, 12000)
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> nodes.pause()
        }
    }
}
