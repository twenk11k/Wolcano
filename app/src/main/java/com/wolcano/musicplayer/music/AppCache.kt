package com.wolcano.musicplayer.music

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import com.wolcano.musicplayer.music.data.model.Song
import com.wolcano.musicplayer.music.widgets.SongCover

object AppCache {

    val songList: List<Song> = ArrayList()
    private val activityList = ArrayList<Activity>()

    fun initializeCache(application: Application) {
        SongCover.init(application.applicationContext)
        application.registerActivityLifecycleCallbacks(SchemeActivities())
    }

    private class SchemeActivities : ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            activityList.add(activity)
        }

        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityResumed(activity: Activity) {}
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityDestroyed(activity: Activity) {
            activityList.remove(activity)
        }
    }

}