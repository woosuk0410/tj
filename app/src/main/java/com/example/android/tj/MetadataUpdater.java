package com.example.android.tj;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.media.MediaMetadataCompat;

class MetadataUpdater {

    private Handler handler;
    private TJService tjService;

    MetadataUpdater(TJService tjService) {
        this.handler = new Handler();
        this.tjService = tjService;
    }

    void run(Nodes.Node node) {
        this.handler.removeCallbacksAndMessages(null);
        this.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Nodes.player.isPlaying()) {
                    MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
                    builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, node.file.getName())
                            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, node.file
                                    .getName())
                            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, "谭晶")
                            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, Nodes.player
                                    .getDuration());
                    Bitmap bitmap = BitmapFactory.decodeFile(Nodes.TJ_DIR_IMG + "/tj2.png");
                    builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, bitmap);
                    builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap);
                    tjService.mediaSession.setMetadata(builder.build());
                }
                handler.postDelayed(this, 1000);
            }
        }, 1);
    }
}
