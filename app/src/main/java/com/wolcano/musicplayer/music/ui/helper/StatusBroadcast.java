package com.wolcano.musicplayer.music.ui.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.wolcano.musicplayer.music.provider.RemotePlay;


public class StatusBroadcast extends BroadcastReceiver {

    public static final String STATUS_EXTRA_PLAY_PAUSE = "play_pause";
    public static final String STATUS_EXTRA_PREV = "prev";
    public static final String STATUS_EXTRA_NEXT = "next";
    public static final String STATUS_EXTRA = "extra";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || TextUtils.isEmpty(intent.getAction())) {
            return;
        }
        String extraIntent = intent.getStringExtra(STATUS_EXTRA);
        if (TextUtils.equals(extraIntent, STATUS_EXTRA_NEXT)) {
            RemotePlay.get().next(context,false);
        } else if (TextUtils.equals(extraIntent, STATUS_EXTRA_PLAY_PAUSE)) {
            RemotePlay.get().buttonClick(context);
        } else  if (TextUtils.equals(extraIntent, STATUS_EXTRA_PREV)) {
            RemotePlay.get().prev(context);
        }
    }
}
