package com.wolcano.musicplayer.music.di.module

import android.app.Application
import com.wolcano.musicplayer.music.App
import com.wolcano.musicplayer.music.di.scope.PerApplication
import dagger.Module
import dagger.Provides

@Module
class ApplicationModule(private val wolcanoApp: App) {
    @Provides
    @PerApplication
    fun providesWolcanoApp(): App {
        return wolcanoApp
    }

    @Provides
    fun provideApplication(): Application {
        return wolcanoApp
    }
}