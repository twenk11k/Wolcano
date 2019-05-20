package com.wolcano.musicplayer.music.mvp.models;

public class SongOnline {


    private String title;
    private String path;
    private String artistName;
    private long duration;

    public SongOnline(String path, String artistName, String title, long duration){

        this.title = title;
        this.artistName = artistName;
        this.path = path;
        this.duration = duration;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtistName() {
        return artistName;
    }


    public long getDuration() {
        return duration;
    }

    public String getPath() {
        return path;
    }


}
