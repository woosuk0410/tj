package com.example.android.tj.model.database

import com.example.android.tj.application.TJApplication
import com.example.android.tj.database.RoomDatabaseClient
import com.example.android.tj.database.Song
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeoutOrNull

class SongModel {
    private val dao = RoomDatabaseClient.getInstance(TJApplication.instance).songDao()

    fun getById(id: String): Song {
        return Song(
                id,
                dao.getData0ById(id),
                dao.getData1ById(id),
                dao.getData2ById(id),
                dao.getData3ById(id),
                dao.getData4ById(id),
                dao.getData5ById(id),
                dao.getData6ById(id),
                dao.getData7ById(id),
                dao.getData8ById(id),
                dao.getData9ById(id)
//                dao.getData10ById(id),
//                dao.getData11ById(id),
//                dao.getData12ById(id),
//                dao.getData13ById(id),
//                dao.getData14ById(id),
//                dao.getData15ById(id),
//                dao.getData16ById(id),
//                dao.getData17ById(id),
//                dao.getData18ById(id),
//                dao.getData19ById(id),
//                dao.getData20ById(id),
//                dao.getData21ById(id),
//                dao.getData22ById(id),
//                dao.getData23ById(id),
//                dao.getData24ById(id),
//                dao.getData25ById(id),
//                dao.getData26ById(id),
//                dao.getData27ById(id),
//                dao.getData28ById(id),
//                dao.getData29ById(id),
//                dao.getData30ById(id),
//                dao.getData31ById(id),
//                dao.getData32ById(id),
//                dao.getData33ById(id),
//                dao.getData34ById(id),
//                dao.getData35ById(id),
//                dao.getData36ById(id),
//                dao.getData37ById(id),
//                dao.getData38ById(id),
//                dao.getData39ById(id),
//                dao.getData40ById(id),
//                dao.getData41ById(id),
//                dao.getData42ById(id),
//                dao.getData43ById(id),
//                dao.getData44ById(id),
//                dao.getData45ById(id),
//                dao.getData46ById(id),
//                dao.getData47ById(id),
//                dao.getData48ById(id),
//                dao.getData49ById(id),
//                dao.getData50ById(id),
//                dao.getData51ById(id),
//                dao.getData52ById(id),
//                dao.getData53ById(id),
//                dao.getData54ById(id),
//                dao.getData55ById(id),
//                dao.getData56ById(id),
//                dao.getData57ById(id),
//                dao.getData58ById(id),
//                dao.getData59ById(id),
//                dao.getData60ById(id),
//                dao.getData61ById(id),
//                dao.getData62ById(id),
//                dao.getData63ById(id),
//                dao.getData64ById(id),
//                dao.getData65ById(id),
//                dao.getData66ById(id),
//                dao.getData67ById(id),
//                dao.getData68ById(id),
//                dao.getData69ById(id),
//                dao.getData70ById(id),
//                dao.getData71ById(id),
//                dao.getData72ById(id),
//                dao.getData73ById(id),
//                dao.getData74ById(id),
//                dao.getData75ById(id),
//                dao.getData76ById(id),
//                dao.getData77ById(id),
//                dao.getData78ById(id),
//                dao.getData79ById(id),
//                dao.getData80ById(id),
//                dao.getData81ById(id),
//                dao.getData82ById(id),
//                dao.getData83ById(id),
//                dao.getData84ById(id),
//                dao.getData85ById(id),
//                dao.getData86ById(id),
//                dao.getData87ById(id),
//                dao.getData88ById(id),
//                dao.getData89ById(id),
//                dao.getData90ById(id),
//                dao.getData91ById(id),
//                dao.getData92ById(id),
//                dao.getData93ById(id),
//                dao.getData94ById(id),
//                dao.getData95ById(id),
//                dao.getData96ById(id),
//                dao.getData97ById(id),
//                dao.getData98ById(id),
//                dao.getData99ById(id)
        )
    }

    suspend fun getById(id: String, callback: (Song?) -> Unit) {
        val job = GlobalScope.async {
            withTimeoutOrNull(
                    TIMEOUT_DURATION_MILLIS) {
                getById(id)
            }
        }

        callback.invoke(job.await())
    }
}
