package com.wolcano.musicplayer.music.di.module

import android.app.Activity
import androidx.fragment.app.Fragment
import com.wolcano.musicplayer.music.mvp.interactor.SongInteractorImpl
import com.wolcano.musicplayer.music.mvp.presenter.SongPresenterImpl
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.SongPresenter
import com.wolcano.musicplayer.music.mvp.view.SongView
import dagger.Module
import dagger.Provides

@Module
class SongModule(
    private val view: SongView,
    private val fragment: Fragment,
    private val activity: Activity,
    private val sort: String,
    private val songInteractor: SongInteractorImpl
) {
    @Provides
    fun provideView(): SongView {
        return view
    }

    @Provides
    fun providePresenter(): SongPresenter {
        return SongPresenterImpl(fragment, activity, sort, songInteractor)
    }
}