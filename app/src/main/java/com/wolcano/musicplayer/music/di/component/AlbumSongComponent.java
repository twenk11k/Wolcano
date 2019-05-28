package com.wolcano.musicplayer.music.di.component;

import com.wolcano.musicplayer.music.di.module.AlbumSongModule;
import com.wolcano.musicplayer.music.di.scope.PerActivity;
import com.wolcano.musicplayer.music.ui.fragment.library.detail.FragmentAlbumDetail;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class,modules = AlbumSongModule.class)
public interface AlbumSongComponent {
    void inject(FragmentAlbumDetail fragmentAlbumDetail);
}
