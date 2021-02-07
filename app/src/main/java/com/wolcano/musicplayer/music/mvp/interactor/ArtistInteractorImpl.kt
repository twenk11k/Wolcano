package com.wolcano.musicplayer.music.mvp.interactor

import android.Manifest
import android.app.Activity
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.constants.Constants.SONG_LIBRARY
import com.wolcano.musicplayer.music.mvp.DisposableManager.add
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.ArtistInteractor
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.ArtistInteractor.OnGetArtistListener
import com.wolcano.musicplayer.music.mvp.models.Artist
import com.wolcano.musicplayer.music.utils.PermissionUtils
import com.wolcano.musicplayer.music.utils.PermissionUtils.PermInterface
import com.wolcano.musicplayer.music.utils.SongUtils.scanArtists
import com.wolcano.musicplayer.music.utils.ToastUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class ArtistInteractorImpl : ArtistInteractor {

    @Subscribe(tags = [Tag(SONG_LIBRARY)])
    override fun getArtist(
        activity: Activity?,
        sort: String?,
        onGetArtistListener: OnGetArtistListener?
    ) {
        PermissionUtils.with(activity!!)
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .result(object : PermInterface {
                override fun onPermGranted() {
                    val observable =
                        Observable.fromCallable { scanArtists(activity) }
                            .throttleFirst(500, TimeUnit.MILLISECONDS)
                    val disposable = observable.subscribeOn(Schedulers.io()).observeOn(
                        AndroidSchedulers.mainThread()
                    ).subscribe { artistList: ArrayList<Artist>? ->
                        onGetArtistListener?.sendArtist(artistList)
                    }
                    add(disposable)
                }

                override fun onPermUnapproved() {
                    onGetArtistListener!!.controlIfEmpty()
                    ToastUtils.show(activity.applicationContext, R.string.no_perm_storage)
                }
            })
            .reqPerm()
    }

}