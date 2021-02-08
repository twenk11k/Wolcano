package com.wolcano.musicplayer.music.di.module;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.wolcano.musicplayer.music.mvp.interactor.SongInteractorImpl;
import com.wolcano.musicplayer.music.mvp.presenter.SongPresenterImpl;
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.SongPresenter;
import com.wolcano.musicplayer.music.mvp.view.SongView;

import dagger.Module;
import dagger.Provides;

@Module
public class GenreSongModule {

    private SongView view;
    private Fragment fragment;
    private Activity activity;
    private String sort;
    private long genreID;
    private SongInteractorImpl songInteractor;

    public GenreSongModule(SongView view, Fragment fragment, Activity activity, String sort, long genreID, SongInteractorImpl songInteractor) {
        this.view = view;
        this.fragment = fragment;
        this.activity = activity;
        this.songInteractor = songInteractor;
        this.sort = sort;
        this.genreID = genreID;
    }

    @Provides
    public SongView provideView(){
        return view;
    }

    @Provides
    public SongPresenter providePresenter(){
        return new SongPresenterImpl(fragment,activity,sort,genreID,songInteractor);
    }

}
