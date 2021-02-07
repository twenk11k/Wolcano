package com.wolcano.musicplayer.music.mvp.presenter

import android.app.Activity
import androidx.fragment.app.Fragment
import com.wolcano.musicplayer.music.mvp.interactor.AlbumInteractorImpl
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.AlbumInteractor
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.AlbumInteractor.OnGetAlbumListener
import com.wolcano.musicplayer.music.mvp.models.Album
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.AlbumPresenter
import com.wolcano.musicplayer.music.ui.fragment.library.FragmentAlbums

class AlbumPresenterImpl(
    private val fragment: Fragment,
    private val activity: Activity,
    private val sort: String,
    albumInteractor: AlbumInteractorImpl
) : AlbumPresenter, OnGetAlbumListener {
    private val albumInteractor: AlbumInteractor
    override val albums: Unit
        get() {
            albumInteractor.getAlbum(activity, sort, this)
        }

    override fun sendAlbum(albumList: ArrayList<Album>?) {
        if (fragment is FragmentAlbums) {
            fragment.setAlbumList(albumList)
        }
    }

    override fun controlIfEmpty() {
        if (fragment is FragmentAlbums) {
            fragment.controlIfEmpty()
        }
    }

    init {
        this.albumInteractor = albumInteractor
    }
}