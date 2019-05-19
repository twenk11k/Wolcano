package com.wolcano.musicplayer.music.mvp.listener;


import com.wolcano.musicplayer.music.mvp.models.Song;

public interface OnServiceListener {

    void onBufferingUpdate(int percent);

    void onChangeSong(Song song);

    void onPlayStart();

    void onPlayPause();

    void onProgressChange(int progress);


}
