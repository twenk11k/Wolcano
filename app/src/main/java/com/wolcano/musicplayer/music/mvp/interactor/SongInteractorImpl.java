package com.wolcano.musicplayer.music.mvp.interactor;

import android.Manifest;
import android.app.Activity;
import android.util.Log;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.DisposableManager;
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.SongInteractor;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.utils.PermissionUtils;
import com.wolcano.musicplayer.music.utils.SongUtils;
import com.wolcano.musicplayer.music.utils.ToastUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.wolcano.musicplayer.music.constants.Constants.ERROR_TAG;
import static com.wolcano.musicplayer.music.constants.Constants.SONG_LIBRARY;

public class SongInteractorImpl implements SongInteractor {


    @Subscribe(tags = {@Tag(SONG_LIBRARY)})
    @Override
    public void getSongs(Activity activity, String sort, OnGetSongListener onGetSongListener) {

        PermissionUtils.with(activity)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .result(new PermissionUtils.PermInterface() {
                    @Override
                    public void onPermGranted() {

                        Observable<List<Song>> observable =
                                Observable.fromCallable(() -> SongUtils.scanSongs(activity, sort)).throttleFirst(500, TimeUnit.MILLISECONDS);
                        Disposable disposable = observable.
                                subscribeOn(Schedulers.io()).
                                observeOn(AndroidSchedulers.mainThread()).
                                subscribe(songsList -> onGetSongListener.sendSongList(songsList));

                        DisposableManager.add(disposable);

                    }

                    @Override
                    public void onPermUnapproved() {

                        onGetSongListener.controlIfEmpty();
                        ToastUtils.show(activity.getApplicationContext(), R.string.no_perm_storage);

                    }

                })
                .reqPerm();

    }

    @Subscribe(tags = {@Tag(SONG_LIBRARY)})
    @Override
    public void getPlaylistSongs(Activity activity, String sort, long playlistID, OnGetSongListener onGetSongListener) {


        PermissionUtils.with(activity)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .result(new PermissionUtils.PermInterface() {
                    @Override
                    public void onPermGranted() {

                        Observable<List<Song>> observable =
                                Observable.fromCallable(() -> SongUtils.scanSongsforPlaylist(activity, sort, playlistID))
                                        .throttleFirst(500, TimeUnit.MILLISECONDS)
                                        .doOnError(throwable -> {
                                            Log.e(ERROR_TAG,"Message: "+throwable.getMessage());
                                        });

                        Disposable disposable = observable.
                                subscribeOn(Schedulers.io()).
                                observeOn(AndroidSchedulers.mainThread()).
                                subscribe(songsList -> onGetSongListener.sendSongList(songsList));

                        DisposableManager.add(disposable);
                    }

                    @Override
                    public void onPermUnapproved() {

                        onGetSongListener.controlIfEmpty();
                        ToastUtils.show(activity.getApplicationContext(), R.string.no_perm_storage);

                    }

                })
                .reqPerm();

    }

    @Subscribe(tags = {@Tag(SONG_LIBRARY)})
    @Override
    public void getAlbumSongs(Activity activity, String sort, long albumID, OnGetSongListener onGetSongListener) {
        PermissionUtils.with(activity)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .result(new PermissionUtils.PermInterface() {
                    @Override
                    public void onPermGranted() {
                        Observable<List<Song>> observable =
                                Observable.fromCallable(() -> SongUtils.scanSongsforAlbum(activity, sort, albumID)).throttleFirst(500, TimeUnit.MILLISECONDS);

                        Disposable disposable = observable.
                                subscribeOn(Schedulers.io()).
                                observeOn(AndroidSchedulers.mainThread()).
                                subscribe(songList -> onGetSongListener.sendSongList(songList));

                        DisposableManager.add(disposable);
                    }

                    @Override
                    public void onPermUnapproved() {
                        onGetSongListener.controlIfEmpty();
                        ToastUtils.show(activity.getApplicationContext(), R.string.no_perm_storage);
                    }
                })
                .reqPerm();
    }

    @Subscribe(tags = {@Tag(SONG_LIBRARY)})
    @Override
    public void getArtistSongs(Activity activity, String sort, long artistID, OnGetSongListener onGetSongListener) {


        PermissionUtils.with(activity)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .result(new PermissionUtils.PermInterface() {
                    @Override
                    public void onPermGranted() {
                        Observable<List<Song>> observable =
                                Observable.fromCallable(() -> SongUtils.scanSongsforArtist(activity, sort, artistID)).throttleFirst(500, TimeUnit.MILLISECONDS);

                        Disposable disposable = observable.
                                subscribeOn(Schedulers.io()).
                                observeOn(AndroidSchedulers.mainThread()).
                                subscribe(songList -> onGetSongListener.sendSongList(songList));

                        DisposableManager.add(disposable);

                    }

                    @Override
                    public void onPermUnapproved() {
                        onGetSongListener.controlIfEmpty();
                        ToastUtils.show(activity.getApplicationContext(), R.string.no_perm_storage);
                    }
                })
                .reqPerm();
    }

    @Subscribe(tags = {@Tag(SONG_LIBRARY)})
    @Override
    public void getGenreSongs(Activity activity, String sort, long genreID, OnGetSongListener onGetSongListener) {


        PermissionUtils.with(activity)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .result(new PermissionUtils.PermInterface() {
                    @Override
                    public void onPermGranted() {
                        Observable<List<Song>> observable =
                                Observable.fromCallable(() -> SongUtils.scanSongsforGenre(activity, sort, genreID)).throttleFirst(500, TimeUnit.MILLISECONDS);

                        Disposable disposable = observable.
                                subscribeOn(Schedulers.io()).
                                observeOn(AndroidSchedulers.mainThread()).
                                subscribe(songList -> onGetSongListener.sendSongList(songList));

                        DisposableManager.add(disposable);
                    }

                    @Override
                    public void onPermUnapproved() {
                        onGetSongListener.controlIfEmpty();
                        ToastUtils.show(activity.getApplicationContext(), R.string.no_perm_storage);
                    }
                })
                .reqPerm();
    }

}
