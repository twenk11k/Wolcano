package com.wolcano.musicplayer.music.di.module;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.wolcano.musicplayer.music.mvp.interactor.GenreInteractorImpl;
import com.wolcano.musicplayer.music.mvp.presenter.GenrePresenterImpl;
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.GenrePresenter;
import com.wolcano.musicplayer.music.mvp.view.GenreView;

import dagger.Module;
import dagger.Provides;

@Module
public class GenreModule {

    private GenreView view;
    private Fragment fragment;
    private Activity activity;
    private GenreInteractorImpl genreInteractor;
    private String sort;

    public GenreModule(GenreView view, Fragment fragment, Activity activity, String sort, GenreInteractorImpl genreInteractor) {
        this.view = view;
        this.fragment = fragment;
        this.activity = activity;
        this.genreInteractor = genreInteractor;
        this.sort = sort;
    }

    @Provides
    public GenreView provideView() {
        return view;
    }

    @Provides
    public GenrePresenter providePresenter() {
        return new GenrePresenterImpl(fragment, activity, sort, genreInteractor);
    }

}
