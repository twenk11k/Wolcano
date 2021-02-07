package com.wolcano.musicplayer.music.mvp.view

import com.wolcano.musicplayer.music.mvp.models.Song

interface SongView {
    fun setSongList(songList: ArrayList<Song>?)
}