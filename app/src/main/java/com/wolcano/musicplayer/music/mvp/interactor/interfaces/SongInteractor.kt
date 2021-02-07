package com.wolcano.musicplayer.music.mvp.interactor.interfaces

import android.app.Activity
import com.wolcano.musicplayer.music.mvp.models.Song

interface SongInteractor {

    interface OnGetSongListener {
        fun sendSongList(songList: ArrayList<Song>?)
        fun controlIfEmpty()
    }

    fun getSongs(activity: Activity?, sort: String?, onGetSongListener: OnGetSongListener?)
    fun getPlaylistSongs(
        activity: Activity?,
        sort: String?,
        playlistID: Long,
        onGetSongListener: OnGetSongListener?
    )

    fun getAlbumSongs(
        activity: Activity?,
        sort: String?,
        albumID: Long,
        onGetSongListener: OnGetSongListener?
    )

    fun getArtistSongs(
        activity: Activity?,
        sort: String?,
        artistID: Long,
        onGetSongListener: OnGetSongListener?
    )

    fun getGenreSongs(
        activity: Activity?,
        sort: String?,
        genreID: Long,
        onGetSongListener: OnGetSongListener?
    )

}