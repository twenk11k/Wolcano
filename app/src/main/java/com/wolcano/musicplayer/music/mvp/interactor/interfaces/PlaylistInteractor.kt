package com.wolcano.musicplayer.music.mvp.interactor.interfaces

import android.app.Activity
import com.wolcano.musicplayer.music.mvp.models.Playlist

interface PlaylistInteractor {

    interface OnGetPlaylistListener {
        fun sendPlaylists(playlistList: ArrayList<Playlist>?)
        fun controlIfEmpty()
    }

    fun getPlaylists(
        activity: Activity?,
        sort: String?,
        onGetPlaylistListener: OnGetPlaylistListener?
    )

}