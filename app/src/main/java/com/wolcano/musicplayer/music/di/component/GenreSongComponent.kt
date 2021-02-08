package com.wolcano.musicplayer.music.di.component;

import com.wolcano.musicplayer.music.di.module.GenreSongModule;
import com.wolcano.musicplayer.music.di.scope.PerActivity;
import com.wolcano.musicplayer.music.ui.fragment.library.detail.FragmentGenreDetail;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = GenreSongModule.class)
public interface GenreSongComponent {
    void inject(FragmentGenreDetail fragmentGenreDetail);
}
