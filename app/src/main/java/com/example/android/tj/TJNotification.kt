/*
 * Borrowed from:
 * https://github.com/googlecodelabs/musicplayer-devices/blob/master/final/src/main/java/com
 * /example/android/musicplayercodelab/MediaNotificationManager.java
 */
package com.example.android.tj

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.app.NotificationCompat
import com.example.android.tj.Constants.NOTIFICATION_CHANNEL_ID
import com.example.android.tj.activity.MainActivityV2
import java.util.*

class TJNotification internal constructor(
        private val nodes: Nodes, private val tjService: TJService) : BroadcastReceiver() {


    private val playAction: NotificationCompat.Action
    private val pauseAction: NotificationCompat.Action
    private val nextAction: NotificationCompat.Action
    private val prevAction: NotificationCompat.Action


    internal val notification: Notification
        get() {
            val notificationBuilder = NotificationCompat.Builder(
                    tjService,
                    NOTIFICATION_CHANNEL_ID)

            val bitmap = nodes.bitMap

            val intent = Intent(tjService, MainActivityV2::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(tjService, 0, intent, 0)

            return notificationBuilder
                    .addAction(prevAction)
                    .addAction(if (Nodes.player.isPlaying) pauseAction else playAction)
                    .addAction(nextAction)
                    .setStyle(
                            androidx.media.app.NotificationCompat.MediaStyle()
                                    .setMediaSession(tjService.mediaSession.sessionToken)
                                    .setShowActionsInCompactView(0, 1, 2)
                    )
                    .setSmallIcon(R.drawable.ic_music_note_black_24dp)
                    .setContentTitle(nodes.last?.title)
                    .setLargeIcon(bitmap)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .build()
        }

    init {

        val pkg = tjService.packageName
        val playIntent = PendingIntent.getBroadcast(
                tjService,
                REQUEST_CODE,
                Intent(ACTION_PLAY).setPackage(pkg),
                PendingIntent.FLAG_CANCEL_CURRENT)
        val pauseIntent = PendingIntent.getBroadcast(
                tjService,
                REQUEST_CODE,
                Intent(ACTION_PAUSE).setPackage(pkg),
                PendingIntent.FLAG_CANCEL_CURRENT)
        val nextIntent = PendingIntent.getBroadcast(
                tjService,
                REQUEST_CODE,
                Intent(ACTION_NEXT).setPackage(pkg),
                PendingIntent.FLAG_CANCEL_CURRENT)
        val prevIntent = PendingIntent.getBroadcast(
                tjService,
                REQUEST_CODE,
                Intent(ACTION_PREV).setPackage(pkg),
                PendingIntent.FLAG_CANCEL_CURRENT)

        playAction = NotificationCompat.Action(
                R.drawable.ic_notification_play,
                "Play",
                playIntent)
        pauseAction = NotificationCompat.Action(
                R.drawable.ic_notification_pause,
                "Pause",
                pauseIntent)
        prevAction = NotificationCompat.Action(
                R.drawable.ic_notification_previous,
                "Previous",
                prevIntent)
        nextAction = NotificationCompat.Action(
                R.drawable.ic_notification_next,
                "Next",
                nextIntent)

        val filter = IntentFilter()
        filter.addAction(ACTION_NEXT)
        filter.addAction(ACTION_PAUSE)
        filter.addAction(ACTION_PLAY)
        filter.addAction(ACTION_PREV)

        tjService.registerReceiver(this, filter)
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        when (Objects.requireNonNull(action)) {
            ACTION_PAUSE -> nodes.pause()
            ACTION_PLAY  -> nodes.play()
            ACTION_NEXT  -> nodes.next()
            ACTION_PREV  -> nodes.previous()
        }
    }

    companion object {

        private val REQUEST_CODE = 100

        private val ACTION_PAUSE = "com.example.android.tj.pause"
        private val ACTION_PLAY = "com.example.android.tj.play"
        private val ACTION_NEXT = "com.example.android.tj.next"
        private val ACTION_PREV = "com.example.android.tj.prev"
    }
}
