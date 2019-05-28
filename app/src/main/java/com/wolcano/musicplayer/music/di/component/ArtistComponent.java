package com.wolcano.musicplayer.music.di.component;

import com.wolcano.musicplayer.music.di.module.ArtistModule;
import com.wolcano.musicplayer.music.di.scope.PerActivity;
import com.wolcano.musicplayer.music.ui.fragment.library.FragmentArtists;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class,modules = ArtistModule.class)
public interface ArtistComponent {

    void inject(FragmentArtists fragmentArtists);

}
