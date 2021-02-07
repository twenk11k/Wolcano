package com.wolcano.musicplayer.music.mvp.interactor.interfaces

import android.app.Activity
import com.wolcano.musicplayer.music.mvp.models.Artist

interface ArtistInteractor {

    interface OnGetArtistListener {
        fun sendArtist(artistList: ArrayList<Artist>?)
        fun controlIfEmpty()
    }

    fun getArtist(activity: Activity?, sort: String?, onGetArtistListener: OnGetArtistListener?)

}