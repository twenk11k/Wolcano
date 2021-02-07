package com.wolcano.musicplayer.music.mvp.models;

import android.graphics.Bitmap;

public class ModelBitmap {

    private int id;
    private Bitmap bitmap;

    public ModelBitmap() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getId() {
        return id;
    }

}
