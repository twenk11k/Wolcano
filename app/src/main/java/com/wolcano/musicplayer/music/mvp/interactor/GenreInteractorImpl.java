package com.wolcano.musicplayer.music.mvp.interactor;

import android.Manifest;
import android.app.Activity;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.GenreInteractor;
import com.wolcano.musicplayer.music.mvp.models.Genre;
import com.wolcano.musicplayer.music.utils.PermissionUtils;
import com.wolcano.musicplayer.music.utils.SongUtils;
import com.wolcano.musicplayer.music.utils.ToastUtils;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.wolcano.musicplayer.music.Constants.SONG_LIBRARY;

public class GenreInteractorImpl implements GenreInteractor {

    private Disposable disposable1;

    @Subscribe(tags = {@Tag(SONG_LIBRARY)})
    @Override
    public void getGenres(Activity activity, Disposable disposable, String sort, OnGetGenreListener onGetGenreListener) {

        disposable1 = disposable;

        PermissionUtils.with(activity)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .result(new PermissionUtils.PermInterface() {
                    @Override
                    public void onPermGranted() {
                        Observable<List<Genre>> observable =
                                Observable.fromCallable(() -> SongUtils.scanGenre(activity)).throttleFirst(500, TimeUnit.MILLISECONDS);
                        disposable1 = observable.
                                subscribeOn(Schedulers.io()).
                                observeOn(AndroidSchedulers.mainThread()).
                                subscribe(genreList -> onGetGenreListener.sendGenres(genreList));
                    }
                    @Override
                    public void onPermUnapproved() {
                        onGetGenreListener.controlIfEmpty();
                        ToastUtils.show(activity.getApplicationContext(), R.string.no_perm_storage);
                    }
                })
                .reqPerm();


    }


}
