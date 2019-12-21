package com.example.android.tj.activity

import android.app.Activity
import android.content.Intent
import com.example.android.tj.Constants
import com.example.android.tj.TJService
import com.example.android.tj.model.TJServiceCommand

interface TJServiceUtil {

    fun sendCmdToTJService(activity: Activity?, vararg cmds: TJServiceCommand) {
        cmds.forEach { cmd ->
            run {
                val intent = activity?.let { Intent(it, TJService::class.java) }
                intent?.putExtra(Constants.SERVICE_CMD, cmd.toString())
                activity?.let { a -> intent?.let { a.startService(it) } }
            }
        }
    }

    fun playFromHash(activity: Activity?, hash: String) {
        val intent = Intent(activity, TJService::class.java)
        intent.putExtra(Constants.SERVICE_CMD, TJServiceCommand(Constants.SERVICE_CMD_PLAY_FROM_HASH, hash)
                .toString())
        activity?.startService(intent)
    }
}