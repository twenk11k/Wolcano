package com.wolcano.musicplayer.music.listener

import com.wolcano.musicplayer.music.data.model.Song

interface PlaylistListener {
    fun handlePlaylistDialog(song: Song?)
}