package com.wolcano.musicplayer.music.mvp.models;

public class Playlist {

    public long id;
    public String name;
    public int songCount;


    public Playlist(long _id, String _name, int _songCount) {
        this.id = _id;
        this.name = _name;
        this.songCount = _songCount;
    }
}
