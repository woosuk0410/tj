package com.example.android.tj.service

import android.media.MediaPlayer
import com.example.android.tj.database.SongMetadata
import java.time.Duration
import java.time.Instant

class PlayerWrapper : MediaPlayer() {
    override fun getDuration(): Int {
        if (PlayerSemaphore.lock.tryAcquire()) {
            var ret = 0
            try {
                ret = super.getDuration()
            } finally {
                PlayerSemaphore.lock.release()
                return ret
            }
        } else {
            return 0
        }
    }

    override fun getCurrentPosition(): Int {
        if (PlayerSemaphore.lock.tryAcquire()) {
            var ret = 0
            try {
                ret = super.getCurrentPosition()
            } finally {
                PlayerSemaphore.lock.release()
                return ret
            }
        } else {
            return 0
        }
    }

    private var currentSegmentStartingTime = Instant.MAX
    var playedSoFarSeconds = 0L
    var recordingSong: SongMetadata? = null

    override fun reset() {
        super.reset()
        playedSoFarSeconds = 0
        currentSegmentStartingTime = Instant.MAX
    }

    override fun start() {
        super.start()
        currentSegmentStartingTime = Instant.now()
    }

    fun accumulatePlayedSoFar() {
        playedSoFarSeconds += Duration.between(currentSegmentStartingTime, Instant.now()).seconds
    }

    override fun pause() {
        super.pause()
        accumulatePlayedSoFar()
    }
}