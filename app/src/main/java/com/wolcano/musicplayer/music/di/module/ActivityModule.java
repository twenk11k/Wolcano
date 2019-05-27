package com.wolcano.musicplayer.music.di.module;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.wolcano.musicplayer.music.di.scope.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    private final AppCompatActivity activity;

    public ActivityModule(AppCompatActivity activity){
        this.activity = activity;
    }

    @Provides
    @PerActivity
    public Context provideContext(){
        return activity;
    }

}
