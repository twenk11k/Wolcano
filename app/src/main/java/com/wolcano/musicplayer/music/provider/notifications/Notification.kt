package com.wolcano.musicplayer.music.provider.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.model.Song
import com.wolcano.musicplayer.music.provider.MusicService
import com.wolcano.musicplayer.music.provider.RemotePlay.isPlaying

abstract class Notification {

    private var notificationManager: NotificationManager? = null
    private val NOTIFICATION_ID = 1337
    private val NOTIFY_MODE_BACKGROUND = 0
    private val NOTIFY_MODE_FOREGROUND = 1
    private var notifyMode = NOTIFY_MODE_BACKGROUND

    var service: MusicService? = null
    var stopped = false
    val NOTIFICATION_CHANNEL_ID = "thenotification"

    @Synchronized
    open fun init(service: MusicService) {
        this.service = service
        notificationManager =
            service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

    open fun updateNotifyModeAndPostNotification(notification: Notification?) {
        val newNotifyMode: Int = if (isPlaying()) {
            NOTIFY_MODE_FOREGROUND
        } else {
            NOTIFY_MODE_BACKGROUND
        }
        if (notifyMode != newNotifyMode && newNotifyMode == NOTIFY_MODE_BACKGROUND) {
            service?.stopForeground(false)
        }
        if (newNotifyMode == NOTIFY_MODE_FOREGROUND) {
            service?.startForeground(NOTIFICATION_ID, notification)
        } else if (newNotifyMode == NOTIFY_MODE_BACKGROUND) {
            notificationManager?.notify(NOTIFICATION_ID, notification)
        }
        notifyMode = newNotifyMode
    }

    abstract fun update(song: Song?)

    @Synchronized
    open fun stop() {
        stopped = true
        service?.stopForeground(true)
        notificationManager?.cancel(NOTIFICATION_ID)
    }

    @RequiresApi(26)
    private fun createNotificationChannel() {
        var notificationChannel =
            notificationManager?.getNotificationChannel(NOTIFICATION_CHANNEL_ID)
        if (notificationChannel == null) {
            notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                service!!.getString(R.string.notification),
                NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.description =
                service!!.getString(R.string.playing_notification_description)
            notificationChannel.enableLights(false)
            notificationChannel.enableVibration(false)
            notificationChannel.setShowBadge(false)
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }
}