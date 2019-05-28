package com.wolcano.musicplayer.music.di.component;

import com.wolcano.musicplayer.music.di.module.GenreModule;
import com.wolcano.musicplayer.music.di.scope.PerActivity;
import com.wolcano.musicplayer.music.ui.fragment.library.FragmentGenres;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class,modules = GenreModule.class)
public interface GenreComponent {

    void inject(FragmentGenres fragmentGenres);

}
