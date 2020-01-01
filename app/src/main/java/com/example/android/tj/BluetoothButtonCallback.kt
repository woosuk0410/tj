package com.example.android.tj

import android.content.Intent
import android.support.v4.media.session.MediaSessionCompat
import android.util.Pair
import android.view.KeyEvent
import java.util.*

class BluetoothButtonCallback internal constructor(private val nodes: Nodes) :
        MediaSessionCompat.Callback() {

    private val eventList = ArrayList<Pair<Int, Long>>()

    private fun maybeShuffle(keyCode: Int) {
        val shuffleFunc = Runnable {
            if (keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
                nodes.shuffle()
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_NEXT) {
                nodes.priorityShuffle()
            }
            nodes.playFromTop()
        }

        eventList.add(Pair(keyCode, System.currentTimeMillis()))

        if (eventList.size < 3) return

        val mostRecent = eventList.subList(
                eventList.size - 3, eventList
                .size)

        if (mostRecent.stream().filter { event -> event.first == keyCode }.count() >= 3) {
            val start = mostRecent[0].second
            val end = mostRecent[2].second
            if (end - start <= 3000) {
                shuffleFunc.run()
                eventList.clear()
            }
        }
    }

    override fun onMediaButtonEvent(mediaButtonEvent: Intent): Boolean {
        val keyEvent = mediaButtonEvent
                .getParcelableExtra<KeyEvent>("android.intent.extra" + ".KEY_EVENT")
        val action = keyEvent.action
        if (action == KeyEvent.ACTION_UP) {
            val keyCode = keyEvent.keyCode
            when (keyCode) {
                KeyEvent.KEYCODE_MEDIA_PLAY     -> {
                    nodes.play()
                    eventList.clear()
                }
                KeyEvent.KEYCODE_MEDIA_PAUSE    -> {
                    nodes.pause()
                    eventList.clear()
                }
                KeyEvent.KEYCODE_MEDIA_NEXT     -> {
                    nodes.next()
                    maybeShuffle(keyCode)
                }
                KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                    nodes.previous()
                    maybeShuffle(keyCode)
                }
                else                            -> return super.onMediaButtonEvent(mediaButtonEvent)
            }
        }
        return super.onMediaButtonEvent(mediaButtonEvent)
    }
}
