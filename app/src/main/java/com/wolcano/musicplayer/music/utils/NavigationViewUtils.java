package com.wolcano.musicplayer.music.utils;

import android.content.res.ColorStateList;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationViewUtils {
    public static void setItemIconColors(@NonNull BottomNavigationView navigationView, @ColorInt int normalColor, @ColorInt int selectedColor) {
        ColorStateList iconSl = new ColorStateList(new int[][]{{-16842912}, {16842912}}, new int[]{normalColor, selectedColor});
        navigationView.setItemIconTintList(iconSl);
    }

    public static void setItemTextColors(@NonNull BottomNavigationView navigationView, @ColorInt int normalColor, @ColorInt int selectedColor) {
        ColorStateList textSl = new ColorStateList(new int[][]{{-16842912}, {16842912}}, new int[]{normalColor, selectedColor});
        navigationView.setItemTextColor(textSl);
    }

    private NavigationViewUtils() {
    }
}
