package com.wolcano.musicplayer.music

import android.app.Application
import android.content.Context
import android.content.Intent
import com.wolcano.musicplayer.music.content.AppHandler
import com.wolcano.musicplayer.music.di.component.ApplicationComponent
import com.wolcano.musicplayer.music.di.component.DaggerApplicationComponent
import com.wolcano.musicplayer.music.di.module.ApplicationModule
import com.wolcano.musicplayer.music.provider.MusicService

class App : Application() {

    private var applicationComponent: ApplicationComponent? = null

    companion object {
        lateinit var application: Application

        fun getContext(): Context {
            return application.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        setupInjector()

        GeneralCache.initializeCache(this)
        AppHandler.setCallbacks(this)

        val intent = Intent(this, MusicService::class.java)
        startService(intent)

    }

    private fun setupInjector() {
        applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }

    fun getApplicationComponent(): ApplicationComponent? {
        return applicationComponent
    }

}