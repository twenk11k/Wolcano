package com.twenk11k.materialsearchview.utils

import android.view.View

interface AnimationListener {

    fun onAnimationStart(view: View): Boolean

    fun onAnimationEnd(view: View): Boolean

    fun onAnimationCancel(view: View): Boolean
}