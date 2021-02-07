package com.wolcano.musicplayer.music.mvp.interactor.interfaces;

import android.app.Activity;

import com.wolcano.musicplayer.music.mvp.models.Playlist;

import java.util.List;

import io.reactivex.disposables.Disposable;

public interface PlaylistInteractor {

    interface OnGetPlaylistListener {
        void sendPlaylists(List<Playlist> playlistList);
        void controlIfEmpty();
    }


    void getPlaylists(Activity activity,String sort,OnGetPlaylistListener onGetPlaylistListener);


}
