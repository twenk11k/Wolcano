package com.wolcano.musicplayer.music.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wolcano.musicplayer.music.provider.RemotePlay

class SoundBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        RemotePlay.buttonClick(context)
    }

}
