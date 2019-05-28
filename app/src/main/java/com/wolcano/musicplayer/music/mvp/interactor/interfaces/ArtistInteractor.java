package com.wolcano.musicplayer.music.mvp.interactor.interfaces;

import android.app.Activity;

import com.wolcano.musicplayer.music.mvp.models.Artist;
import java.util.List;
import io.reactivex.disposables.Disposable;

public interface ArtistInteractor {

    interface OnGetArtistListener{
        void sendArtist(List<Artist> artistList);
        void controlIfEmpty();
    }

    void getArtist(Activity activity, String sort, OnGetArtistListener onGetArtistListener);


}
