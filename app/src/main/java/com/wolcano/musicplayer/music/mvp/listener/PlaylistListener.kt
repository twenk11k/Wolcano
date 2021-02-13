package com.wolcano.musicplayer.music.mvp.listener

import com.wolcano.musicplayer.music.model.Song

interface PlaylistListener {
    fun handlePlaylistDialog(song: Song?)
}