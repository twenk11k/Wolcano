package com.wolcano.musicplayer.music

import android.app.Application
import android.content.Context
import android.content.Intent
import com.wolcano.musicplayer.music.content.AppHandler
import com.wolcano.musicplayer.music.provider.MusicService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    companion object {
        lateinit var application: Application

        fun getContext(): Context {
            return application.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        AppCache.initializeCache(this)
        AppHandler.setCallbacks(this)
        startService(Intent(this, MusicService::class.java))
    }

}