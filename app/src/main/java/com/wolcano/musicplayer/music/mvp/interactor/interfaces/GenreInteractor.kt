package com.wolcano.musicplayer.music.mvp.interactor.interfaces

import android.app.Activity
import com.wolcano.musicplayer.music.mvp.models.Genre

interface GenreInteractor {

    interface OnGetGenreListener {
        fun sendGenres(genreList: ArrayList<Genre>?)
        fun controlIfEmpty()
    }

    fun getGenres(activity: Activity?, sort: String?, OnGetGenreListener: OnGetGenreListener?)

}