package com.wolcano.musicplayer.music.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;

public class PrefUtils {

    private static PrefUtils instance;

    private final SharedPreferences prefs;

    public static PrefUtils getInstance(@NonNull final Context context) {
        if (instance == null) {
            instance = new PrefUtils(context);
        }
        return instance;
    }


    private PrefUtils(@NonNull final Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void unregisterOnSharedPreferenceChangedListener(SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener) {
        prefs.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }

    public void registerOnSharedPreferenceChangedListener(SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener) {
        prefs.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    }



}
