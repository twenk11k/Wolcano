package com.wolcano.musicplayer.music.mvp.view

import com.wolcano.musicplayer.music.mvp.models.Playlist

interface PlaylistView {
    fun setPlaylistList(playlistList: MutableList<Playlist>?)
}