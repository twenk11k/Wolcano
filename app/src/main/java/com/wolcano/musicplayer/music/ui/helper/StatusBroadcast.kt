package com.wolcano.musicplayer.music.ui.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.wolcano.musicplayer.music.provider.RemotePlay.buttonClick
import com.wolcano.musicplayer.music.provider.RemotePlay.next
import com.wolcano.musicplayer.music.provider.RemotePlay.prev

class StatusBroadcast : BroadcastReceiver() {

    val STATUS_EXTRA_PLAY_PAUSE = "play_pause"
    val STATUS_EXTRA_PREV = "prev"
    val STATUS_EXTRA_NEXT = "next"
    val STATUS_EXTRA = "extra"

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null || TextUtils.isEmpty(intent.action)) {
            return
        }
        val extraIntent = intent.getStringExtra(STATUS_EXTRA)
        when {
            TextUtils.equals(extraIntent, STATUS_EXTRA_NEXT) -> {
                next(context, false)
            }
            TextUtils.equals(extraIntent, STATUS_EXTRA_PLAY_PAUSE) -> {
                buttonClick(context)
            }
            TextUtils.equals(extraIntent, STATUS_EXTRA_PREV) -> {
                prev(context)
            }
        }
    }
}