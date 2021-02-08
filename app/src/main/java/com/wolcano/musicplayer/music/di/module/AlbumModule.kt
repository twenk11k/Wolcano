package com.wolcano.musicplayer.music.di.module

import android.app.Activity
import androidx.fragment.app.Fragment
import com.wolcano.musicplayer.music.mvp.interactor.AlbumInteractorImpl
import com.wolcano.musicplayer.music.mvp.presenter.AlbumPresenterImpl
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.AlbumPresenter
import com.wolcano.musicplayer.music.mvp.view.AlbumView
import dagger.Module
import dagger.Provides

@Module
class AlbumModule(
    private val view: AlbumView,
    private val fragment: Fragment,
    private val activity: Activity,
    private val sort: String,
    private val artistInteractor: AlbumInteractorImpl
) {
    @Provides
    fun provideView(): AlbumView {
        return view
    }

    @Provides
    fun providePresenter(): AlbumPresenter {
        return AlbumPresenterImpl(fragment, activity, sort, artistInteractor)
    }
}