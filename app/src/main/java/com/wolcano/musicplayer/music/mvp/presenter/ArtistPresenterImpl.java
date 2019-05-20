package com.wolcano.musicplayer.music.mvp.presenter;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.wolcano.musicplayer.music.mvp.interactor.ArtistInteractorImpl;
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.ArtistInteractor;
import com.wolcano.musicplayer.music.mvp.models.Artist;
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.ArtistPresenter;
import com.wolcano.musicplayer.music.ui.fragments.library.FragmentArtists;

import java.util.List;

import io.reactivex.disposables.Disposable;

public class ArtistPresenterImpl implements ArtistPresenter, ArtistInteractor.OnGetArtistListener {

    private Fragment fragment;
    private Activity activity;
    private String sort;
    private Disposable disposable;
    private ArtistInteractor artistInteractor;

    public ArtistPresenterImpl(Fragment fragment, Activity activity, Disposable disposable, String sort, ArtistInteractorImpl artistInteractor) {

        this.fragment = fragment;
        this.activity = activity;
        this.disposable = disposable;
        this.sort = sort;
        this.artistInteractor = artistInteractor;

    }

    @Override
    public void getArtists() {
        artistInteractor.getArtist(activity,disposable,sort,this);
    }

    @Override
    public void sendArtist(List<Artist> artistList) {
        if(fragment instanceof FragmentArtists){
            FragmentArtists fragmentArtists = (FragmentArtists) fragment;
            fragmentArtists.setArtistList(artistList);
        }
    }

    @Override
    public void controlIfEmpty() {
        if(fragment instanceof FragmentArtists){
            FragmentArtists fragmentArtists = (FragmentArtists) fragment;
            fragmentArtists.controlIfEmpty();
        }
    }


}
