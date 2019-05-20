package com.wolcano.musicplayer.music.mvp.interactor.interfaces;

import android.app.Activity;

import com.wolcano.musicplayer.music.mvp.models.Album;

import java.util.List;

import io.reactivex.disposables.Disposable;

public interface AlbumInteractor {

    interface OnGetAlbumListener{
        void sendAlbum(List<Album> albumList);
        void controlIfEmpty();
    }

    void getAlbum(Activity activity, Disposable disposable, String sort, OnGetAlbumListener onGetAlbumListener);

}
