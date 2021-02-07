package com.wolcano.musicplayer.music.mvp.presenter;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.wolcano.musicplayer.music.mvp.interactor.SongInteractorImpl;
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.SongInteractor;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.SongPresenter;
import com.wolcano.musicplayer.music.ui.fragment.FragmentRecently;
import com.wolcano.musicplayer.music.ui.fragment.library.FragmentSongs;
import com.wolcano.musicplayer.music.ui.fragment.library.detail.FragmentAlbumDetail;
import com.wolcano.musicplayer.music.ui.fragment.library.detail.FragmentArtistDetail;
import com.wolcano.musicplayer.music.ui.fragment.library.detail.FragmentGenreDetail;
import com.wolcano.musicplayer.music.ui.fragment.library.detail.FragmentPlaylistDetail;

import java.util.List;


public class SongPresenterImpl implements SongPresenter, SongInteractor.OnGetSongListener {

    private Fragment fragment;
    private SongInteractor songInteractor;
    private String sort;
    private Activity activity;
    private long id;

    public SongPresenterImpl(Fragment fragment,Activity activity, String sort, SongInteractorImpl songInteractor){

        this.fragment = fragment;
        this.songInteractor = songInteractor;
        this.sort = sort;
        this.activity = activity;

    }

    public SongPresenterImpl(Fragment fragment,Activity activity, String sort,long id, SongInteractorImpl songInteractor){

        this.fragment = fragment;
        this.songInteractor = songInteractor;
        this.sort = sort;
        this.activity = activity;
        this.id = id;

    }

    @Override
    public void getSongs() {
        songInteractor.getSongs(activity,sort,this);
    }

    @Override
    public void getPlaylistSongs() {
        songInteractor.getPlaylistSongs(activity,sort,id,this);
    }
    @Override
    public void getAlbumSongs() {
        songInteractor.getAlbumSongs(activity,sort,id,this);
    }

    @Override
    public void getArtistSongs() {
        songInteractor.getArtistSongs(activity,sort,id,this);

    }

    @Override
    public void getGenreSongs() {

        songInteractor.getGenreSongs(activity,sort,id,this);

    }

    @Override
    public void sendSongList(List<Song> songList) {
        if(fragment instanceof FragmentRecently){
            FragmentRecently fragmentRecently =  (FragmentRecently) fragment;
            fragmentRecently.setSongList(songList);
        } else if(fragment instanceof FragmentSongs){
            FragmentSongs fragmentSongs = (FragmentSongs) fragment;
            fragmentSongs.setSongList(songList);
        } else if(fragment instanceof FragmentPlaylistDetail){
            FragmentPlaylistDetail fragmentPlaylistDetail = (FragmentPlaylistDetail) fragment;
            fragmentPlaylistDetail.setSongList(songList);
        } else if(fragment instanceof FragmentAlbumDetail){
            FragmentAlbumDetail fragmentAlbumDetail = (FragmentAlbumDetail) fragment;
            fragmentAlbumDetail.setSongList(songList);
        } else if(fragment instanceof FragmentArtistDetail){
            FragmentArtistDetail fragmentArtistDetail = (FragmentArtistDetail) fragment;
            fragmentArtistDetail.setSongList(songList);
        } else if(fragment instanceof FragmentGenreDetail) {
            FragmentGenreDetail fragmentGenreDetail = (FragmentGenreDetail) fragment;
            fragmentGenreDetail.setSongList(songList);
        }

    }

    @Override
    public void controlIfEmpty() {
        if(fragment instanceof FragmentRecently){
            FragmentRecently fragmentRecently =  (FragmentRecently) fragment;
            fragmentRecently.controlIfEmpty();
        } else if(fragment instanceof FragmentSongs){
            FragmentSongs fragmentSongs = (FragmentSongs) fragment;
            fragmentSongs.controlIfEmpty();
        } else if(fragment instanceof FragmentPlaylistDetail){
            FragmentPlaylistDetail fragmentPlaylistDetail = (FragmentPlaylistDetail) fragment;
            fragmentPlaylistDetail.controlIfEmpty();
        } else if(fragment instanceof FragmentArtistDetail){
            FragmentArtistDetail fragmentArtistDetail = (FragmentArtistDetail) fragment;
            fragmentArtistDetail.controlIfEmpty();
        } else if(fragment instanceof FragmentGenreDetail){
            FragmentGenreDetail fragmentGenreDetail = (FragmentGenreDetail) fragment;
            fragmentGenreDetail.controlIfEmpty();
        }
    }
}
