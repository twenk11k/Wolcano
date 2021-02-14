package com.wolcano.musicplayer.music.listener

import com.wolcano.musicplayer.music.model.Song

interface TaskInterface<T> {
    fun onPrepare()
    fun onTaskDone(songList: List<Song>?)
    fun onTask()
    fun onTaskFail(e: Exception?)
}