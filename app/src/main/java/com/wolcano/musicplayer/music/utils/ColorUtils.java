package com.wolcano.musicplayer.music.utils;

import android.graphics.Color;

import com.kabouzeid.appthemehelper.util.ColorUtil;

public class ColorUtils {

    public static int getOppositeColor(int color){
        if(ColorUtil.isColorLight(color)) {
            return Color.BLACK;
        } else {
            return Color.WHITE;
        }
    }
}
