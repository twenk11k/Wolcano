package com.wolcano.musicplayer.music.content.managers

import android.content.Context
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import com.wolcano.musicplayer.music.provider.RemotePlay.getMediaPlayer
import com.wolcano.musicplayer.music.provider.RemotePlay.pauseRemotePlay
import com.wolcano.musicplayer.music.provider.RemotePlay.startRemotePlay

class SoundManager(private val context: Context) : OnAudioFocusChangeListener {

    private var audioManager: AudioManager? = null
    private var isPause = false

    init {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> getMediaPlayer()!!.setVolume(0.5f, 0.5f)
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                pauseRemotePlay(context.applicationContext, false)
                isPause = true
            }
            AudioManager.AUDIOFOCUS_LOSS -> pauseRemotePlay(context.applicationContext)
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (isPause) {
                    startRemotePlay(context.applicationContext)
                }
                getMediaPlayer()?.setVolume(1f, 1f)
                isPause = false
            }
        }
    }

    fun requestAudioFocus(): Boolean {
        return (audioManager?.requestAudioFocus(
            this,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
    }

    fun abandonAudioFocus() {
        audioManager?.abandonAudioFocus(this)
    }

}