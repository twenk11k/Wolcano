package com.wolcano.musicplayer.music.di.module;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.wolcano.musicplayer.music.mvp.interactor.PlaylistInteractorImpl;
import com.wolcano.musicplayer.music.mvp.presenter.PlaylistPresenterImpl;
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.PlaylistPresenter;
import com.wolcano.musicplayer.music.mvp.view.PlaylistView;

import dagger.Module;
import dagger.Provides;

@Module
public class PlaylistModule {

    private PlaylistView view;
    private Fragment fragment;
    private Activity activity;
    private PlaylistInteractorImpl playlistInteractor;
    private String sort;


    public PlaylistModule(PlaylistView view,Fragment fragment,Activity activity,String sort,PlaylistInteractorImpl playlistInteractor){

        this.view = view;
        this.fragment = fragment;
        this.activity = activity;
        this.sort = sort;
        this.playlistInteractor = playlistInteractor;

    }

    @Provides
    public PlaylistView provideView(){
        return view;
    }

    @Provides
    public PlaylistPresenter providePresenter(){
        return new PlaylistPresenterImpl(fragment,activity,sort,playlistInteractor);
    }


}
