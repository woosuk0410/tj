/*
 * Borrowed from:
 * https://github.com/googlecodelabs/musicplayer-devices/blob/master/final/src/main/java/com
 * /example/android/musicplayercodelab/MediaNotificationManager.java
 */
package com.example.android.tj;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;

import java.util.Objects;

import static com.example.android.tj.Constants.NOTIFICATION_CHANNEL_ID;

public class TJNotification extends BroadcastReceiver {

    private TJService tjService;
    private Nodes nodes;

    private static final int REQUEST_CODE = 100;

    private static final String ACTION_PAUSE = "com.example.android.tj.pause";
    private static final String ACTION_PLAY = "com.example.android.tj.play";
    private static final String ACTION_NEXT = "com.example.android.tj.next";
    private static final String ACTION_PREV = "com.example.android.tj.prev";


    private final NotificationCompat.Action playAction;
    private final NotificationCompat.Action pauseAction;
    private final NotificationCompat.Action nextAction;
    private final NotificationCompat.Action prevAction;

    TJNotification(Nodes nodes, TJService ctx) {
        this.tjService = ctx;
        this.nodes = nodes;

        String pkg = tjService.getPackageName();
        PendingIntent playIntent =
                PendingIntent.getBroadcast(
                        tjService,
                        REQUEST_CODE,
                        new Intent(ACTION_PLAY).setPackage(pkg),
                        PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pauseIntent =
                PendingIntent.getBroadcast(
                        tjService,
                        REQUEST_CODE,
                        new Intent(ACTION_PAUSE).setPackage(pkg),
                        PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent nextIntent =
                PendingIntent.getBroadcast(
                        tjService,
                        REQUEST_CODE,
                        new Intent(ACTION_NEXT).setPackage(pkg),
                        PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent prevIntent =
                PendingIntent.getBroadcast(
                        tjService,
                        REQUEST_CODE,
                        new Intent(ACTION_PREV).setPackage(pkg),
                        PendingIntent.FLAG_CANCEL_CURRENT);

        playAction =
                new NotificationCompat.Action(
                        R.drawable.ic_notification_play,
                        "Play",
                        playIntent);
        pauseAction =
                new NotificationCompat.Action(
                        R.drawable.ic_notification_pause,
                        "Pause",
                        pauseIntent);
        prevAction =
                new NotificationCompat.Action(
                        R.drawable.ic_notification_previous,
                        "Next",
                        nextIntent);
        nextAction =
                new NotificationCompat.Action(
                        R.drawable.ic_notification_next,
                        "Previous",
                        prevIntent);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NEXT);
        filter.addAction(ACTION_PAUSE);
        filter.addAction(ACTION_PLAY);
        filter.addAction(ACTION_PREV);

        tjService.registerReceiver(this, filter);
    }


    Notification getNotification() {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(tjService,
                NOTIFICATION_CHANNEL_ID);

        Bitmap bitmap = nodes.getBitMap();

        Intent intent = new Intent(tjService, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(tjService, 0, intent, 0);

        return notificationBuilder
                .addAction(prevAction)
                .addAction(Nodes.player.isPlaying() ? pauseAction : playAction)
                .addAction(nextAction)
                .setStyle(
                        new android.support.v4.media.app.NotificationCompat.MediaStyle()
                                .setMediaSession(tjService.mediaSession.getSessionToken())
                                .setShowActionsInCompactView(0, 1, 2)
                )
                .setSmallIcon(R.drawable.ic_music_note_black_24dp)
                .setContentTitle(nodes.nodes.getLast().metadata.name)
                .setLargeIcon(bitmap)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        switch (Objects.requireNonNull(action)) {
            case ACTION_PAUSE:
                nodes.pause();
                break;
            case ACTION_PLAY:
                nodes.play();
                break;
            case ACTION_NEXT:
                nodes.next();
                break;
            case ACTION_PREV:
                nodes.previous();
                break;
        }
    }
}
