package com.wolcano.musicplayer.music.mvp.models;

public class Copy {

    private String text;
    private int icon;

    public Copy(String text, int icon) {
        this.text = text;
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

    public String getText() {
        return text;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setText(String text) {
        this.text = text;
    }

}
