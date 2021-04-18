package com.wolcano.musicplayer.music.ui.adapter

import com.wolcano.musicplayer.music.model.SongOnline

interface OnlineAdapterClickListener {
    fun performDownload(song: SongOnline)
    fun copySongInfo(song: SongOnline)
    fun playSongOnline(items: List<SongOnline>, position: Int)
}