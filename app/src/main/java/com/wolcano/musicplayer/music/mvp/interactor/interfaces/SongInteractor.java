package com.wolcano.musicplayer.music.mvp.interactor.interfaces;

import android.app.Activity;

import com.wolcano.musicplayer.music.mvp.models.Song;

import java.util.List;

import io.reactivex.disposables.Disposable;

public interface SongInteractor {

    interface OnGetSongListener{
        void sendSongs(List<Song> songList);
        void controlIfEmpty();
    }

    void getSongs(Activity activity, Disposable disposable,String sort, OnGetSongListener onGetSongListener);
    void getPlaylistSongs(Activity activity,Disposable disposable,String sort,long playlistID,OnGetSongListener onGetSongListener);


}
