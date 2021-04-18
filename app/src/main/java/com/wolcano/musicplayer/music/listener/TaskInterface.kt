package com.wolcano.musicplayer.music.listener

import com.wolcano.musicplayer.music.data.model.Song

interface TaskInterface<T> {
    fun onPrepare()
    fun onTaskDone(songList: List<Song>?)
    fun onTask()
    fun onTaskFail(e: Exception?)
}