package com.example.android.tj

import android.media.MediaPlayer

class PlayerWrapper : MediaPlayer() {

    override fun getDuration(): Int {
        if (PlayerSemaphore.lock.tryAcquire()) {
            try {
                val ret = super.getDuration()
                return ret
            } finally {
                PlayerSemaphore.lock.release()
            }
        } else {
            return 0
        }
    }

    override fun getCurrentPosition(): Int {
        if (PlayerSemaphore.lock.tryAcquire()) {
            try {
                val ret = super.getCurrentPosition()
                return ret
            } finally {
                PlayerSemaphore.lock.release()
            }
        } else {
            return 0
        }
    }
}