package com.wolcano.musicplayer.music.di.module

import android.app.Activity
import androidx.fragment.app.Fragment
import com.wolcano.musicplayer.music.mvp.interactor.ArtistInteractorImpl
import com.wolcano.musicplayer.music.mvp.presenter.ArtistPresenterImpl
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.ArtistPresenter
import com.wolcano.musicplayer.music.mvp.view.ArtistView
import dagger.Module
import dagger.Provides

@Module
class ArtistModule(
    private val view: ArtistView,
    private val fragment: Fragment,
    private val activity: Activity,
    private val sort: String,
    private val artistInteractor: ArtistInteractorImpl
) {
    @Provides
    fun provideView(): ArtistView {
        return view
    }

    @Provides
    fun providePresenter(): ArtistPresenter {
        return ArtistPresenterImpl(fragment, activity, sort, artistInteractor)
    }
}