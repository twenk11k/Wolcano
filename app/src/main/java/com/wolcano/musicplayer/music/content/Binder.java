package com.wolcano.musicplayer.music.content;

import android.app.Activity;
import android.view.View;

import com.wolcano.musicplayer.music.mvp.listener.Bind;

import java.lang.reflect.Field;

public class Binder {

    public static void bindIt(Activity activity) {
        bindIt(activity, activity.getWindow().getDecorView());
    }
    public static void bindIt(Object target, View source) {
        Field[] fieldArr = target.getClass().getDeclaredFields();
        if (fieldArr.length > 0 && fieldArr != null) {
            for (Field f : fieldArr) {
                try {
                    f.setAccessible(true);
                    if (f.get(target) != null) {
                        continue;
                    }
                    Bind bind = f.getAnnotation(Bind.class);
                    if (bind != null) {
                        int id = bind.value();
                        f.set(target, source.findViewById(id));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
