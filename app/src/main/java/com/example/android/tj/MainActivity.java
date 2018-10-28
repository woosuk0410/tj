package com.example.android.tj;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.android.tj.R.layout.activity_main);
        switch_ = findViewById(com.example.android.tj.R.id.switch1);

        seekBar = findViewById(com.example.android.tj.R.id.seekBar);
        nowPlaying = findViewById(R.id.now_playing);

        if (nodes == null) {
            handler = new Handler();
            nodes = new Nodes(this);

            mediaSession = new MediaSessionCompat(this, TAG);
            mediaSession.setCallback(new BluetoothButtonCallback(nodes));
            mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
            mediaSession.setActive(true);

            receiver = new BluetoothBroadcastReceiver(nodes);
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            registerReceiver(receiver, filter);

            init();
        }
    }

    private static final String TAG = "MainActivity";
    public Switch switch_;
    public SeekBar seekBar;
    public TextView nowPlaying;
    private Handler handler;
    private Nodes nodes;


    private BroadcastReceiver receiver;
    MediaSessionCompat mediaSession;

    private void init() {
        ListView lv = findViewById(com.example.android.tj.R.id.list_files);
        lv.setAdapter(nodes.adapter);

        lv.setOnItemClickListener((parent, view, position, id) -> {
            nodes.playFromLocation(position);
            lv.smoothScrollToPosition(0);
        });

        switch_.setChecked(true);
        nodes.next();

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Nodes.player.isPlaying()) {
                    seekBar.setProgress(Nodes.player.getCurrentPosition() / 1000);
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    public void onShuffle(View view) {
        nodes.priorityShuffle();
        nodes.next();
    }

    public void onSwitch(View view) {
        if (switch_.isChecked()) {
            nodes.play();
        } else {
            nodes.pause();
        }
    }

    public void onSort(View view) {
        nodes.nodes.sort(Comparator.comparing(n -> n.file.getName()));
        nodes.next();
    }
}
