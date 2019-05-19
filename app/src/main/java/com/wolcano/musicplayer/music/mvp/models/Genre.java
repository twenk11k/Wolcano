package com.wolcano.musicplayer.music.mvp.models;

public class Genre {

    public final long id;
    public final String name;
    public final int songCount;

    public Genre(final long id, final String name, final int songCount) {
        this.id = id;
        this.name = name;
        this.songCount = songCount;
    }

}
