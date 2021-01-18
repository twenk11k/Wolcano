package com.wolcano.musicplayer.music;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.multidex.MultiDex;

import com.wolcano.musicplayer.music.content.AppHandler;
import com.wolcano.musicplayer.music.di.component.ApplicationComponent;
import com.wolcano.musicplayer.music.di.component.DaggerApplicationComponent;
import com.wolcano.musicplayer.music.di.module.ApplicationModule;
import com.wolcano.musicplayer.music.mvp.db.DatabaseManager;
import com.wolcano.musicplayer.music.provider.MusicService;

public class App extends Application {

    public Context context = null;
    public static float dens = 1;
    private ApplicationComponent applicationComponent;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        setupInjector();
        dens = context.getResources().getDisplayMetrics().density;

        GeneralCache.get().initializeCache(this);
        AppHandler.setCallbacks(this);
        DatabaseManager.get().init(this);

        Intent intent = new Intent(this, MusicService.class);
        startService(intent);

    }

    private void setupInjector() {
        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
