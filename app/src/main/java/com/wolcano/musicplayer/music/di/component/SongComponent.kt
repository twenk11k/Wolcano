package com.wolcano.musicplayer.music.di.component;


import com.wolcano.musicplayer.music.di.module.SongModule;
import com.wolcano.musicplayer.music.di.scope.PerActivity;
import com.wolcano.musicplayer.music.ui.fragment.FragmentRecently;
import com.wolcano.musicplayer.music.ui.fragment.library.FragmentSongs;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {SongModule.class})
public interface SongComponent {

    void inject(FragmentSongs fragmentSongs);
    void inject(FragmentRecently fragmentRecently);

}
