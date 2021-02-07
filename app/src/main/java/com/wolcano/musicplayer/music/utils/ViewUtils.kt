package com.wolcano.musicplayer.music.utils

import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener


object ViewUtils {

    fun removeOnGlobalLayoutListener(v: View, listener: OnGlobalLayoutListener?) {
        v.viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }

}