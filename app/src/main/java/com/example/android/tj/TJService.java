package com.example.android.tj;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Pair;

import com.google.gson.Gson;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.example.android.tj.Constants.NOTIFICATION_CHANNEL_ID;
import static com.example.android.tj.Constants.NOTIFICATION_ID;
import static com.example.android.tj.Constants.SERVICE_CMD;

public class TJService extends Service {


    private static final String TAG = "TJService";


    private Nodes nodes;
    private BroadcastReceiver receiver;
    MediaSessionCompat mediaSession;
    private NotificationManager notificationManager;

    private ServiceHandler serviceHandler;
    private Handler mediaMetadataHandler;

    private void initNotificationManager() {
        String channelName = "TJService";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName,
                NotificationManager.IMPORTANCE_DEFAULT);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(chan);
    }

    private void initBluetoothBroadcastReceiver() {
        receiver = new BluetoothBroadcastReceiver(nodes);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(receiver, filter);
    }

    private void initMediaSession() {
        mediaSession = new MediaSessionCompat(TJService.this, TAG);
        mediaSession.setCallback(new BluetoothButtonCallback(nodes));
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setActive(true);
    }

    private void initServiceHandler() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        // Get the HandlerThread's Looper and use it for our Handler
        Looper mServiceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(mServiceLooper);
    }

    private void initMediaMetadataHandler() {
        mediaMetadataHandler = new Handler();

        mediaMetadataHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Bitmap bitmap = nodes.getBitMap();

                // MediaMetadataCompat
                MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
                builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, nodes.currentFile()
                        .getName().replace(".aac", ""))
                        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, nodes
                                .currentFile().getName())
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, Nodes.player
                                .getDuration())
                        .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap);
                mediaSession.setMetadata(builder.build());

                // PlaybackStateCompat
                long actions = PlaybackStateCompat.ACTION_PLAY
                        | PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                        | PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
                        | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS;
                if (Nodes.player.isPlaying()) {
                    actions |= PlaybackStateCompat.ACTION_PAUSE;
                }
                int state = Nodes.player.isPlaying() ? PlaybackStateCompat
                        .STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;
                PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                        .setActions(actions).setState(state, Nodes.player.getCurrentPosition(),
                                1.0f,
                                SystemClock.elapsedRealtime());
                mediaSession.setPlaybackState(stateBuilder.build());

                mediaSession.setActive(true);
                mediaMetadataHandler.postDelayed(this, 5000);
            }
        }, 5000);
    }

    private TJServiceStatus getCurrentStatus() {
        List<String> fileNames = IntStream.range(1, nodes.nodes.size() + 1).mapToObj(i ->
                ((Integer) i).toString() +
                        ". " +
                        nodes.nodes.get(i - 1).file.getName()).collect(Collectors.toList
                ());
        int duration = Nodes.player.getDuration();
        int curPos = Nodes.player.getCurrentPosition();
        String nowPlaying = nodes.nodes.getLast().file.getName();
        boolean isPlaying = Nodes.player.isPlaying();
        String md5 = nodes.nodes.getLast().metadata.md5Hash;
        return new TJServiceStatus(fileNames, duration, curPos, nowPlaying, isPlaying, md5);
    }

    private TJServiceSearchResult getSearchResult(String query) {
        List<Nodes.Node> candidates = nodes.nodes.stream().filter(n -> n.metadata.name.contains
                (query)).collect(Collectors.toList());

        List<Pair<String, String>> pairs = candidates.stream().map(candidate -> Pair.create
                (candidate.metadata.md5Hash, candidate.metadata.name)).collect(Collectors.toList());

        return new TJServiceSearchResult(pairs);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        nodes = new Nodes(TJService.this);
        initMediaSession();
        initBluetoothBroadcastReceiver();
        initNotificationManager();
        startForeground(NOTIFICATION_ID, nodes.getNotification());
        initServiceHandler();
        initMediaMetadataHandler();
    }

    private final class ServiceHandler extends Handler {
        ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Constants.SERVICE_CMD_PLAY:
                    nodes.play();
                    break;
                case Constants.SERVICE_CMD_NEXT:
                    nodes.next();
                    break;
                case Constants.SERVICE_CMD_PREVIOUS:
                    nodes.previous();
                    break;
                case Constants.SERVICE_CMD_PLAY_FROM:
                    nodes.playFromLocation(msg.arg1);
                    break;
                case Constants.SERVICE_CMD_PAUSE:
                    nodes.pause();
                    break;
                case Constants.SERVICE_CMD_SEEK:
                    Nodes.player.seekTo(msg.arg1);
                    break;
                case Constants.SERVICE_CMD_PRIORITY_SHUFFLE:
                    nodes.priorityShuffle();
                    nodes.next();
                    break;
                case Constants.SERVICE_CMD_SHUFFLE:
                    nodes.shuffle();
                    nodes.next();
                    break;
                case Constants.SERVICE_CMD_START:
                    if (!nodes.hasStarted) {
                        nodes.next();
                    } else {
                        nodes.play();
                    }
                    break;
                case Constants.SERVICE_CMD_SORT:
                    nodes.nodes.sort(Comparator.comparing(n -> n.file.getName()));
                    nodes.next();
                    break;
                case Constants.SERVICE_QUERY_METADATA:
                    Intent intent = new Intent(Constants.SERVICE_ANSWER);
                    Metadata metadata = nodes.nodes.get(msg.arg1).metadata;
                    intent.putExtra(Constants.SERVICE_ANSWER_METADATA, new Gson().toJson(metadata));
                    LocalBroadcastManager.getInstance(TJService.this).sendBroadcast(intent);
                    break;
                case Constants.SERVICE_PATCH_METADATA:
                    nodes.UpdateMetadata((Metadata) msg.obj);
                case Constants.SERVICE_QUERY_SEARCH:
                    TJServiceSearchResult result = getSearchResult((String) msg.obj);
                    intent = new Intent(Constants.SERVICE_ANSWER);
                    intent.putExtra(Constants.SERVICE_ANSWER_SEARCH, result.toString());
                    LocalBroadcastManager.getInstance(TJService.this).sendBroadcast(intent);
                case Constants.SERVICE_CMD_SYNC:
                default:
                    break;
            }
            Intent intent = new Intent(Constants.SERVICE_RESULT);
            intent.putExtra(Constants.SERVICE_RESULT_STATUS, getCurrentStatus().toString());
            LocalBroadcastManager.getInstance(TJService.this).sendBroadcast(intent);
            notificationManager.notify(NOTIFICATION_ID, nodes.getNotification());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        TJServiceCommand cmd = TJServiceCommand.fromJson(intent.getStringExtra(SERVICE_CMD));
        Message msg = serviceHandler.obtainMessage();
        msg.what = cmd.cmdCode;
        msg.arg1 = cmd.arg1; //TODO: used in play from, seek to, and metadata query

        if (cmd.cmdCode == Constants.SERVICE_PATCH_METADATA) {
            msg.obj = new Gson().fromJson(cmd.data, Metadata.class);
        } else if (cmd.cmdCode == Constants.SERVICE_QUERY_SEARCH) {
            msg.obj = cmd.data;
        }

        serviceHandler.sendMessage(msg);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
