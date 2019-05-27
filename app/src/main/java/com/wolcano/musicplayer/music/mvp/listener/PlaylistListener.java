package com.wolcano.musicplayer.music.mvp.listener;

import com.wolcano.musicplayer.music.mvp.models.Song;

public interface PlaylistListener {
    void handlePlaylistDialog(Song song);
}
