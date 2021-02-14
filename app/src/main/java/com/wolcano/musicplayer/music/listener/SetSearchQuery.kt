package com.wolcano.musicplayer.music.listener

interface SetSearchQuery {
    fun onSearchQuery(position: Int, isFirst: Boolean)
    fun onRemoveSuggestion(position: Int, whichList: Int)
}