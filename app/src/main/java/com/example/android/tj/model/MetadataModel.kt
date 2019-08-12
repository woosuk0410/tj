package com.example.android.tj.model

import com.example.android.tj.application.TJApplication
import com.example.android.tj.database.RoomDatabaseClient
import com.example.android.tj.database.SongMetadata
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull

const val TIMEOUT_DURATION_MILLIS = 3000L

class MetadataModel {
    private val dao = RoomDatabaseClient.getInstance(TJApplication.instance).songMetadataDao()

    suspend fun getAll(callback: (List<SongMetadata>) -> Unit) {
        val job = GlobalScope.async {
            withTimeoutOrNull(TIMEOUT_DURATION_MILLIS) {
                dao.getAll()
            }
        }

        callback.invoke(job.await() ?: emptyList())
    }

    suspend fun insert(metadata: SongMetadata, callback: (Boolean) -> Unit) {
        val job = GlobalScope.async {
            try {
                withTimeout(TIMEOUT_DURATION_MILLIS) {
                    dao.insert(metadata)
                }
            } catch (e: java.lang.Exception) {
                callback.invoke(false)
            }
        }
        job.await()
        callback.invoke(true)
    }
}