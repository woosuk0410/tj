package com.example.android.tj

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
import android.util.Log
import android.util.Pair
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.android.tj.Constants.NOTIFICATION_CHANNEL_ID
import com.example.android.tj.Constants.NOTIFICATION_ID
import com.example.android.tj.Constants.SERVICE_CMD
import com.example.android.tj.database.SongMetadata
import com.example.android.tj.model.TJServiceCommand
import com.example.android.tj.model.TJServiceSearchResult
import com.example.android.tj.model.TJServiceStatus
import com.google.gson.Gson

class TJService : Service() {


    private lateinit var nodes: Nodes
    private lateinit var receiver: BroadcastReceiver
    internal lateinit var mediaSession: MediaSessionCompat
    private lateinit var notificationManager: NotificationManager

    private lateinit var serviceHandler: ServiceHandler
    private lateinit var mediaMetadataHandler: Handler

    private val currentStatus: TJServiceStatus
        get() {
            try {
                val fileNames = IntRange(1, nodes.nodes.size).map { i ->
                    "$i.${nodes.nodes[i - 1].title}"
                }
                val duration = Nodes.player.duration
                val curPos = Nodes.player.currentPosition
                val nowPlaying = nodes.nodes.last().title
                val isPlaying = Nodes.player.isPlaying
                val md5 = nodes.nodes.last().id
                return TJServiceStatus(fileNames, duration, curPos, nowPlaying, isPlaying, md5)
            } catch (e: Exception) {
                Log.e("TJService", "Exception when generating currentStatus ${e}")
                return TJServiceStatus(emptyList(), 0, 0, "", false, "")
            }

        }

    private fun initNotificationManager() {
        val channelName = "TJService"
        val chan = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_LOW)
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
        mediaSession = MediaSessionCompat(this@TJService, TAG)
        mediaSession.setCallback(BluetoothButtonCallback(nodes))
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mediaSession.isActive = true
    }

    private fun initServiceHandler() {
        val thread = HandlerThread("ServiceStartArguments",
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
                if (nodes.nodes.isEmpty()) { //TODO: better way to check init
                    return
                }

                val bitmap = nodes.bitMap

                // MediaMetadataCompat
                val builder = MediaMetadataCompat.Builder()
                builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, nodes.currentNode()
                        .title.replace(".aac", ""))
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, nodes
                                .currentNode().title)
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, Nodes.player
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
                        .setActions(actions).setState(state, Nodes.player.currentPosition.toLong(),
                                1.0f,
                                SystemClock.elapsedRealtime())
                mediaSession.setPlaybackState(stateBuilder.build())

                mediaSession.isActive = true
                mediaMetadataHandler.postDelayed(this, 5000)
            }
        }, 5000)
    }

    private fun getSearchResult(query: String): TJServiceSearchResult {
        val candidates = nodes.nodes.filter { n -> n.title.contains(query) }

        val pairs = candidates.map { candidate -> Pair.create(candidate.id, candidate.title) }

        return TJServiceSearchResult(pairs)
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
                Constants.SERVICE_CMD_PLAY -> nodes.play()
                Constants.SERVICE_CMD_NEXT -> nodes.next()
                Constants.SERVICE_CMD_PREVIOUS -> nodes.previous()
                Constants.SERVICE_CMD_PLAY_FROM -> nodes.playFromLocation(msg.arg1)
                Constants.SERVICE_CMD_PLAY_FROM_HASH -> nodes.playFromHash(msg.obj as String)
                Constants.SERVICE_CMD_PAUSE -> nodes.pause()
                Constants.SERVICE_CMD_SEEK -> Nodes.player.seekTo(msg.arg1)
                Constants.SERVICE_CMD_PRIORITY_SHUFFLE -> {
                    nodes.priorityShuffle()
                    nodes.next()
                }
                Constants.SERVICE_CMD_SHUFFLE -> {
                    nodes.shuffle()
                    nodes.next()
                }
                Constants.SERVICE_CMD_SORT -> {
                    nodes.sortByTitle()
                    nodes.next()
                }
                Constants.SERVICE_QUERY_METADATA -> {
                    val intent = Intent(Constants.SERVICE_ANSWER)
                    val metadata = nodes.nodes[msg.arg1]
                    intent.putExtra(Constants.SERVICE_ANSWER_METADATA, Gson().toJson(metadata))
                    LocalBroadcastManager.getInstance(this@TJService).sendBroadcast(intent)
                }
                Constants.SERVICE_QUERY_METADATA_BY_HASH -> {
                    val intent = Intent(Constants.SERVICE_ANSWER)
                    val metadata = nodes.getNodeByHash(msg.obj as String)
                    intent.putExtra(Constants.SERVICE_ANSWER_METADATA, Gson().toJson(metadata))
                    LocalBroadcastManager.getInstance(this@TJService).sendBroadcast(intent)
                }
                Constants.SERVICE_PATCH_METADATA -> {
                    nodes.updateMetadata(msg.obj as SongMetadata)
                }
                Constants.SERVICE_QUERY_SEARCH -> {
                    val result = getSearchResult(msg.obj as String)
                    val intent = Intent(Constants.SERVICE_ANSWER)
                    intent.putExtra(Constants.SERVICE_ANSWER_SEARCH, result.toString())
                    LocalBroadcastManager.getInstance(this@TJService).sendBroadcast(intent)
                }
                Constants.SERVICE_CMD_SYNC -> {
                }
                else -> {
                }
            }
            val intent = Intent(Constants.SERVICE_RESULT)
            intent.putExtra(Constants.SERVICE_RESULT_STATUS, currentStatus.toString())
            LocalBroadcastManager.getInstance(this@TJService).sendBroadcast(intent)
            notificationManager.notify(NOTIFICATION_ID, nodes.notification)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val cmd = TJServiceCommand.fromJson(intent.getStringExtra(SERVICE_CMD))
        val msg = serviceHandler.obtainMessage()
        msg.what = cmd.cmdCode
        msg.arg1 = cmd.arg1 //TODO: used in play from, seek to, and metadata query

        if (cmd.cmdCode == Constants.SERVICE_PATCH_METADATA) {
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
