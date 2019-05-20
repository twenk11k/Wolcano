package com.wolcano.musicplayer.music.mvp.presenter;

import android.app.Activity;
import androidx.fragment.app.Fragment;
import com.wolcano.musicplayer.music.mvp.interactor.PlaylistInteractorImpl;
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.PlaylistInteractor;
import com.wolcano.musicplayer.music.mvp.models.Playlist;
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.PlaylistPresenter;
import com.wolcano.musicplayer.music.ui.fragments.FragmentPlaylist;
import java.util.List;
import io.reactivex.disposables.Disposable;


public class PlaylistPresenterImpl implements PlaylistPresenter, PlaylistInteractor.OnGetPlaylistListener {

    private Fragment fragment;
    private PlaylistInteractor playlistInteractor;
    private String sort;
    private Disposable disposable;
    private Activity activity;

    public PlaylistPresenterImpl(Fragment fragment, Activity activity, Disposable disposable, String sort, PlaylistInteractorImpl playlistInteractor){
        this.fragment = fragment;
        this.activity = activity;
        this.disposable = disposable;
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
        playlistInteractor.getPlaylists(activity,disposable,sort,this);
    }
}
