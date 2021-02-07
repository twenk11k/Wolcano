package com.wolcano.musicplayer.music.mvp.interactor.interfaces

import android.app.Activity
import com.wolcano.musicplayer.music.mvp.models.Album

interface AlbumInteractor {

    interface OnGetAlbumListener {
        fun sendAlbum(albumList: ArrayList<Album>?)
        fun controlIfEmpty()
    }

    fun getAlbum(activity: Activity?, sort: String?, onGetAlbumListener: OnGetAlbumListener?)

}