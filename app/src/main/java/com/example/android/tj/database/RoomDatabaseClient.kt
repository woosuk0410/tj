package com.example.android.tj.database

import android.content.Context
import android.os.Environment
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(version = 2, entities = [Song::class, SongMetadata::class, Image::class, History::class])
abstract class RoomDatabaseClient : RoomDatabase() {

    abstract fun songDao(): SongDao
    abstract fun songMetadataDao(): SongMetadataDao
    abstract fun imageDao(): ImageDao
    abstract fun historyDao(): HistoryDao


    companion object {
        private val DB_PATH = "${Environment.getExternalStorageDirectory().absolutePath}/tj_db/songs"

        private var instance: RoomDatabaseClient? = null

        fun getInstance(context: Context): RoomDatabaseClient {
            if (instance == null) {
                instance = createDatabase(context)
            }
            return instance!!
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE IF EXISTS histories")
                database.execSQL(
                        "CREATE TABLE histories (id TEXT NOT NULL, played_at TEXT NOT NULL, PRIMARY KEY (id, played_at))")
            }
        }

        private fun createDatabase(context: Context): RoomDatabaseClient {
            return Room.databaseBuilder(
                    context,
                    RoomDatabaseClient::class.java,
                    DB_PATH
            ).allowMainThreadQueries()
                    .addMigrations(MIGRATION_1_2)
                    .build()
        }
    }
}