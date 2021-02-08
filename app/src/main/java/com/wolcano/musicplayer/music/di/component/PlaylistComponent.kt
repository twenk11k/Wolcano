package com.wolcano.musicplayer.music.di.component

import com.wolcano.musicplayer.music.di.module.PlaylistModule
import com.wolcano.musicplayer.music.di.scope.PerActivity
import com.wolcano.musicplayer.music.ui.fragment.FragmentPlaylist
import dagger.Component

@PerActivity
@Component(dependencies = [ApplicationComponent::class], modules = [PlaylistModule::class])
interface PlaylistComponent {
    fun inject(fragmentPlaylist: FragmentPlaylist?)
}