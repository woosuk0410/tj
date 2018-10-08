package com.example.android.tj;

import android.media.MediaPlayer;
import android.support.v4.media.MediaMetadataCompat;
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

    //TODO
    //.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, Glide.with(ctx)
    //.asBitmap().load(TJ_DIR_IMG + "/tj2.png").submit().get())
    static void setMetadata(MainActivity ctx, Nodes.Node node) {
        ctx.mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, node.file.getName())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, "谭晶")
                .build());
    }
}
