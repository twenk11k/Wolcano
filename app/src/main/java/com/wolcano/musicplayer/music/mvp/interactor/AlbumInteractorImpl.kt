package com.wolcano.musicplayer.music.mvp.interactor

import android.Manifest
import android.app.Activity
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.constants.Constants.SONG_LIBRARY
import com.wolcano.musicplayer.music.mvp.DisposableManager.add
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.AlbumInteractor
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.AlbumInteractor.OnGetAlbumListener
import com.wolcano.musicplayer.music.mvp.models.Album
import com.wolcano.musicplayer.music.utils.PermissionUtils
import com.wolcano.musicplayer.music.utils.PermissionUtils.PermInterface
import com.wolcano.musicplayer.music.utils.SongUtils.scanAlbums
import com.wolcano.musicplayer.music.utils.ToastUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class AlbumInteractorImpl : AlbumInteractor {

    @Subscribe(tags = [Tag(SONG_LIBRARY)])
    override fun getAlbum(
        activity: Activity?,
        sort: String?,
        onGetAlbumListener: OnGetAlbumListener?
    ) {
        PermissionUtils.with(activity!!)
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .result(object : PermInterface {
                override fun onPermGranted() {
                    val observable = Observable.fromCallable { scanAlbums(activity) }
                        .throttleFirst(500, TimeUnit.MILLISECONDS)
                    val disposable = observable.subscribeOn(Schedulers.io()).observeOn(
                        AndroidSchedulers.mainThread()
                    ).subscribe { albumList: ArrayList<Album>? ->
                        onGetAlbumListener?.sendAlbum(albumList)
                    }
                    add(disposable)
                }

                override fun onPermUnapproved() {
                    onGetAlbumListener!!.controlIfEmpty()
                    ToastUtils.show(activity.applicationContext, R.string.no_perm_storage)
                }
            })
            .reqPerm()
    }

}