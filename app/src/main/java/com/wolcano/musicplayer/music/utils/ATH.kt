package com.wolcano.musicplayer.music.utils

import android.app.Activity
import android.app.ActivityManager.TaskDescription
import android.os.Build
import android.view.View
import androidx.annotation.ColorInt
import com.wolcano.musicplayer.music.utils.Utils.getPrimaryColor

object ATH {
    fun setLightStatusbarAuto(activity: Activity, bgColor: Int) {
        setLightStatusbar(activity, ColorUtils.isColorLight(bgColor))
    }

    fun setLightStatusbar(activity: Activity, enabled: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decorView = activity.window.decorView
            val systemUiVisibility = decorView.systemUiVisibility
            if (enabled) {
                decorView.systemUiVisibility =
                    systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                decorView.systemUiVisibility =
                    systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
    }

    fun setTaskDescriptionColorAuto(activity: Activity) {
        setTaskDescriptionColor(activity, getPrimaryColor(activity))
    }

    fun setTaskDescriptionColor(activity: Activity, @ColorInt color: Int) {
        var color = color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Task description requires fully opaque color
            color = ColorUtils.stripAlpha(color)
            // Sets color of entry in the system recents page
            activity.setTaskDescription(TaskDescription(activity.title as String, null, color))
        }
    }

}