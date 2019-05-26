package com.wolcano.musicplayer.music.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wolcano.musicplayer.music.provider.RemotePlay;


public class SoundBroadcast extends BroadcastReceiver {
    /**
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        RemotePlay.get().buttonClick(context);
    }
}
