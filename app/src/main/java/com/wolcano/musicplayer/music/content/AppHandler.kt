package com.wolcano.musicplayer.music.content

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import java.util.*

object AppHandler : ActivityLifecycleCallbacks {

    private var observerList: List<Observer>? = null
    private var activityCount = 0
    private var handler: Handler? = null
    private val handlerDelay: Long = 500
    private var isChecked = false

    init {
        observerList = Collections.synchronizedList(ArrayList())
        handler = Handler(Looper.getMainLooper())
    }

    interface Observer {
        fun onForeground(activity: Activity?)
        fun onBackground(activity: Activity?)
    }

    fun setCallbacks(application: Application) {
        application.registerActivityLifecycleCallbacks(AppHandler)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivityDestroyed(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {
        activityCount++
        if (!isChecked && activityCount > 0) {
            isChecked = true
            notify(activity, true)
        }
    }

    private fun notify(activity: Activity?, foreground: Boolean) {
        for (observer in observerList!!) {
            if (foreground) {
                observer.onForeground(activity)
            } else {
                observer.onBackground(activity)
            }
        }
    }

    override fun onActivityPaused(activity: Activity) {
        activityCount--
        handler?.postDelayed({
            if (isChecked && activityCount == 0) {
                isChecked = false
                notify(activity, false)
            }
        }, handlerDelay)
    }

}