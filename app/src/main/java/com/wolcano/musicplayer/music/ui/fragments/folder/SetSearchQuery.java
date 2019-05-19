package com.wolcano.musicplayer.music.ui.fragments.folder;


public interface SetSearchQuery {

    void onSearchQuery(int position, boolean isFirst);
    void onRemoveSuggestion(int position, int whichList);

}
