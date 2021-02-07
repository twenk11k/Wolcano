package com.wolcano.musicplayer.music.mvp.presenter

import android.app.Activity
import androidx.fragment.app.Fragment
import com.wolcano.musicplayer.music.mvp.interactor.GenreInteractorImpl
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.GenreInteractor
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.GenreInteractor.OnGetGenreListener
import com.wolcano.musicplayer.music.mvp.models.Genre
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.GenrePresenter
import com.wolcano.musicplayer.music.ui.fragment.library.FragmentGenres

class GenrePresenterImpl(
    private val fragment: Fragment,
    private val activity: Activity,
    private val sort: String,
    genreInteractor: GenreInteractorImpl
) : GenrePresenter, OnGetGenreListener {
    private val genreInteractor: GenreInteractor
    override val genres: Unit
        get() {
            genreInteractor.getGenres(activity, sort, this)
        }

    override fun sendGenres(genreList: ArrayList<Genre>?) {
        if (fragment is FragmentGenres) {
            fragment.setGenreList(genreList)
        }
    }

    override fun controlIfEmpty() {
        if (fragment is FragmentGenres) {
            fragment.controlIfEmpty()
        }
    }

    init {
        this.genreInteractor = genreInteractor
    }
}