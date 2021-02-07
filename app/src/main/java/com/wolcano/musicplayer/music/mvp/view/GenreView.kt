package com.wolcano.musicplayer.music.mvp.view

import com.wolcano.musicplayer.music.mvp.models.Genre

interface GenreView {
    fun setGenreList(genreList: ArrayList<Genre>?)
}