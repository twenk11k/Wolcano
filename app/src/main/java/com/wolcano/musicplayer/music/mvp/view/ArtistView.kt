package com.wolcano.musicplayer.music.mvp.view

import com.wolcano.musicplayer.music.mvp.models.Artist

interface ArtistView {
    fun setArtistList(artistList: MutableList<Artist>?)
}