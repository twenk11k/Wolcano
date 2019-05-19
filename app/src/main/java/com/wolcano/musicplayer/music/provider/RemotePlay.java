package com.wolcano.musicplayer.music.provider;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.content.PlayerEnum;
import com.wolcano.musicplayer.music.content.managers.SessionManager;
import com.wolcano.musicplayer.music.content.managers.SoundManager;
import com.wolcano.musicplayer.music.mvp.listener.OnServiceListener;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.mvp.db.DatabaseManager;
import com.wolcano.musicplayer.music.utils.ToastUtils;
import com.wolcano.musicplayer.music.utils.Utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class RemotePlay {

    private SoundManager soundManager;
    private MediaPlayer mediaPlayer;
    private IntentFilter intentFilter;
    private List<Song> arrayList;

    private final List<OnServiceListener> listenerList = new ArrayList<>();
    private static final int IDLE = 0;
    private static final int PLAY = 2;

    private Handler mHandler;

    private static final long GUNCEL = 300L;
    private static final int PREPARE = 1;

    private int mState = IDLE;
    private MusicService musicService;
    private static final int PAUSE = 3;
    private ServiceConnection serviceConnection;

    public static RemotePlay get() {
        return SingletonHolder.singletonInstance;
    }

    private static class SingletonHolder {
        private static RemotePlay singletonInstance = new RemotePlay();
    }

    private RemotePlay() {
    }
    public void init(Context context) {
        arrayList = DatabaseManager.get().getAppDao().queryBuilder().build().list();
        soundManager = new SoundManager(context);
        mediaPlayer = new MediaPlayer();
        mHandler = new Handler(Looper.getMainLooper());
        bindService(context);
        intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

        mediaPlayer.setOnCompletionListener(mp -> next(context,true));
        mediaPlayer.setOnPreparedListener(mp -> {
            if (isPreparing()) {
                startRemotePlay(context);
            }
        });
        mediaPlayer.setOnBufferingUpdateListener((mp, percent) -> {
            for (OnServiceListener listener : listenerList) {
                listener.onBufferingUpdate(percent);
            }
        });
    }
    private void bindService(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, MusicService.class);
        serviceConnection = new RemoteServiceConn();
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    public void playAdd(Context context,List<Song> alist, Song song) {
        arrayList.clear();

        for (int i = 0; i < alist.size(); i++) {
            arrayList.add(alist.get(i));
        }

        int position = arrayList.indexOf(song);
        DatabaseManager.get().getAppDao().deleteAll();
        DatabaseManager.get().getAppDao().insert(song);
        playSong(context,position);

    }
    public void onListener(OnServiceListener listener) {
        if (!listenerList.contains(listener)) {
            listenerList.add(listener);
        }
    }

    public void removeListener(OnServiceListener listener) {
        listenerList.remove(listener);
    }

    public void buttonClick(Context context) {
        if (isPreparing()) {
        } else if (isPlaying()) {
            pauseRemotePlay(context);
        } else if (isPausing()) {
            startRemotePlay(context);
        } else  {
            playSong(context,getRemotePlayPos(context));
        }
    }


    public void playSong(Context context,int position) {
        if (arrayList.isEmpty()) {
            return;
        }

        if (position < 0) {
            position = arrayList.size() - 1;
        } else if (position >= arrayList.size()) {
            position = 0;
        }

        setRemotePlayPos(context,position);
        Song song = getPlayMusic(context);
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(song.getPath());
            if(song.getTip()== Song.Tip.MODEL1){
                mediaPlayer.prepareAsync();
            } else {
                mediaPlayer.prepare();
            }
            mState = PREPARE;
            for (OnServiceListener listener : listenerList) {
                listener.onChangeSong(song);
            }

            musicService.notification.update(song);
            SessionManager.get().updateSessionMetaData(song);
            SessionManager.get().updateSessionPlaybackState();
        } catch (IOException e) {
            e.printStackTrace();
            ToastUtils.show(context.getApplicationContext(),context.getString(R.string.cannot_play));
        }
    }
    public void next(Context context,boolean isCompletion) {
        if (arrayList.isEmpty()) {
            return;
        }

        PlayerEnum mode = PlayerEnum.valueOf(Utils.getPlaylistId(context));
        switch (mode) {
            case SHUFFLE:
                playSong(context,new Random().nextInt(arrayList.size()));
                break;
            case REPEAT:
                if (isCompletion) {
                    playSong(context,getRemotePlayPos(context));
                } else {
                    playSong(context,getRemotePlayPos(context) + 1);
                }
                break;
            case NORMAL:
            default:
                playSong(context,getRemotePlayPos(context) + 1);
                break;
        }
    }


    public void deleteFromRemotePlay(Context context,int size, int position,Song song) {
        int playPosition = getRemotePlayPos(context);
        if(position<=arrayList.size()-1){

            Song song1 = arrayList.get(position);
            if (size == arrayList.size()) {
                if(song1.getSongId()== song.getSongId()){
                    arrayList.remove(position);
                    if (DatabaseManager.get().getAppDao().hasKey(song1)) {
                        DatabaseManager.get().getAppDao().delete(song1);
                    }
                }
            }
            if(song1.getSongId()== song.getSongId()){
                boolean isClosed = false;
                if (playPosition > position) {
                    setRemotePlayPos(context,playPosition - 1);
                } else if (playPosition == position) {
                    if (position == 0 && playPosition == 0) {
                        stopRemotePlay(context);
                        musicService.notification.stop();
                        for (OnServiceListener listener : listenerList) {

                            listener.onChangeSong(getPlayMusic(context));
                        }
                        isClosed = true;
                    } else if (isPlaying() || isPreparing()) {
                        next(context,false);
                        setRemotePlayPos(context,playPosition - 1);
                    } else if (!isClosed) {
                        stopRemotePlay(context);
                        for (OnServiceListener listener : listenerList) {

                            listener.onChangeSong(getPlayMusic(context));
                        }
                    }
                }
            }    }

    }

    public void prev(Context context) {
        if (arrayList.isEmpty()) {
            return;
        }

        PlayerEnum mode = PlayerEnum.valueOf(Utils.getPlaylistId(context));
        switch (mode) {
            case SHUFFLE:
                playSong(context,new Random().nextInt(arrayList.size()));
                break;
            case REPEAT:
                playSong(context,getRemotePlayPos(context) - 1);
                break;
            case NORMAL:
            default:
                playSong(context,getRemotePlayPos(context) - 1);
                break;
        }
    }


    public void startRemotePlay(Context context) {
        if (!isPreparing() && !isPausing()) {
            return;
        }

        if (soundManager.reqSoundFocus()) {
            mediaPlayer.start();
            musicService.registerReceiv();
            mState = PLAY;
            mHandler.post(remoteRunnable);

            musicService.notification.update(getPlayMusic(context));
            SessionManager.get().updateSessionPlaybackState();

            for (OnServiceListener listener : listenerList) {
                listener.onPlayStart();
            }
        }
    }

    public void pauseRemotePlay(Context context) {
        pauseRemotePlay(context,true);
    }

    public void pauseRemotePlay(Context context,boolean abSoundFocus) {
        if (!isPlaying()) {
            return;
        }

        mediaPlayer.pause();
        mState = PAUSE;
        mHandler.removeCallbacks(remoteRunnable);

        musicService.notification.update(getPlayMusic(context));
        SessionManager.get().updateSessionPlaybackState();
        if (abSoundFocus) {
            soundManager.abSoundFocus();
        }

        for (OnServiceListener listener : listenerList) {
            listener.onPlayPause();
        }
    }
    private Runnable remoteRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlaying()) {
                for (OnServiceListener listener : listenerList) {
                    listener.onProgressChange(mediaPlayer.getCurrentPosition());
                }
            }
            mHandler.postDelayed(this, GUNCEL);
        }
    };
    public void stopRemotePlay(Context context) {
        if (isItIDLE()) {
            return;
        }

        pauseRemotePlay(context);
        mediaPlayer.reset();
        mState = IDLE;
    }

    private class RemoteServiceConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((MusicService.ServiceInit) service).getMusicService();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }


    public long getSoundPos() {
        if (isPlaying() || isPausing()) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }
    public void seekTo(int msec) {
        if (isPlaying() || isPausing()) {
            mediaPlayer.seekTo(msec);
            SessionManager.get().updateSessionPlaybackState();
            for (OnServiceListener listener : listenerList) {
                listener.onProgressChange(msec);
            }
        }
    }


    public boolean isPreparing() {
        return mState == PREPARE;
    }

    private boolean isItIDLE() {
        return mState == IDLE;
    }
  public List<Song> getArrayList() {
        return arrayList;
    }

    public Song getPlayMusic(Context context) {
        if (arrayList.isEmpty()) {
            return null;
        }
        return arrayList.get(getRemotePlayPos(context));
    }
    public int getRemotePlayPos(Context context) {
        int position = Utils.getPlaylistPos(context);
        if (position < 0 || position >= arrayList.size()) {
            position = 0;
            Utils.savePlayPosition(context,position);
        }
        return position;
    }

    public boolean isPlaying() {
        return mState == PLAY;
    }

    public boolean isPausing() {
        return mState == PAUSE;
    }
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }



    private void setRemotePlayPos(Context context,int position) {
        Utils.savePlayPosition(context,position);
    }

}
