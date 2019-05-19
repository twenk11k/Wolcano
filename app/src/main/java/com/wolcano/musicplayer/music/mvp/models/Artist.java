package com.wolcano.musicplayer.music.mvp.models;

public class Artist {

    public long id;
    public String name;
    public int songCount;

    public Artist(long id,String name,int songCount){
        this.id = id;
        this.name = name;
        this.songCount = songCount;
    }

}
