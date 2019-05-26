package com.wolcano.musicplayer.music.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import com.wolcano.musicplayer.music.provider.RemotePlay;


public class RemoteBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        KeyEvent keyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        if (keyEvent == null || keyEvent.getAction() != KeyEvent.ACTION_UP) {
            return;
        }
        switch (keyEvent.getKeyCode()) {
            case KeyEvent.KEYCODE_HEADSETHOOK:
                RemotePlay.get().buttonClick(context);
                break;
            case KeyEvent.KEYCODE_MEDIA_NEXT:
                RemotePlay.get().next(context,false);
                break;
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                RemotePlay.get().prev(context);
                break;
        }
    }
}
