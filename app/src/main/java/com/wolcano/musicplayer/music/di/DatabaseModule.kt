package com.wolcano.musicplayer.music.di

import com.wolcano.musicplayer.music.persistence.AppDatabase
import com.wolcano.musicplayer.music.persistence.SongDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /*
    @Provides
    @Singleton
    fun provideAppDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "wolcano.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    */

    @Provides
    @Singleton
    fun provideSongDao(appDatabase: AppDatabase): SongDao {
        return appDatabase.songDao()
    }

}