package com.wolcano.musicplayer.music.mvp.view;

import com.wolcano.musicplayer.music.mvp.models.Album;
import com.wolcano.musicplayer.music.mvp.models.Artist;

import java.util.List;

public interface AlbumView {
    void setAlbumList(List<Album> albumList);
}
