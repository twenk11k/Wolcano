package com.wolcano.musicplayer.music.mvp.presenter;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.wolcano.musicplayer.music.mvp.interactor.GenreInteractorImpl;
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.GenreInteractor;
import com.wolcano.musicplayer.music.mvp.models.Genre;
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.GenrePresenter;
import com.wolcano.musicplayer.music.ui.fragment.library.FragmentGenres;

import java.util.List;

public class GenrePresenterImpl implements GenrePresenter, GenreInteractor.OnGetGenreListener {

    private Fragment fragment;
    private Activity activity;
    private String sort;
    private GenreInteractor genreInteractor;

    public GenrePresenterImpl(Fragment fragment, Activity activity, String sort, GenreInteractorImpl genreInteractor) {

        this.fragment = fragment;
        this.activity = activity;
        this.sort = sort;
        this.genreInteractor = genreInteractor;
    }

    @Override
    public void getGenres() {
        genreInteractor.getGenres(activity,sort,this);
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
