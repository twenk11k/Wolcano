package com.wolcano.musicplayer.music.mvp.interactor.interfaces;

import android.app.Activity;

import com.wolcano.musicplayer.music.mvp.models.Genre;
import java.util.List;
import io.reactivex.disposables.Disposable;


public interface GenreInteractor {

    interface OnGetGenreListener{
        void sendGenres(List<Genre> genreList);
        void controlIfEmpty();
    }

    void getGenres(Activity activity, String sort, OnGetGenreListener OnGetGenreListener);

}
