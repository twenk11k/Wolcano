package com.wolcano.musicplayer.music.provider;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.Toast;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.content.managers.SessionManager;
import com.wolcano.musicplayer.music.provider.notifications.Notification;
import com.wolcano.musicplayer.music.provider.notifications.NotificationImpl;
import com.wolcano.musicplayer.music.provider.notifications.NotificationLatestImpl;
import com.wolcano.musicplayer.music.provider.notifications.NotificationOldImpl;
import static com.wolcano.musicplayer.music.constants.Constants.ACTION_PAUSE;
import static com.wolcano.musicplayer.music.constants.Constants.ACTION_REWIND;
import static com.wolcano.musicplayer.music.constants.Constants.ACTION_SKIP;
import static com.wolcano.musicplayer.music.constants.Constants.ACTION_STOP;
import static com.wolcano.musicplayer.music.constants.Constants.ACTION_STOP_SLEEP;
import static com.wolcano.musicplayer.music.constants.Constants.ACTION_TOGGLE_PAUSE;


public class MusicService extends Service {

    private Handler uiThreadHandler;
    private SessionManager sessionManager;
    private boolean becomingNoisyReceiverRegistered;
    private IntentFilter becomingNoisyIntentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    private Notification notification;


    private final BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            if (intent.getAction().equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
                RemotePlay.get().pauseRemotePlay(context);
            }
        }
    };

    public Notification getNotification(){
        return notification;
    }


    public class ServiceInit extends Binder {
        public MusicService getMusicService() {
            return MusicService.this;
        }
    }
    public void registerReceiv(){
        if (!becomingNoisyReceiverRegistered) {
            registerReceiver(becomingNoisyReceiver, becomingNoisyIntentFilter);
            becomingNoisyReceiverRegistered = true;
        }
    }
    public SessionManager getSessionManager(){
        return sessionManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RemotePlay.get().init(this.getApplicationContext());
        SessionManager.get().setSessionManager(this);
        sessionManager = SessionManager.get();
        uiThreadHandler = new Handler();
        setNotification();
    }

    private void stopService() {
        RemotePlay.get().stopRemotePlay(this.getApplicationContext());
        notification.stop();
    }
    private void setNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notification = new NotificationLatestImpl();
        } else if (Build.VERSION.SDK_INT >= 21) {
            notification = new NotificationImpl();
        } else {
            notification = new NotificationOldImpl();
        }
        notification.init(this);
    }

    public void runOnUiThread(Runnable runnable) {
        uiThreadHandler.post(runnable);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ServiceInit();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {

            switch (intent.getAction()) {
                case ACTION_STOP:
                    stopService();
                    break;
                case ACTION_STOP_SLEEP:
                    if(RemotePlay.get().isPlaying()){
                        Toast.makeText(getApplicationContext(), getString(R.string.sleeptimerstopped_stop), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),getString(R.string.sleeptimerstopped_plain),Toast.LENGTH_SHORT).show();
                    }
                    stopService();
                    break;
                case ACTION_TOGGLE_PAUSE:
                    RemotePlay.get().buttonClick(this.getApplicationContext());
                    break;
                case ACTION_REWIND:
                    RemotePlay.get().prev(this.getApplicationContext());
                    break;
                case ACTION_SKIP:
                    RemotePlay.get().next(this.getApplicationContext(),false);
                    break;
                case ACTION_PAUSE:
                    if(RemotePlay.get().isPlaying()){
                        Toast.makeText(getApplicationContext(), getString(R.string.sleeptimerstopped_pause), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),getString(R.string.sleeptimerstopped_plain),Toast.LENGTH_SHORT).show();
                    }
                    RemotePlay.get().pauseRemotePlay(this.getApplicationContext());
                    break;

            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (becomingNoisyReceiverRegistered) {
            unregisterReceiver(becomingNoisyReceiver);
            becomingNoisyReceiverRegistered = false;
        }
        SessionManager.get().getMediaSessionCompat().setActive(false);
        RemotePlay.get().pauseRemotePlay(this);
        notification.stop();
        stopSelf();
        SessionManager.get().getMediaSessionCompat().release();

    }

}
