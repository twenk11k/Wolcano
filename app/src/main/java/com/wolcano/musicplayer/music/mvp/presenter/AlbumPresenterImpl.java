package com.wolcano.musicplayer.music.mvp.presenter;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.wolcano.musicplayer.music.mvp.interactor.AlbumInteractorImpl;
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.AlbumInteractor;
import com.wolcano.musicplayer.music.mvp.models.Album;
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.AlbumPresenter;
import com.wolcano.musicplayer.music.ui.fragment.library.FragmentAlbums;

import java.util.List;

import io.reactivex.disposables.Disposable;

public class AlbumPresenterImpl implements AlbumPresenter, AlbumInteractor.OnGetAlbumListener {

    private Fragment fragment;
    private Activity activity;
    private String sort;
    private Disposable disposable;
    private AlbumInteractor albumInteractor;

    public AlbumPresenterImpl(Fragment fragment, Activity activity, Disposable disposable, String sort, AlbumInteractorImpl albumInteractor) {

        this.fragment = fragment;
        this.activity = activity;
        this.disposable = disposable;
        this.sort = sort;
        this.albumInteractor = albumInteractor;

    }

    @Override
    public void getAlbums() {
        albumInteractor.getAlbum(activity,sort,this);

    }

    @Override
    public void sendAlbum(List<Album> albumList) {
        if(fragment instanceof FragmentAlbums){
            FragmentAlbums fragmentAlbums = (FragmentAlbums) fragment;
            fragmentAlbums.setAlbumList(albumList);
        }
    }

    @Override
    public void controlIfEmpty() {
        if(fragment instanceof FragmentAlbums){
            FragmentAlbums fragmentAlbums = (FragmentAlbums) fragment;
            fragmentAlbums.controlIfEmpty();
        }
    }


}
