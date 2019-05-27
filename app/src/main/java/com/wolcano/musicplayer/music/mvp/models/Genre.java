package com.wolcano.musicplayer.music.mvp.models;

public class Genre {

    private long id;
    private String name;
    private int songCount;

    public Genre(long id,String name,int songCount) {
        this.id = id;
        this.name = name;
        this.songCount = songCount;
    }

    public int getSongCount() {
        return songCount;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
