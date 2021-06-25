package com.twenk11k.materialsearchview

interface SetSearchQuery {
    fun onSearchQuery(position: Int, isFirst: Boolean)
    fun onRemoveSuggestion(position: Int, whichList: Int)
}