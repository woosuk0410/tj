package com.example.android.tj.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.example.android.tj.Constants
import com.example.android.tj.Constants.NOTIFICATION_CHANNEL_ID
import com.example.android.tj.Constants.NOTIFICATION_ID
import com.example.android.tj.Constants.SERVICE_CMD
import com.example.android.tj.database.SongMetadata
import com.example.android.tj.model.CurrentListMode
import com.example.android.tj.model.TJServiceCommand
import com.google.gson.Gson

class TJService : Service(), TJServiceUtil {

    private lateinit var nodes: Nodes
    private lateinit var receiver: BroadcastReceiver
    internal lateinit var mediaSession: MediaSessionCompat
    private lateinit var notificationManager: NotificationManager

    private lateinit var serviceHandler: ServiceHandler
    private lateinit var mediaMetadataHandler: Handler

    private fun initNotificationManager() {
        val channelName = "TJService"
        val chan = NotificationChannel(
                NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_LOW)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(chan)
    }

    private fun initBluetoothBroadcastReceiver() {
        receiver = BluetoothBroadcastReceiver(nodes)
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        registerReceiver(receiver, filter)
    }

    private fun initMediaSession() {
        mediaSession = MediaSessionCompat(
                this@TJService,
                TAG)
        mediaSession.setCallback(
                BluetoothButtonCallback(nodes))
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mediaSession.isActive = true
    }

    private fun initServiceHandler() {
        val thread = HandlerThread(
                "ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND)
        thread.start()
        // Get the HandlerThread's Looper and use it for our Handler
        val mServiceLooper = thread.looper
        serviceHandler = ServiceHandler(mServiceLooper)
    }

    private fun initMediaMetadataHandler() {
        mediaMetadataHandler = Handler()

        mediaMetadataHandler.postDelayed(object : Runnable {
            override fun run() {
                if (nodes.currentList.isEmpty()) { //TODO: better way to check init
                    return
                }

                val bitmap = nodes.bitMap

                // MediaMetadataCompat
                val builder = MediaMetadataCompat.Builder()
                builder.putString(
                        MediaMetadataCompat.METADATA_KEY_TITLE, nodes.currentNode()
                        .title.replace(".aac", ""))
                        .putString(
                                MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, nodes
                                .currentNode().title)
                        .putLong(
                                MediaMetadataCompat.METADATA_KEY_DURATION, Nodes.player
                                .duration.toLong())
                        .putBitmap(MediaMetadataCompat.METADATA_KEY_ART, bitmap)
                mediaSession.setMetadata(builder.build())

                // PlaybackStateCompat
                var actions = (PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                        or PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
                        or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                if (Nodes.player.isPlaying) {
                    actions = actions or PlaybackStateCompat.ACTION_PAUSE
                }
                val state = if (Nodes.player.isPlaying)
                    PlaybackStateCompat
                            .STATE_PLAYING
                else
                    PlaybackStateCompat.STATE_PAUSED
                val stateBuilder = PlaybackStateCompat.Builder()
                        .setActions(actions).setState(
                                state, Nodes.player.currentPosition.toLong(),
                                1.0f,
                                SystemClock.elapsedRealtime())
                mediaSession.setPlaybackState(stateBuilder.build())

                mediaSession.isActive = true
                mediaMetadataHandler.postDelayed(this, 5000)
            }
        }, 5000)
    }

    override fun onCreate() {
        super.onCreate()

        nodes = Nodes(this@TJService)

        initMediaSession()
        initBluetoothBroadcastReceiver()
        initNotificationManager()
        startForeground(NOTIFICATION_ID, nodes.notification)
        initServiceHandler()
        initMediaMetadataHandler()
    }

    private inner class ServiceHandler internal constructor(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {

            when (msg.what) {
                Constants.SERVICE_CMD_PLAY               -> nodes.play()
                Constants.SERVICE_CMD_NEXT               -> nodes.next()
                Constants.SERVICE_CMD_PREVIOUS           -> nodes.previous()
                Constants.SERVICE_CMD_PLAY_FROM          -> nodes.playFromLocation(msg.arg1)
                Constants.SERVICE_CMD_PLAY_FROM_HASH     -> nodes.playFromHash(msg.obj as String)
                Constants.SERVICE_CMD_PAUSE              -> nodes.pause()
                Constants.SERVICE_CMD_SEEK               -> Nodes.player.seekTo(msg.arg1)
                Constants.SERVICE_CMD_PRIORITY_SHUFFLE   -> {
                    nodes.priorityShuffle()
                    nodes.playFromTop()
                }
                Constants.SERVICE_CMD_SHUFFLE            -> {
                    nodes.shuffle()
                    nodes.playFromTop()
                }
                Constants.SERVICE_CMD_SORT               -> {
                    nodes.sortByTitle()
                    nodes.playFromTop()
                }
                Constants.SERVICE_QUERY_METADATA_BY_HASH -> {
                    announceBroadcast(
                            this@TJService, nodes.searchByHashBroadcastIntent(msg.obj as String))
                }
                Constants.SERVICE_PATCH_METADATA         -> {
                    nodes.updateMetadata(msg.obj as SongMetadata)
                }
                Constants.SERVICE_ADD_TO_SELECTED_LIST   -> {
                    nodes.addToSelectedList(msg.obj as SongMetadata)
                }
                Constants.SERVICE_QUERY_SEARCH           -> {
                    announceBroadcast(
                            this@TJService,
                            nodes.searchByTitleBroadcastIntent(msg.obj as String))
                }
                Constants.SERVICE_CMD_SYNC_SONGS_DATA    -> {
                    announceBroadcast(this@TJService, nodes.songsSyncBroadcastIntent())
                }
                Constants.SERVICE_CLEAR_SELECTED_LIST    -> {
                    nodes.selectedList = emptyList()
                    announceBroadcast(this@TJService, nodes.songsSyncBroadcastIntent())
                }
                Constants.SERVICE_CMD_SWITCH_TARGET_LIST -> {
                    val targetMode = when (msg.arg1) {
                        CurrentListMode.Normal.value   -> CurrentListMode.Normal
                        CurrentListMode.Selected.value -> CurrentListMode.Selected
                        else                           -> throw RuntimeException(
                                "${msg.arg1} does not match ${CurrentListMode.Normal} or ${CurrentListMode.Selected}")
                    }
                    nodes.switchCurrentListMode(targetMode)
                }
                Constants.SERVICE_CMD_PLAY_FROM_TOP      -> {
                    nodes.playFromTop()
                }
                Constants.SERVICE_CMD_SYNC               -> {
                }
                else                                     -> {
                }
            }
            announceBroadcast(this@TJService, nodes.playingStatusBroadcastIntent())
            notificationManager.notify(NOTIFICATION_ID, nodes.notification)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val cmd = TJServiceCommand.fromJson(intent.getStringExtra(SERVICE_CMD))
        val msg = serviceHandler.obtainMessage()
        msg.what = cmd.cmdCode
        msg.arg1 = cmd.arg1 //TODO: used in play from, seek to, and songs list mode

        if (cmd.cmdCode == Constants.SERVICE_PATCH_METADATA
            || cmd.cmdCode == Constants.SERVICE_ADD_TO_SELECTED_LIST
        ) {
            msg.obj = Gson().fromJson(cmd.data, SongMetadata::class.java)
        } else if (cmd.cmdCode == Constants.SERVICE_QUERY_SEARCH) {
            msg.obj = cmd.data
        } else if (cmd.cmdCode == Constants.SERVICE_CMD_PLAY_FROM_HASH) {
            msg.obj = cmd.data
        } else if (cmd.cmdCode == Constants.SERVICE_QUERY_METADATA_BY_HASH) {
            msg.obj = cmd.data
        }

        serviceHandler.sendMessage(msg)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {


        private const val TAG = "TJService"
    }
}
