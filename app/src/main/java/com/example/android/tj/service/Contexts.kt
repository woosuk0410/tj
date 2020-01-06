package com.example.android.tj.service

import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executors

object Contexts {
    val singleThreadContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
}