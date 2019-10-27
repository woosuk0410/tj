package com.example.android.tj

import java.util.concurrent.Semaphore

object PlayerSemaphore {
    val lock = Semaphore(1)
}