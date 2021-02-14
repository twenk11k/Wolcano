package com.wolcano.musicplayer.music.di

import com.wolcano.musicplayer.music.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
class RepositoryModule {

    @Provides
    @ActivityRetainedScoped
    fun provideSongRepository(): SongRepository {
        return SongRepository()
    }

    @Provides
    @ActivityRetainedScoped
    fun provideAlbumRepository(): AlbumRepository {
        return AlbumRepository()
    }

    @Provides
    @ActivityRetainedScoped
    fun provideArtistRepository(): ArtistRepository {
        return ArtistRepository()
    }

    @Provides
    @ActivityRetainedScoped
    fun provideGenreRepository(): GenreRepository {
        return GenreRepository()
    }

    @Provides
    @ActivityRetainedScoped
    fun providePlaylistRepository(): PlaylistRepository {
        return PlaylistRepository()
    }

    @Provides
    @ActivityRetainedScoped
    fun provideMainRepository(): MainRepository {
        return MainRepository()
    }

}