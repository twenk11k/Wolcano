package com.wolcano.musicplayer.music.mvp.presenter;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.wolcano.musicplayer.music.mvp.interactor.PlaylistInteractorImpl;
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.PlaylistInteractor;
import com.wolcano.musicplayer.music.mvp.models.Playlist;
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.PlaylistPresenter;
import com.wolcano.musicplayer.music.ui.fragment.FragmentPlaylist;

import java.util.List;


public class PlaylistPresenterImpl implements PlaylistPresenter, PlaylistInteractor.OnGetPlaylistListener {

    private Fragment fragment;
    private PlaylistInteractor playlistInteractor;
    private String sort;
    private Activity activity;

    public PlaylistPresenterImpl(Fragment fragment, Activity activity, String sort, PlaylistInteractorImpl playlistInteractor){
        this.fragment = fragment;
        this.activity = activity;
        this.sort = sort;
        this.playlistInteractor = playlistInteractor;
    }

    @Override
    public void sendPlaylists(List<Playlist> playlistList) {
        if(fragment instanceof FragmentPlaylist){
            FragmentPlaylist fragmentPlaylist = (FragmentPlaylist) fragment;
            fragmentPlaylist.setPlaylistList(playlistList);
        }
    }

    @Override
    public void controlIfEmpty() {
        if(fragment instanceof FragmentPlaylist){
            FragmentPlaylist fragmentPlaylist = (FragmentPlaylist) fragment;
            fragmentPlaylist.controlIfEmpty();
        }
    }

    @Override
    public void getPlaylists() {
        playlistInteractor.getPlaylists(activity,sort,this);
    }
}
