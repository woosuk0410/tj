package com.example.android.tj;

import android.content.Intent;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Pair;
import android.view.KeyEvent;

import java.util.ArrayList;
import java.util.List;

public class BluetoothButtonCallback extends MediaSessionCompat.Callback {

    private Nodes nodes;

    private List<Pair<Integer, Long>> eventList = new ArrayList<>();

    BluetoothButtonCallback(Nodes nodes) {
        this.nodes = nodes;
    }

    private void maybeShuffle(int keyCode) {
        Runnable shuffleFunc = () -> {
            if (keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
                nodes.shuffle();
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_NEXT) {
                nodes.priorityShuffle();
            }
            nodes.next();
        };

        eventList.add(new Pair<>(keyCode, System.currentTimeMillis()));

        if (eventList.size() < 3) return;

        List<Pair<Integer, Long>> mostRecent = eventList.subList(eventList.size() - 3, eventList
                .size());

        if (mostRecent.stream().filter(event -> event.first == keyCode).count
                () >= 3) {
            long start = mostRecent.get(0).second;
            long end = mostRecent.get(2).second;
            if (end - start <= 3000) {
                shuffleFunc.run();
                eventList.clear();
            }
        }
    }

    @Override
    public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
        KeyEvent keyEvent = mediaButtonEvent.getParcelableExtra("android.intent.extra" +
                ".KEY_EVENT");
        int action = keyEvent.getAction();
        if (action == KeyEvent.ACTION_UP) {
            int keyCode = keyEvent.getKeyCode();
            switch (keyCode) {
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    nodes.play();
                    eventList.clear();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    nodes.pause();
                    eventList.clear();
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    nodes.next();
                    maybeShuffle(keyCode);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    nodes.previous();
                    maybeShuffle(keyCode);
                    break;
                default:
                    return super.onMediaButtonEvent(mediaButtonEvent);
            }
        }
        return super.onMediaButtonEvent(mediaButtonEvent);
    }
}
