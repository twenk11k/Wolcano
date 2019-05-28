package com.wolcano.musicplayer.music.di.component;

import com.wolcano.musicplayer.music.di.module.PlaylistSongModule;
import com.wolcano.musicplayer.music.di.scope.PerActivity;
import com.wolcano.musicplayer.music.ui.fragment.library.detail.FragmentPlaylistDetail;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class,modules = PlaylistSongModule.class)
public interface PlaylistSongComponent {
    void inject(FragmentPlaylistDetail fragmentPlaylistDetail);
}
