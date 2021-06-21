package com.wolcano.musicplayer.music.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import com.wolcano.musicplayer.music.provider.RemotePlay.buttonClick
import com.wolcano.musicplayer.music.provider.RemotePlay.next
import com.wolcano.musicplayer.music.provider.RemotePlay.prev

class RemoteBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val keyEvent = intent?.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
        if (keyEvent == null || keyEvent.action != KeyEvent.ACTION_UP) {
            return
        }
        when (keyEvent.keyCode) {
            KeyEvent.KEYCODE_HEADSETHOOK -> buttonClick(context)
            KeyEvent.KEYCODE_MEDIA_NEXT -> next(context, false)
            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> prev(context)
        }
    }

}