package com.example.android.tj.activity

import android.app.Activity
import android.content.Intent
import com.example.android.tj.Constants
import com.example.android.tj.database.SongMetadata
import com.example.android.tj.model.TJServiceCommand
import com.example.android.tj.service.TJService

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
        intent.putExtra(
                Constants.SERVICE_CMD, TJServiceCommand(Constants.SERVICE_CMD_PLAY_FROM_HASH, hash)
                .toString())
        activity?.startService(intent)
    }

    fun queryMetadataByHash(activity: Activity?, hash: String) {
        val cmd = TJServiceCommand(Constants.SERVICE_QUERY_METADATA_BY_HASH, hash)
        sendCmdToTJService(activity, cmd)
    }

    fun patchInMemoryMetadata(activity: Activity?, metadata: SongMetadata) {
        val cmd = TJServiceCommand(Constants.SERVICE_PATCH_METADATA, metadata.toString())
        sendCmdToTJService(activity, cmd)
    }

    fun sendSearchQuery(activity: Activity?, query: String) {
        val cmd = TJServiceCommand(Constants.SERVICE_QUERY_SEARCH, query)
        sendCmdToTJService(activity, cmd)
    }

    fun addToSelectedList(activity: Activity?, metadata: SongMetadata) {
        val cmd = TJServiceCommand(Constants.SERVICE_ADD_TO_SELECTED_LIST, metadata.toString())
        sendCmdToTJService(activity, cmd)
    }
}