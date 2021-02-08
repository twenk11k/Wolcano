package com.wolcano.musicplayer.music.di.component

import android.app.Application
import com.wolcano.musicplayer.music.App
import com.wolcano.musicplayer.music.di.module.ApplicationModule
import com.wolcano.musicplayer.music.di.scope.PerApplication
import dagger.Component

@PerApplication
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    fun application(): Application?
    fun wolcanoApp(): App?
}