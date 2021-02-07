package com.wolcano.musicplayer.music.mvp.presenter

import android.app.Activity
import androidx.fragment.app.Fragment
import com.wolcano.musicplayer.music.mvp.interactor.SongInteractorImpl
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.SongInteractor
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.SongInteractor.OnGetSongListener
import com.wolcano.musicplayer.music.mvp.models.Song
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.SongPresenter
import com.wolcano.musicplayer.music.ui.fragment.FragmentRecently
import com.wolcano.musicplayer.music.ui.fragment.library.FragmentSongs
import com.wolcano.musicplayer.music.ui.fragment.library.detail.FragmentAlbumDetail
import com.wolcano.musicplayer.music.ui.fragment.library.detail.FragmentArtistDetail
import com.wolcano.musicplayer.music.ui.fragment.library.detail.FragmentGenreDetail
import com.wolcano.musicplayer.music.ui.fragment.library.detail.FragmentPlaylistDetail

class SongPresenterImpl : SongPresenter, OnGetSongListener {
    private var fragment: Fragment
    private var songInteractor: SongInteractor
    private var sort: String
    private var activity: Activity
    private var id: Long = 0

    constructor(
        fragment: Fragment,
        activity: Activity,
        sort: String,
        songInteractor: SongInteractorImpl
    ) {
        this.fragment = fragment
        this.songInteractor = songInteractor
        this.sort = sort
        this.activity = activity
    }

    constructor(
        fragment: Fragment,
        activity: Activity,
        sort: String,
        id: Long,
        songInteractor: SongInteractorImpl
    ) {
        this.fragment = fragment
        this.songInteractor = songInteractor
        this.sort = sort
        this.activity = activity
        this.id = id
    }

    override val songs: Unit
        get() {
            songInteractor.getSongs(activity, sort, this)
        }
    override val playlistSongs: Unit
        get() {
            songInteractor.getPlaylistSongs(activity, sort, id, this)
        }
    override val albumSongs: Unit
        get() {
            songInteractor.getAlbumSongs(activity, sort, id, this)
        }
    override val artistSongs: Unit
        get() {
            songInteractor.getArtistSongs(activity, sort, id, this)
        }
    override val genreSongs: Unit
        get() {
            songInteractor.getGenreSongs(activity, sort, id, this)
        }

    override fun sendSongList(songList: ArrayList<Song>?) {
        if (fragment is FragmentRecently) {
            val fragmentRecently = fragment as FragmentRecently
            fragmentRecently.setSongList(songList)
        } else if (fragment is FragmentSongs) {
            val fragmentSongs = fragment as FragmentSongs
            fragmentSongs.setSongList(songList)
        } else if (fragment is FragmentPlaylistDetail) {
            val fragmentPlaylistDetail = fragment as FragmentPlaylistDetail
            fragmentPlaylistDetail.setSongList(songList)
        } else if (fragment is FragmentAlbumDetail) {
            val fragmentAlbumDetail = fragment as FragmentAlbumDetail
            fragmentAlbumDetail.setSongList(songList)
        } else if (fragment is FragmentArtistDetail) {
            val fragmentArtistDetail = fragment as FragmentArtistDetail
            fragmentArtistDetail.setSongList(songList)
        } else if (fragment is FragmentGenreDetail) {
            val fragmentGenreDetail = fragment as FragmentGenreDetail
            fragmentGenreDetail.setSongList(songList)
        }
    }

    override fun controlIfEmpty() {
        if (fragment is FragmentRecently) {
            val fragmentRecently = fragment as FragmentRecently
            fragmentRecently.controlIfEmpty()
        } else if (fragment is FragmentSongs) {
            val fragmentSongs = fragment as FragmentSongs
            fragmentSongs.controlIfEmpty()
        } else if (fragment is FragmentPlaylistDetail) {
            val fragmentPlaylistDetail = fragment as FragmentPlaylistDetail
            fragmentPlaylistDetail.controlIfEmpty()
        } else if (fragment is FragmentArtistDetail) {
            val fragmentArtistDetail = fragment as FragmentArtistDetail
            fragmentArtistDetail.controlIfEmpty()
        } else if (fragment is FragmentGenreDetail) {
            val fragmentGenreDetail = fragment as FragmentGenreDetail
            fragmentGenreDetail.controlIfEmpty()
        }
    }
}