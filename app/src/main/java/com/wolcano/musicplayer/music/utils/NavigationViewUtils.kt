package com.wolcano.musicplayer.music.utils

import android.content.res.ColorStateList
import androidx.annotation.ColorInt
import com.google.android.material.bottomnavigation.BottomNavigationView

object NavigationViewUtils {

    fun setItemIconColors(
        navigationView: BottomNavigationView,
        @ColorInt normalColor: Int,
        @ColorInt selectedColor: Int
    ) {
        val iconSl = ColorStateList(
            arrayOf(intArrayOf(-16842912), intArrayOf(16842912)),
            intArrayOf(normalColor, selectedColor)
        )
        navigationView.itemIconTintList = iconSl
    }

    fun setItemTextColors(
        navigationView: BottomNavigationView,
        @ColorInt normalColor: Int,
        @ColorInt selectedColor: Int
    ) {
        val textSl = ColorStateList(
            arrayOf(intArrayOf(-16842912), intArrayOf(16842912)),
            intArrayOf(normalColor, selectedColor)
        )
        navigationView.itemTextColor = textSl
    }

}