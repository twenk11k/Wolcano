package com.wolcano.musicplayer.music.data.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wolcano.musicplayer.music.data.model.SearchHistory
import com.wolcano.musicplayer.music.data.model.Song

@Database(entities = [Song::class, SearchHistory::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun songDao(): SongDao

    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {

        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, AppDatabase::class.java, "wolcano.db")
                    .allowMainThreadQueries()
                    .build()
            }
            return INSTANCE!!
        }

    }
}