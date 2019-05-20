package com.wolcano.musicplayer.music.mvp.interactor;

import android.Manifest;
import android.app.Activity;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.SongInteractor;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.utils.Perms;
import com.wolcano.musicplayer.music.utils.SongUtils;
import com.wolcano.musicplayer.music.utils.ToastUtils;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.wolcano.musicplayer.music.Constants.SONG_LIBRARY;

public class SongInteractorImpl implements SongInteractor {

    private Disposable disposable1;


    @Subscribe(tags = {@Tag(SONG_LIBRARY)})
    @Override
    public void getSongs(Activity activity,Disposable disposable,String sort,OnGetSongListener onGetSongListener) {

        disposable1 = disposable;

        Perms.with(activity)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .result(new Perms.PermInterface() {
                    @Override
                    public void onPermGranted() {

                        Observable<List<Song>> observable =
                                Observable.fromCallable(() -> SongUtils.scanSongs(activity, sort)).throttleFirst(500, TimeUnit.MILLISECONDS);

                        disposable1 = observable.
                                subscribeOn(Schedulers.io()).
                                observeOn(AndroidSchedulers.mainThread()).
                                subscribe(songsList -> onGetSongListener.sendSongs(songsList));

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
