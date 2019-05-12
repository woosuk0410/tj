package com.example.android.tj.database

import android.content.Context
import android.os.Environment
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(version = 1, entities = [Song::class, SongMetadata::class, Image::class])
@TypeConverters(Converters::class)
abstract class RoomDatabaseClient : RoomDatabase() {

    abstract fun songDao(): SongDao
    abstract fun songMetadataDao(): SongMetadataDao
    abstract fun imageDao(): ImageDao


    companion object {
        private val DB_PATH = "${Environment.getExternalStorageDirectory().absolutePath}/tj_db/songs"

        private var instance: RoomDatabaseClient? = null

        fun getInstance(context: Context): RoomDatabaseClient {
            if (instance == null) {
                instance = createDatabase(context)
            }
            return instance!!
        }

        private fun createDatabase(context: Context): RoomDatabaseClient {
            return Room.databaseBuilder(
                    context,
                    RoomDatabaseClient::class.java,
                    DB_PATH
            ).allowMainThreadQueries().build()
        }
    }
}