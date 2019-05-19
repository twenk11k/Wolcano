package com.wolcano.musicplayer.music;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.multidex.MultiDex;

import com.wolcano.musicplayer.music.content.AppHandler;
import com.wolcano.musicplayer.music.mvp.db.DatabaseManager;
import com.wolcano.musicplayer.music.provider.MusicService;


public class App extends Application {

    public Context context = null;
    public static float dens = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        dens = context.getResources().getDisplayMetrics().density;

        GeneralCache.get().initializeCache(this);
        AppHandler.setCallbacks(this);
        DatabaseManager.get().init(this);

        Intent intent = new Intent(this, MusicService.class);
        startService(intent);

    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}
