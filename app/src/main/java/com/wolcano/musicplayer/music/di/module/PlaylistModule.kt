package com.wolcano.musicplayer.music.di.module

import android.app.Activity
import androidx.fragment.app.Fragment
import com.wolcano.musicplayer.music.mvp.interactor.PlaylistInteractorImpl
import com.wolcano.musicplayer.music.mvp.presenter.PlaylistPresenterImpl
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.PlaylistPresenter
import com.wolcano.musicplayer.music.mvp.view.PlaylistView
import dagger.Module
import dagger.Provides

@Module
class PlaylistModule(
    private val view: PlaylistView,
    private val fragment: Fragment,
    private val activity: Activity,
    private val sort: String,
    private val playlistInteractor: PlaylistInteractorImpl
) {
    @Provides
    fun provideView(): PlaylistView {
        return view
    }

    @Provides
    fun providePresenter(): PlaylistPresenter {
        return PlaylistPresenterImpl(fragment, activity, sort, playlistInteractor)
    }
}