package com.example.android.tj;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.LinkedList;

import static com.example.android.tj.Constants.SERVICE_CMD;
import static com.example.android.tj.Constants.SERVICE_RESULT;
import static com.example.android.tj.Constants.SERVICE_RESULT_STATUS;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    public Switch switch_;
    public SeekBar seekBar;
    public TextView nowPlaying;
    private Handler handler;
    ArrayAdapter<String> adapter;


    private Runnable uiUpdateCallback = new Runnable() {
        @Override
        public void run() {
            sendTJServiceCmd(Constants.SERVICE_CMD_SYNC);
            handler.postDelayed(this, 1000);
        }
    };

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra(SERVICE_RESULT_STATUS);
            updateUI(TJServiceStatus.fromJson(status));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.android.tj.R.layout.activity_main);
        switch_ = findViewById(com.example.android.tj.R.id.switch1);

        seekBar = findViewById(com.example.android.tj.R.id.seekBar);
        nowPlaying = findViewById(R.id.now_playing);

        sendTJServiceCmd(Constants.SERVICE_CMD_START);


        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
                new IntentFilter(SERVICE_RESULT));


        initUI();
        initPollingThread();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.handler.removeCallbacksAndMessages(null);
    }

    //TODO: more general representation for arg1
    private void sendTJServiceCmd(int cmd, int arg1) {
        Intent intent = new Intent(this, TJService.class);
        intent.putExtra(SERVICE_CMD, new TJServiceCommand(cmd, arg1).toString());
        startService(intent);
    }

    private void sendTJServiceCmd(int cmd) {
        Intent intent = new Intent(this, TJService.class);
        intent.putExtra(SERVICE_CMD, new TJServiceCommand(cmd).toString());
        startService(intent);
    }

    private void initUI() {
        //switch
        switch_.setChecked(true);

        //list view
        ListView lv = findViewById(com.example.android.tj.R.id.list_files);
        adapter = new ArrayAdapter<>(this, R.layout.activity_listview, new LinkedList<>());
        lv.setAdapter(adapter);

        lv.setOnItemClickListener((parent, view, position, id) -> {
            sendTJServiceCmd(Constants.SERVICE_CMD_PLAY_FROM, position);
            lv.smoothScrollToPosition(0);
        });

        //seek bar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    sendTJServiceCmd(Constants.SERVICE_CMD_SEEK, progress * 1000);

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacksAndMessages(null);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                handler.postDelayed(uiUpdateCallback, 1000);
            }
        });
    }

    private void updateUI(TJServiceStatus status) {
        //switch
        switch_.setChecked(status.isPlaying);

        //list view
        adapter.clear();
        adapter.addAll(status.fileNamesWithIdx);
        adapter.notifyDataSetChanged();

        //now playing
        nowPlaying.setText(status.nowPlaying);

        //seek bar
        seekBar.setMax(status.duration / 1000);
        seekBar.setProgress(status.currentPosition / 1000);
    }

    private void initPollingThread() {
        handler = new Handler();
        this.runOnUiThread(uiUpdateCallback);
    }


    public void onPriorityShuffle(View view) {
        sendTJServiceCmd(Constants.SERVICE_CMD_PRIORITY_SHUFFLE);
    }

    public void onShuffle(View view) {
        sendTJServiceCmd(Constants.SERVICE_CMD_SHUFFLE);
    }

    public void onSwitch(View view) {
        if (switch_.isChecked()) {
            sendTJServiceCmd(Constants.SERVICE_CMD_PLAY);
        } else {
            sendTJServiceCmd(Constants.SERVICE_CMD_PAUSE);
        }
    }

    public void onSort(View view) {
        sendTJServiceCmd(Constants.SERVICE_CMD_SORT);
    }
}
