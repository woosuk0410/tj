package com.example.android.tj.service

import java.util.concurrent.Semaphore

object PlayerSemaphore {
    val lock = Semaphore(1)
}