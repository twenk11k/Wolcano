package com.wolcano.musicplayer.music.mvp.listener;


import com.wolcano.musicplayer.music.mvp.models.Song;

import java.util.List;


public interface TaskInterface<T> {
    void onPrepare();
    void onTaskDone(List<Song> songList);
    void onTask();
    void onTaskFail(Exception e);
}
