package com.wolcano.musicplayer.music.mvp.listener

import com.wolcano.musicplayer.music.mvp.models.Song

interface OnServiceListener {
    fun onBufferingUpdate(percent: Int)
    fun onChangeSong(song: Song?)
    fun onPlayStart()
    fun onPlayPause()
    fun onProgressChange(progress: Int)
}