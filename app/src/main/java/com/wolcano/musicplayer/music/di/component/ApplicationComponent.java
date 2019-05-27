package com.wolcano.musicplayer.music.di.component;

import android.app.Application;

import com.wolcano.musicplayer.music.App;
import com.wolcano.musicplayer.music.di.module.ApplicationModule;
import com.wolcano.musicplayer.music.di.scope.PerApplication;

import java.lang.annotation.RetentionPolicy;

import dagger.Component;

@PerApplication
@Component(modules ={ApplicationModule.class})
public interface ApplicationComponent {

    Application application();

    App wolcanoApp();

}
