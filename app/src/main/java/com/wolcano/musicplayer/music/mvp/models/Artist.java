package com.wolcano.musicplayer.music.mvp.models;

public class Artist {

    private long id;
    private String name;
    private int songCount;

    public Artist(long id,String name,int songCount){
        this.id = id;
        this.name = name;
        this.songCount = songCount;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public int getSongCount() {
        return songCount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setSongCount(int songCount) {
        this.songCount = songCount;
    }

}
