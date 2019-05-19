package com.wolcano.musicplayer.music.content.managers;

import android.content.Context;
import android.media.AudioManager;

import com.wolcano.musicplayer.music.provider.RemotePlay;

import static android.content.Context.AUDIO_SERVICE;

public class SoundManager implements AudioManager.OnAudioFocusChangeListener {

    private AudioManager audioManager;
    private boolean isPause;
    private Context context;

    public SoundManager(Context context) {
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                RemotePlay.get().getMediaPlayer().setVolume(0.5f, 0.5f);
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                RemotePlay.get().pauseRemotePlay(context.getApplicationContext(),false);
                isPause = true;
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                RemotePlay.get().pauseRemotePlay(context.getApplicationContext());
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                if (isPause) {
                    RemotePlay.get().startRemotePlay(context.getApplicationContext());
                }
                RemotePlay.get().getMediaPlayer().setVolume(1f, 1f);
                isPause = false;
                break;

        }
    }



    public boolean reqSoundFocus() {
        return audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public void abSoundFocus() {
        audioManager.abandonAudioFocus(this);
    }

}
