package com.wolcano.musicplayer.music.di.module;


import android.app.Application;

import com.wolcano.musicplayer.music.App;
import com.wolcano.musicplayer.music.di.scope.PerApplication;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private final App wolcanoApp;

    public ApplicationModule(App wolcanoApp){
        this.wolcanoApp = wolcanoApp;
    }

    @Provides
    @PerApplication
    public App providesWolcanoApp(){
        return wolcanoApp;
    }

    @Provides
    public Application provideApplication(){
        return wolcanoApp;
    }


}
