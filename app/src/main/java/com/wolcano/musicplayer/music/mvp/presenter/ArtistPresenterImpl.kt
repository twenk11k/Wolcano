package com.wolcano.musicplayer.music.mvp.presenter

import android.app.Activity
import androidx.fragment.app.Fragment
import com.wolcano.musicplayer.music.mvp.interactor.ArtistInteractorImpl
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.ArtistInteractor
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.ArtistInteractor.OnGetArtistListener
import com.wolcano.musicplayer.music.mvp.models.Artist
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.ArtistPresenter
import com.wolcano.musicplayer.music.ui.fragment.library.FragmentArtists

class ArtistPresenterImpl(
    private val fragment: Fragment,
    private val activity: Activity,
    private val sort: String,
    artistInteractor: ArtistInteractorImpl
) : ArtistPresenter, OnGetArtistListener {
    private val artistInteractor: ArtistInteractor
    override val artists: Unit
        get() {
            artistInteractor.getArtist(activity, sort, this)
        }

    override fun sendArtist(artistList: ArrayList<Artist>?) {
        if (fragment is FragmentArtists) {
            fragment.setArtistList(artistList)
        }
    }

    override fun controlIfEmpty() {
        if (fragment is FragmentArtists) {
            fragment.controlIfEmpty()
        }
    }

    init {
        this.artistInteractor = artistInteractor
    }
}