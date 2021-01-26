package com.wolcano.musicplayer.music.provider

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.constants.Constants
import com.wolcano.musicplayer.music.content.managers.SessionManager
import com.wolcano.musicplayer.music.content.managers.SessionManager.setSessionManager
import com.wolcano.musicplayer.music.provider.notifications.Notification
import com.wolcano.musicplayer.music.provider.notifications.NotificationImpl
import com.wolcano.musicplayer.music.provider.notifications.NotificationLatestImpl
import com.wolcano.musicplayer.music.provider.notifications.NotificationOldImpl

class MusicService : Service() {

    private var uiThreadHandler: Handler? = null
    private var sessionManager: SessionManager? = null
    private var becomingNoisyReceiverRegistered = false
    private val becomingNoisyIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)

    private var notification: Notification? = null

    private val becomingNoisyReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                RemotePlay.pauseRemotePlay(context)
            }
        }
    }

    fun getNotification(): Notification? {
        return notification
    }

    inner class ServiceInit : Binder() {
        val musicService: MusicService
            get() = this@MusicService
    }

    fun registerReceiv() {
        if (!becomingNoisyReceiverRegistered) {
            registerReceiver(becomingNoisyReceiver, becomingNoisyIntentFilter)
            becomingNoisyReceiverRegistered = true
        }
    }

    fun getSessionManager(): SessionManager? {
        return sessionManager
    }

    override fun onCreate() {
        super.onCreate()
        RemotePlay.init(this.applicationContext)
        setSessionManager(this)
        sessionManager = SessionManager
        uiThreadHandler = Handler()
        setNotification()
    }

    private fun stopService() {
        RemotePlay.stopRemotePlay(this.applicationContext)
        notification?.stop()
    }

    private fun setNotification() {
        notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationLatestImpl()
        } else if (Build.VERSION.SDK_INT >= 21) {
            NotificationImpl()
        } else {
            NotificationOldImpl()
        }
        notification?.init(this)
    }

    fun runOnUiThread(runnable: Runnable?) {
        uiThreadHandler?.post(runnable)
    }

    override fun onBind(intent: Intent?): IBinder {
        return ServiceInit()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.action != null) {
            when (intent.action) {
                Constants.ACTION_STOP -> stopService()
                Constants.ACTION_STOP_SLEEP -> {
                    if (RemotePlay.isPlaying()) {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.sleeptimerstopped_stop),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.sleeptimerstopped_plain),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    stopService()
                }
                Constants.ACTION_TOGGLE_PAUSE -> RemotePlay
                    .buttonClick(this.applicationContext)
                Constants.ACTION_REWIND -> RemotePlay.prev(this.applicationContext)
                Constants.ACTION_SKIP -> RemotePlay.next(this.applicationContext, false)
                Constants.ACTION_PAUSE -> {
                    if (RemotePlay.isPlaying()) {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.sleeptimerstopped_pause),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.sleeptimerstopped_plain),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    RemotePlay.pauseRemotePlay(this.applicationContext)
                }
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        if (becomingNoisyReceiverRegistered) {
            unregisterReceiver(becomingNoisyReceiver)
            becomingNoisyReceiverRegistered = false
        }
        SessionManager.mediaSessionCompat?.isActive = false
        RemotePlay.pauseRemotePlay(this)
        notification?.stop()
        stopSelf()
        SessionManager.mediaSessionCompat?.release()
    }

}