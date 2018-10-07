package com.example.android.tj;

import android.content.Intent;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.KeyEvent;

public class BluetoothButtonCallback extends MediaSessionCompat.Callback {

    private Nodes nodes;

    BluetoothButtonCallback(Nodes nodes) {
        this.nodes = nodes;
    }

    @Override
    public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
        KeyEvent keyEvent = mediaButtonEvent.getParcelableExtra("android.intent.extra" +
                ".KEY_EVENT");
        int action = keyEvent.getAction();
        if (action == KeyEvent.ACTION_UP) {
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    nodes.play();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    nodes.pause();
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    nodes.next();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    nodes.previous();
                    break;
                default:
                    return super.onMediaButtonEvent(mediaButtonEvent);
            }
        }
        return super.onMediaButtonEvent(mediaButtonEvent);
    }
}
