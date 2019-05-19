package com.wolcano.musicplayer.music.mvp.listener;


public interface SetSearchQuery {

    void onSearchQuery(int position, boolean isFirst);
    void onRemoveSuggestion(int position, int whichList);

}
