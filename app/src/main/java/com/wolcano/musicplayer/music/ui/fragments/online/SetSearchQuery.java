package com.wolcano.musicplayer.music.ui.fragments.online;


public interface SetSearchQuery {

    void onSearchQuery(int position, boolean isFirst);
    void onRemoveSuggestion(int position, int whichList);

}
