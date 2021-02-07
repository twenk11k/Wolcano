package com.wolcano.musicplayer.music.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.preference.PreferenceManager

class PrefUtils constructor(context: Context) {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun unregisterOnSharedPreferenceChangedListener(sharedPreferenceChangeListener: OnSharedPreferenceChangeListener?) {
        prefs.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }

    fun registerOnSharedPreferenceChangedListener(sharedPreferenceChangeListener: OnSharedPreferenceChangeListener?) {
        prefs.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }

}