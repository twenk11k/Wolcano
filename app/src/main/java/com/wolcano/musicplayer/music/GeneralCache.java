package com.wolcano.musicplayer.music;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.utils.SongCover;
import java.util.ArrayList;
import java.util.List;


public class GeneralCache {

    private final List<Song> arrayList = new ArrayList<>();
    private final List<Activity> activityList = new ArrayList<>();

    private static class SingletonHolder {
        private static GeneralCache singletonInstance = new GeneralCache();
    }

    public static GeneralCache get() {
        return SingletonHolder.singletonInstance;
    }

    public void initializeCache(Application application) {
        SongCover.get().init(application.getApplicationContext());
        application.registerActivityLifecycleCallbacks(new SchemeActivities());
    }
    private class SchemeActivities implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            activityList.add(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            activityList.remove(activity);
        }
    }

    public List<Song> getArrayList() {
        return arrayList;
    }



}
