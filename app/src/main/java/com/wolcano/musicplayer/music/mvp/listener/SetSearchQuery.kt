package com.wolcano.musicplayer.music.mvp.listener

interface SetSearchQuery {
    fun onSearchQuery(position: Int, isFirst: Boolean)
    fun onRemoveSuggestion(position: Int, whichList: Int)
}