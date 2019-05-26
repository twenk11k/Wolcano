package com.wolcano.musicplayer.music.mvp.presenter;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import com.wolcano.musicplayer.music.mvp.interactor.SongInteractorImpl;
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.SongInteractor;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.SongPresenter;
import com.wolcano.musicplayer.music.ui.fragment.FragmentRecently;
import com.wolcano.musicplayer.music.ui.fragment.library.FragmentSongs;
import com.wolcano.musicplayer.music.ui.fragment.library.detail.AlbumDetailFragment;
import com.wolcano.musicplayer.music.ui.fragment.library.detail.ArtistDetailFragment;
import com.wolcano.musicplayer.music.ui.fragment.library.detail.GenreDetailFragment;
import com.wolcano.musicplayer.music.ui.fragment.library.detail.PlaylistDetailFragment;
import java.util.List;
import io.reactivex.disposables.Disposable;


public class SongPresenterImpl implements SongPresenter, SongInteractor.OnGetSongListener {

    private Fragment fragment;
    private SongInteractor songInteractor;
    private String sort;
    private Disposable disposable;
    private Activity activity;
    private long id;

    public SongPresenterImpl(Fragment fragment,Activity activity, Disposable disposable, String sort, SongInteractorImpl songInteractor){

        this.fragment = fragment;
        this.songInteractor = songInteractor;
        this.sort = sort;
        this.disposable = disposable;
        this.activity = activity;

    }

    public SongPresenterImpl(Fragment fragment,Activity activity, Disposable disposable, String sort,long id, SongInteractorImpl songInteractor){

        this.fragment = fragment;
        this.songInteractor = songInteractor;
        this.sort = sort;
        this.disposable = disposable;
        this.activity = activity;
        this.id = id;

    }

    @Override
    public void getSongs() {
        songInteractor.getSongs(activity,disposable,sort,this);
    }

    @Override
    public void getPlaylistSongs() {
        songInteractor.getPlaylistSongs(activity,disposable,sort,id,this);
    }
    @Override
    public void getAlbumSongs() {
        songInteractor.getAlbumSongs(activity,disposable,sort,id,this);
    }

    @Override
    public void getArtistSongs() {
        songInteractor.getArtistSongs(activity,disposable,sort,id,this);

    }

    @Override
    public void getGenreSongs() {

        songInteractor.getGenreSongs(activity,disposable,sort,id,this);

    }

    @Override
    public void sendSongList(List<Song> songList) {
        if(fragment instanceof FragmentRecently){
            FragmentRecently fragmentRecently =  (FragmentRecently) fragment;
            fragmentRecently.setSongList(songList);
        } else if(fragment instanceof FragmentSongs){
            FragmentSongs fragmentSongs = (FragmentSongs) fragment;
            fragmentSongs.setSongList(songList);
        } else if(fragment instanceof PlaylistDetailFragment){
            PlaylistDetailFragment playlistDetailFragment = (PlaylistDetailFragment) fragment;
            playlistDetailFragment.setSongList(songList);
        } else if(fragment instanceof AlbumDetailFragment){
            AlbumDetailFragment albumDetailFragment = (AlbumDetailFragment) fragment;
            albumDetailFragment.setSongList(songList);
        } else if(fragment instanceof ArtistDetailFragment){
            ArtistDetailFragment artistDetailFragment = (ArtistDetailFragment) fragment;
            artistDetailFragment.setSongList(songList);
        } else if(fragment instanceof GenreDetailFragment) {
            GenreDetailFragment genreDetailFragment = (GenreDetailFragment) fragment;
            genreDetailFragment.setSongList(songList);
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
        } else if(fragment instanceof PlaylistDetailFragment){
            PlaylistDetailFragment playlistDetailFragment = (PlaylistDetailFragment) fragment;
            playlistDetailFragment.controlIfEmpty();
        } else if(fragment instanceof ArtistDetailFragment){
            ArtistDetailFragment artistDetailFragment = (ArtistDetailFragment) fragment;
            artistDetailFragment.controlIfEmpty();
        } else if(fragment instanceof GenreDetailFragment){
            GenreDetailFragment genreDetailFragment = (GenreDetailFragment) fragment;
            genreDetailFragment.controlIfEmpty();
        }
    }
}
