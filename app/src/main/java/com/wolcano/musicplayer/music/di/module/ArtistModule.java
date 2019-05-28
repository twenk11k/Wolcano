package com.wolcano.musicplayer.music.di.module;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.wolcano.musicplayer.music.mvp.interactor.ArtistInteractorImpl;
import com.wolcano.musicplayer.music.mvp.presenter.ArtistPresenterImpl;
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.ArtistPresenter;
import com.wolcano.musicplayer.music.mvp.view.ArtistView;

import dagger.Module;
import dagger.Provides;

@Module
public class ArtistModule {

    private ArtistView view;
    private Fragment fragment;
    private Activity activity;
    private ArtistInteractorImpl artistInteractor;
    private String sort;

    public ArtistModule(ArtistView view, Fragment fragment, Activity activity, String sort, ArtistInteractorImpl artistInteractor) {
        this.view = view;
        this.fragment = fragment;
        this.activity = activity;
        this.sort = sort;
        this.artistInteractor = artistInteractor;
    }

    @Provides
    public ArtistView provideView(){
        return view;
    }

    @Provides
    public ArtistPresenter providePresenter(){
        return new ArtistPresenterImpl(fragment,activity,sort,artistInteractor);
    }
}
