package com.wolcano.musicplayer.music.di.module;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.wolcano.musicplayer.music.mvp.interactor.AlbumInteractorImpl;
import com.wolcano.musicplayer.music.mvp.presenter.AlbumPresenterImpl;
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.AlbumPresenter;
import com.wolcano.musicplayer.music.mvp.view.AlbumView;
import dagger.Module;
import dagger.Provides;

@Module
public class AlbumModule {

    private AlbumView view;
    private Fragment fragment;
    private Activity activity;
    private AlbumInteractorImpl artistInteractor;
    private String sort;


    public AlbumModule(AlbumView view, Fragment fragment, Activity activity, String sort, AlbumInteractorImpl artistInteractor) {
        this.view = view;
        this.fragment = fragment;
        this.activity = activity;
        this.sort = sort;
        this.artistInteractor = artistInteractor;
    }

    @Provides
    public AlbumView provideView(){
        return view;
    }

    @Provides
    public AlbumPresenter providePresenter(){
        return new AlbumPresenterImpl(fragment,activity,sort,artistInteractor);
    }
}
