package com.wolcano.musicplayer.music.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

    private static Toast toastMsg;

    public static void show(Context context, String str) {
        if (toastMsg == null) {
            toastMsg = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        } else {
            toastMsg.setText(str);
        }
        toastMsg.show();
    }

    public static void show(Context context, int id) {
        show(context, context.getString(id));
    }

}
