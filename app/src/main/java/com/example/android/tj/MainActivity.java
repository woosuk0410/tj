package com.example.android.tj;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.android.tj.R.layout.activity_main);
        switch_ = findViewById(com.example.android.tj.R.id.switch1);

        seekBar = findViewById(com.example.android.tj.R.id.seekBar);
        handler = new Handler();

        if (currentPlayer == null) {
            init();
        }
        if (currentPlayer.isPlaying()) {
            switch_.setChecked(true);
        } else {
            switch_.setChecked(false);
        }
    }

    MediaPlayer currentPlayer;
    File[] musicFiles;
    Switch switch_;
    SeekBar seekBar;
    private Handler handler;


    private void init() {
        ListView lv = findViewById(com.example.android.tj.R.id.list_files);

        File dirFO = Environment.getExternalStorageDirectory();
        File tjDir = new File(dirFO.getAbsolutePath() + "/tj");
        musicFiles = tjDir.listFiles();

        Optional<MediaPlayer> firstMp = Arrays.stream(musicFiles).map(m -> {
            MediaPlayer mp = new MediaPlayer();
            try {
                mp.setDataSource(this, Uri.fromFile(m));
                mp.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mp;
        }).reduce((player1, player2) -> {
            player2.setOnCompletionListener(mp -> {
                mp.release();
                currentPlayer = player1;
                setSeekBar(currentPlayer);
                currentPlayer.start();
            });
            return player2;
        });

        currentPlayer = firstMp.map(player -> {
            try {
                player.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return player;
        }).orElse(MediaPlayer.create(this, Uri.fromFile(musicFiles[0])));

        setSeekBar(currentPlayer);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, com.example.android.tj.R.layout
                .activity_listview,
                Arrays.stream(musicFiles).map(File::getName).collect(Collectors.toList()));
        lv.setAdapter(adapter);


        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(currentPlayer.getCurrentPosition() / 1000);
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void setSeekBar(MediaPlayer player) {
        seekBar.setMax(player.getDuration() / 1000);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void onSwitch(View view) {
        if (switch_.isChecked()) {
            currentPlayer.start();
        } else {
            currentPlayer.pause();
        }
    }
}
