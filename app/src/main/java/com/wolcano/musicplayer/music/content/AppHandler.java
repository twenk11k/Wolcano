package com.wolcano.musicplayer.music.content;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class AppHandler implements Application.ActivityLifecycleCallbacks {

    private List<Observer> observerList;
    private int activityCount;
    private Handler handler;
    private static final long HANDLER_DELAY = 500;
    private boolean isChecked;

    public interface Observer {
        void onForeground(Activity activity);

        void onBackground(Activity activity);
    }

    public static void setCallbacks(Application application) {
        application.registerActivityLifecycleCallbacks(getInstance());
    }

    private static AppHandler getInstance() {
        return SingletonHolder.singletonInstance;
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }
    private static class SingletonHolder {
        private static AppHandler singletonInstance = new AppHandler();
    }
    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }
    private AppHandler() {
        observerList = Collections.synchronizedList(new ArrayList<Observer>());
        handler = new Handler(Looper.getMainLooper());
    }
    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }


    @Override
    public void onActivityDestroyed(Activity activity) {
    }
    @Override
    public void onActivityResumed(Activity activity) {
        activityCount++;
        if (!isChecked && activityCount > 0) {
            isChecked = true;
            notify(activity, true);
        }
    }

    private void notify(Activity activity, boolean foreground) {
        for (Observer observer : observerList) {
            if (foreground) {
                observer.onForeground(activity);
            } else {
                observer.onBackground(activity);
            }
        }
    }
    @Override
    public void onActivityPaused(final Activity activity) {
        activityCount--;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isChecked && activityCount == 0) {
                    isChecked = false;
                    AppHandler.this.notify(activity, false);
                }
            }
        }, HANDLER_DELAY);
    }




}
