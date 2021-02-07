package com.wolcano.musicplayer.music.content

import android.app.Activity
import android.view.View
import com.wolcano.musicplayer.music.mvp.listener.Bind

object Binder {

    fun bindIt(activity: Activity) {
        bindIt(activity, activity.window.decorView)
    }

    fun bindIt(target: Any, source: View?) {
        val fieldArr = target.javaClass.declaredFields
        if (fieldArr.isNotEmpty()) {
            for (f in fieldArr) {
                try {
                    f.isAccessible = true
                    if (f[target] != null) {
                        continue
                    }
                    val bind = f.getAnnotation(Bind::class.java)
                    if (bind != null) {
                        val id: Int = bind.value
                        f[target] = source?.findViewById(id)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}