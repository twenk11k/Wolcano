package com.wolcano.musicplayer.music.mvp.interactor;

import android.Manifest;
import android.app.Activity;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.DisposableManager;
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.ArtistInteractor;
import com.wolcano.musicplayer.music.mvp.models.Artist;
import com.wolcano.musicplayer.music.utils.PermissionUtils;
import com.wolcano.musicplayer.music.utils.SongUtils;
import com.wolcano.musicplayer.music.utils.ToastUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.wolcano.musicplayer.music.constants.Constants.SONG_LIBRARY;

public class ArtistInteractorImpl implements ArtistInteractor {


    @Subscribe(tags = {@Tag(SONG_LIBRARY)})
    @Override
    public void getArtist(Activity activity, String sort, OnGetArtistListener onGetArtistListener) {

        PermissionUtils.with(activity)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .result(new PermissionUtils.PermInterface() {
                    @Override
                    public void onPermGranted() {
                        Observable<List<Artist>> observable =
                                Observable.fromCallable(() -> SongUtils.scanArtists(activity)).throttleFirst(500, TimeUnit.MILLISECONDS);

                        Disposable disposable = observable.
                                subscribeOn(Schedulers.io()).
                                observeOn(AndroidSchedulers.mainThread()).
                                subscribe(artistList -> onGetArtistListener.sendArtist(artistList));

                        DisposableManager.add(disposable);
                    }

                    @Override
                    public void onPermUnapproved() {
                        onGetArtistListener.controlIfEmpty();
                        ToastUtils.show(activity.getApplicationContext(), R.string.no_perm_storage);
                    }
                })
                .reqPerm();


    }
}
