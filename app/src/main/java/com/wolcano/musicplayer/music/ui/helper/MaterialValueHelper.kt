package com.wolcano.musicplayer.music.ui.helper

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.wolcano.musicplayer.music.R


object MaterialValueHelper {

    @SuppressLint("PrivateResource")
    @ColorInt
    fun getPrimaryTextColor(context: Context, dark: Boolean): Int {
        return if (dark) {
            ContextCompat.getColor(context, R.color.primary_text_default_material_light)
        } else ContextCompat.getColor(context, R.color.primary_text_default_material_dark)
    }

    @SuppressLint("PrivateResource")
    @ColorInt
    fun getSecondaryTextColor(context: Context, dark: Boolean): Int {
        return if (dark) {
            ContextCompat.getColor(context, R.color.secondary_text_default_material_light)
        } else ContextCompat.getColor(context, R.color.secondary_text_default_material_dark)
    }

    @SuppressLint("PrivateResource")
    @ColorInt
    fun getPrimaryDisabledTextColor(context: Context, dark: Boolean): Int {
        return if (dark) {
            ContextCompat.getColor(context, R.color.primary_text_disabled_material_light)
        } else ContextCompat.getColor(context, R.color.primary_text_disabled_material_dark)
    }

    @SuppressLint("PrivateResource")
    @ColorInt
    fun getSecondaryDisabledTextColor(context: Context, dark: Boolean): Int {
        return if (dark) {
            ContextCompat.getColor(context, R.color.secondary_text_disabled_material_light)
        } else ContextCompat.getColor(context, R.color.secondary_text_disabled_material_dark)
    }

}