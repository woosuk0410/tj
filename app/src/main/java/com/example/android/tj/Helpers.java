package com.example.android.tj;

import android.media.MediaPlayer;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

class Helpers {
    static void setSeekBar(SeekBar seekBar, MediaPlayer player) {
        seekBar.setMax(player.getDuration() / 1000);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Nodes.player.seekTo(progress * 1000);
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

    static void setNowPlaying(TextView tv, String name) {
        tv.setText(name);
    }

    static void setSwitch(Switch sw, boolean checked) {
        sw.setChecked(checked);
    }

    static void setMetadata(MainActivity activity, Nodes.Node node) {
        MetadataUpdater updater = new MetadataUpdater(activity, node);
        updater.run();
    }
}
