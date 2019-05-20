package com.wolcano.musicplayer.music.mvp.interactor.interfaces;

import android.app.Activity;

import com.wolcano.musicplayer.music.mvp.models.Genre;
import java.util.List;
import io.reactivex.disposables.Disposable;


public interface GenreInteractor {

    interface OnGenreListener{
        void sendGenres(List<Genre> genreList);
        void controlIfEmpty();
    }

    void getGenres(Activity activity, Disposable disposable, String sort, OnGenreListener onGenreListener);

}
