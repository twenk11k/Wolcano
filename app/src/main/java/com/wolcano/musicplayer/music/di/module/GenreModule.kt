package com.wolcano.musicplayer.music.di.module

import android.app.Activity
import androidx.fragment.app.Fragment
import com.wolcano.musicplayer.music.mvp.interactor.GenreInteractorImpl
import com.wolcano.musicplayer.music.mvp.presenter.GenrePresenterImpl
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.GenrePresenter
import com.wolcano.musicplayer.music.mvp.view.GenreView
import dagger.Module
import dagger.Provides

@Module
class GenreModule(
    private val view: GenreView,
    private val fragment: Fragment,
    private val activity: Activity,
    private val sort: String,
    private val genreInteractor: GenreInteractorImpl
) {
    @Provides
    fun provideView(): GenreView {
        return view
    }

    @Provides
    fun providePresenter(): GenrePresenter {
        return GenrePresenterImpl(fragment, activity, sort, genreInteractor)
    }
}