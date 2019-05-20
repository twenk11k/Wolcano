package com.wolcano.musicplayer.music.mvp.interactor.interfaces;

import android.app.Activity;

import com.wolcano.musicplayer.music.mvp.models.Genre;
import java.util.List;
import io.reactivex.disposables.Disposable;


public interface GenreInteractor {

<<<<<<< HEAD
    interface OnGetGenreListener{
=======
    interface OnGenreListener{
>>>>>>> 88390bf391eb92b417823436dc215e2c14b0fd4a
        void sendGenres(List<Genre> genreList);
        void controlIfEmpty();
    }

<<<<<<< HEAD
    void getGenres(Activity activity, Disposable disposable, String sort, OnGetGenreListener OnGetGenreListener);
=======
    void getGenres(Activity activity, Disposable disposable, String sort, OnGenreListener onGenreListener);
>>>>>>> 88390bf391eb92b417823436dc215e2c14b0fd4a

}
