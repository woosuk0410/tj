package com.example.android.tj;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

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
            init();
        }
    }

    public Switch switch_;
    public SeekBar seekBar;
    public TextView nowPlaying;
    private Handler handler;
    private Nodes nodes;


    private void init() {
        ListView lv = findViewById(com.example.android.tj.R.id.list_files);
        lv.setAdapter(nodes.adapter);

        lv.setOnItemClickListener((parent, view, position, id) -> {
            nodes.play(position);
            lv.smoothScrollToPosition(0);
        });


        switch_.setChecked(true);
        nodes.play(0);

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
        Collections.shuffle(nodes.nodes);
        nodes.play(0);
    }


    public void onSwitch(View view) {
        if (switch_.isChecked()) {
            Nodes.player.start();
        } else {
            Nodes.player.pause();
        }
    }

    public void onSort(View view) {
        nodes.nodes.sort(Comparator.comparing(n -> n.file.getName()));
        nodes.play(0);
    }
}
