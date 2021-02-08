package com.wolcano.musicplayer.music.di.component;

import com.wolcano.musicplayer.music.di.module.ArtistSongModule;
import com.wolcano.musicplayer.music.di.scope.PerActivity;
import com.wolcano.musicplayer.music.ui.fragment.library.detail.FragmentArtistDetail;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class,modules = ArtistSongModule.class)
public interface ArtistSongComponent {
    void inject(FragmentArtistDetail fragmentArtistDetail);
}
