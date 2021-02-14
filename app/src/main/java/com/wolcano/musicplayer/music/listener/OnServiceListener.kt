package com.wolcano.musicplayer.music.listener

import com.wolcano.musicplayer.music.model.Song

interface OnServiceListener {
    fun onBufferingUpdate(percent: Int)
    fun onChangeSong(song: Song?)
    fun onPlayStart()
    fun onPlayPause()
    fun onProgressChange(progress: Int)
}