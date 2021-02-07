package com.wolcano.musicplayer.music.mvp.presenter

import android.app.Activity
import androidx.fragment.app.Fragment
import com.wolcano.musicplayer.music.mvp.interactor.PlaylistInteractorImpl
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.PlaylistInteractor
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.PlaylistInteractor.OnGetPlaylistListener
import com.wolcano.musicplayer.music.mvp.models.Playlist
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.PlaylistPresenter
import com.wolcano.musicplayer.music.ui.fragment.FragmentPlaylist

class PlaylistPresenterImpl(
    private val fragment: Fragment,
    private val activity: Activity,
    private val sort: String,
    playlistInteractor: PlaylistInteractorImpl
) : PlaylistPresenter, OnGetPlaylistListener {
    private val playlistInteractor: PlaylistInteractor

    override fun sendPlaylists(playlistList: ArrayList<Playlist>?) {
        if (fragment is FragmentPlaylist) {
            fragment.setPlaylistList(playlistList)
        }
    }

    override fun controlIfEmpty() {
        if (fragment is FragmentPlaylist) {
            fragment.controlIfEmpty()
        }
    }

    override val playlists: Unit
        get() {
            playlistInteractor.getPlaylists(activity, sort, this)
        }

    init {
        this.playlistInteractor = playlistInteractor
    }
}