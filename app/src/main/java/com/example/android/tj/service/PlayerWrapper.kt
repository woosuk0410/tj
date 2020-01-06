package com.example.android.tj.service

import android.media.MediaPlayer

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
}