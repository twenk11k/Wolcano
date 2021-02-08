package com.wolcano.musicplayer.music.di.component;

import com.wolcano.musicplayer.music.di.module.AlbumModule;
import com.wolcano.musicplayer.music.di.scope.PerActivity;
import com.wolcano.musicplayer.music.ui.fragment.library.FragmentAlbums;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class,modules = AlbumModule.class)
public interface AlbumComponent {
    void inject(FragmentAlbums fragmentAlbums);
}
