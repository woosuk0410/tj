package com.example.android.tj.model.database

import com.example.android.tj.application.TJApplication
import com.example.android.tj.database.History
import com.example.android.tj.database.RoomDatabaseClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull

class HistoryModel {
    private val dao = RoomDatabaseClient.getInstance(TJApplication.instance).historyDao()

    suspend fun getAll(callback: (List<History>) -> Unit) {
        val job = GlobalScope.async {
            withTimeoutOrNull(
                    TIMEOUT_DURATION_MILLIS) {
                dao.getAll()
            }
        }
        callback.invoke(job.await() ?: emptyList())
    }

    suspend fun insert(history: History, callback: (Boolean) -> Unit) {
        val job = GlobalScope.async {
            try {
                withTimeout(
                        TIMEOUT_DURATION_MILLIS) {
                    dao.insert(history)
                }
            } catch (e: java.lang.Exception) {
                callback.invoke(false)
            }
        }
        job.await()
        callback.invoke(true)
    }
}