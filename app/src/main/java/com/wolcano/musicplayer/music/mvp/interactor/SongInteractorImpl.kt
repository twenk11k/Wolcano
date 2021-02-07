package com.wolcano.musicplayer.music.mvp.interactor

import android.Manifest
import android.app.Activity
import android.util.Log
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.constants.Constants.ERROR_TAG
import com.wolcano.musicplayer.music.constants.Constants.SONG_LIBRARY
import com.wolcano.musicplayer.music.mvp.DisposableManager.add
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.SongInteractor
import com.wolcano.musicplayer.music.mvp.interactor.interfaces.SongInteractor.OnGetSongListener
import com.wolcano.musicplayer.music.mvp.models.Song
import com.wolcano.musicplayer.music.utils.PermissionUtils
import com.wolcano.musicplayer.music.utils.PermissionUtils.PermInterface
import com.wolcano.musicplayer.music.utils.SongUtils.scanSongs
import com.wolcano.musicplayer.music.utils.SongUtils.scanSongsforAlbum
import com.wolcano.musicplayer.music.utils.SongUtils.scanSongsforArtist
import com.wolcano.musicplayer.music.utils.SongUtils.scanSongsforGenre
import com.wolcano.musicplayer.music.utils.SongUtils.scanSongsforPlaylist
import com.wolcano.musicplayer.music.utils.ToastUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class SongInteractorImpl : SongInteractor {

    @Subscribe(tags = [Tag(SONG_LIBRARY)])
    override fun getSongs(
        activity: Activity?,
        sort: String?,
        onGetSongListener: OnGetSongListener?
    ) {
        PermissionUtils.with(activity!!)
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .result(object : PermInterface {
                override fun onPermGranted() {
                    val observable =
                        Observable.fromCallable { scanSongs(activity, sort) }
                            .throttleFirst(500, TimeUnit.MILLISECONDS)
                    val disposable = observable.subscribeOn(Schedulers.io()).observeOn(
                        AndroidSchedulers.mainThread()
                    ).subscribe { songsList: ArrayList<Song>? ->
                        onGetSongListener?.sendSongList(songsList)
                    }
                    add(disposable)
                }

                override fun onPermUnapproved() {
                    onGetSongListener?.controlIfEmpty()
                    ToastUtils.show(activity.applicationContext, R.string.no_perm_storage)
                }
            })
            .reqPerm()
    }

    @Subscribe(tags = [Tag(SONG_LIBRARY)])
    override fun getPlaylistSongs(
        activity: Activity?,
        sort: String?,
        playlistID: Long,
        onGetSongListener: OnGetSongListener?
    ) {
        PermissionUtils.with(activity!!)
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .result(object : PermInterface {
                override fun onPermGranted() {
                    val observable = Observable.fromCallable {
                        scanSongsforPlaylist(
                            activity,
                            sort,
                            playlistID
                        )
                    }
                        .throttleFirst(500, TimeUnit.MILLISECONDS)
                        .doOnError { throwable: Throwable ->
                            Log.e(
                                ERROR_TAG,
                                "Message: " + throwable.message
                            )
                        }
                    val disposable = observable.subscribeOn(Schedulers.io()).observeOn(
                        AndroidSchedulers.mainThread()
                    ).subscribe { songsList: ArrayList<Song>? ->
                        onGetSongListener?.sendSongList(songsList)
                    }
                    add(disposable)
                }

                override fun onPermUnapproved() {
                    onGetSongListener!!.controlIfEmpty()
                    ToastUtils.show(activity.applicationContext, R.string.no_perm_storage)
                }
            })
            .reqPerm()
    }

    @Subscribe(tags = [Tag(SONG_LIBRARY)])
    override fun getAlbumSongs(
        activity: Activity?,
        sort: String?,
        albumID: Long,
        onGetSongListener: OnGetSongListener?
    ) {
        PermissionUtils.with(activity!!)
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .result(object : PermInterface {
                override fun onPermGranted() {
                    val observable = Observable.fromCallable {
                        scanSongsforAlbum(
                            activity,
                            sort,
                            albumID
                        )
                    }
                        .throttleFirst(500, TimeUnit.MILLISECONDS)
                    val disposable = observable.subscribeOn(Schedulers.io()).observeOn(
                        AndroidSchedulers.mainThread()
                    ).subscribe { songList: ArrayList<Song>? ->
                        onGetSongListener?.sendSongList(songList)
                    }
                    add(disposable)
                }

                override fun onPermUnapproved() {
                    onGetSongListener?.controlIfEmpty()
                    ToastUtils.show(activity.applicationContext, R.string.no_perm_storage)
                }
            })
            .reqPerm()
    }

    @Subscribe(tags = [Tag(SONG_LIBRARY)])
    override fun getArtistSongs(
        activity: Activity?,
        sort: String?,
        artistID: Long,
        onGetSongListener: OnGetSongListener?
    ) {
        PermissionUtils.with(activity!!)
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .result(object : PermInterface {
                override fun onPermGranted() {
                    val observable = Observable.fromCallable<ArrayList<Song>> {
                        scanSongsforArtist(
                            activity,
                            sort,
                            artistID
                        )
                    }
                        .throttleFirst(500, TimeUnit.MILLISECONDS)
                    val disposable = observable.subscribeOn(Schedulers.io()).observeOn(
                        AndroidSchedulers.mainThread()
                    ).subscribe { songList: ArrayList<Song>? ->
                        onGetSongListener?.sendSongList(songList)
                    }
                    add(disposable)
                }

                override fun onPermUnapproved() {
                    onGetSongListener?.controlIfEmpty()
                    ToastUtils.show(activity.applicationContext, R.string.no_perm_storage)
                }
            })
            .reqPerm()
    }

    @Subscribe(tags = [Tag(SONG_LIBRARY)])
    override fun getGenreSongs(
        activity: Activity?,
        sort: String?,
        genreID: Long,
        onGetSongListener: OnGetSongListener?
    ) {
        PermissionUtils.with(activity!!)
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .result(object : PermInterface {
                override fun onPermGranted() {
                    val observable = Observable.fromCallable {
                        scanSongsforGenre(
                            activity,
                            sort,
                            genreID
                        )
                    }
                        .throttleFirst(500, TimeUnit.MILLISECONDS)
                    val disposable = observable.subscribeOn(Schedulers.io()).observeOn(
                        AndroidSchedulers.mainThread()
                    ).subscribe { songList: ArrayList<Song>? ->
                        onGetSongListener?.sendSongList(songList)
                    }
                    add(disposable)
                }

                override fun onPermUnapproved() {
                    onGetSongListener?.controlIfEmpty()
                    ToastUtils.show(activity.applicationContext, R.string.no_perm_storage)
                }
            })
            .reqPerm()
    }

}