package com.wolcano.musicplayer.music.mvp.presenter;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.wolcano.musicplayer.music.mvp.interactor.GenreInteractorImpl;
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.GenreInteractor;
import com.wolcano.musicplayer.music.mvp.models.Genre;
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.GenrePresenter;
import com.wolcano.musicplayer.music.ui.fragments.library.FragmentGenres;

import java.util.List;

import io.reactivex.disposables.Disposable;

public class GenrePresenterImpl implements GenrePresenter, GenreInteractor.OnGenreListener {

    private Fragment fragment;
    private Activity activity;
    private Disposable disposable;
    private String sort;
    private GenreInteractor genreInteractor;


    public GenrePresenterImpl(Fragment fragment, Activity activity, Disposable disposable, String sort, GenreInteractorImpl genreInteractor) {

        this.fragment = fragment;
        this.activity = activity;
        this.disposable = disposable;
        this.sort = sort;
        this.genreInteractor = genreInteractor;
    }


    @Override
    public void getGenres() {
        genreInteractor.getGenres(activity,disposable,sort,this);
    }

    @Override
    public void sendGenres(List<Genre> genreList) {
        if(fragment instanceof FragmentGenres){
            FragmentGenres fragmentGenres = (FragmentGenres) fragment;
            fragmentGenres.setGenreList(genreList);
        }
    }

    @Override
    public void controlIfEmpty() {
        if(fragment instanceof FragmentGenres){
            FragmentGenres fragmentGenres = (FragmentGenres) fragment;
            fragmentGenres.controlIfEmpty();
        }
    }
}
