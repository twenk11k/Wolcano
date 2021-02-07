package com.wolcano.musicplayer.music.mvp.view

import com.wolcano.musicplayer.music.mvp.models.Album

interface AlbumView {
    fun setAlbumList(albumList: MutableList<Album>?)
}