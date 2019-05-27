package com.wolcano.musicplayer.music.mvp.models;

public class Album {

    private long id;
    private String name;
    private String artist;

    public Album(long id, String name, String artist) {

        this.id = id;
        this.name = name;
        this.artist = artist;

    }

    public long getId() {
        return id;
    }

    public String getArtist() {
        return artist;
    }

    public String getName() {
        return name;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

}
