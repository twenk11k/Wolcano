package com.wolcano.musicplayer.music.provider.notifications

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.constants.Constants.ACTION_QUIT
import com.wolcano.musicplayer.music.constants.Constants.ACTION_REWIND
import com.wolcano.musicplayer.music.constants.Constants.ACTION_SKIP
import com.wolcano.musicplayer.music.constants.Constants.ACTION_STOP
import com.wolcano.musicplayer.music.constants.Constants.ACTION_TOGGLE_PAUSE
import com.wolcano.musicplayer.music.data.model.Song
import com.wolcano.musicplayer.music.provider.MusicService
import com.wolcano.musicplayer.music.provider.RemotePlay.isPlaying

class NotificationOldImpl : Notification() {

    private fun getPrevIcon(): Int {
        return R.drawable.baseline_skip_previous_black_36
    }

    private fun getPlayIcon(isPlaying: Boolean): Int {
        return if (isPlaying) {
            R.drawable.baseline_pause_black_36
        } else {
            R.drawable.baseline_play_arrow_black_36
        }
    }

    private fun getNextIcon(): Int {
        return R.drawable.baseline_skip_next_black_36
    }

    private fun getCloseIcon(): Int {
        return R.drawable.baseline_close_black_36
    }

    override fun update(song: Song?) {
        stopped = false
        val isPlaying = isPlaying()
        val notificationLayout = RemoteViews(service!!.packageName, R.layout.notification_old)
        val notificationLayoutExpanded =
            RemoteViews(service!!.packageName, R.layout.notification_old_expanded)
        notificationLayout.setTextViewText(R.id.line1, song?.title)
        notificationLayout.setTextViewText(R.id.line2, song?.artist)
        notificationLayoutExpanded.setTextViewText(R.id.line1, song?.title)
        notificationLayoutExpanded.setTextViewText(R.id.line2, song?.artist)
        notificationLayoutExpanded.setTextViewText(R.id.line3, song?.album)
        linkButtons(notificationLayout)
        linkButtons(notificationLayoutExpanded)
        val action = service!!.packageManager
            .getLaunchIntentForPackage(service!!.packageName)
            .setPackage(null)
            .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        val clickIntent = PendingIntent.getActivity(service, 0, action, 0)
        val deleteIntent: PendingIntent = buildPendingIntent(service!!, ACTION_QUIT, null)
        val notification = NotificationCompat.Builder(service!!, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(clickIntent)
            .setDeleteIntent(deleteIntent)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContent(notificationLayout)
            .setOngoing(isPlaying)
            .build()
        service!!.runOnUiThread(object : Runnable {
            override fun run() {
                val contentURI =
                    "content://media/external/audio/media/" + song?.songId + "/albumart"
                Picasso.get().load(contentURI).into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
                        update(bitmap)
                    }

                    override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {}
                    override fun onPrepareLoad(placeHolderDrawable: Drawable) {
                        update(null)
                    }

                    fun update(bitmap: Bitmap?) {
                        var bitmap = bitmap
                        if (bitmap == null) {
                            bitmap = BitmapFactory.decodeResource(
                                service!!.resources,
                                R.drawable.album_art
                            )
                        }
                        notificationLayout.setImageViewBitmap(R.id.albumArt, bitmap)
                        notificationLayoutExpanded.setImageViewBitmap(R.id.albumArt, bitmap)
                        setNotificationContent()
                        if (stopped) return
                        updateNotifyModeAndPostNotification(notification)
                    }
                })
            }

            private fun setNotificationContent() {
                notificationLayout.setImageViewResource(R.id.prev, getPrevIcon())
                notificationLayout.setImageViewResource(R.id.next, getNextIcon())
                notificationLayout.setImageViewResource(R.id.close, getCloseIcon())
                notificationLayout.setImageViewResource(R.id.playpause, getPlayIcon(isPlaying))
                notificationLayoutExpanded.setImageViewResource(R.id.prev, getPrevIcon())
                notificationLayoutExpanded.setImageViewResource(R.id.next, getNextIcon())
                notificationLayoutExpanded.setImageViewResource(R.id.close, getCloseIcon())
                notificationLayoutExpanded.setImageViewResource(
                    R.id.playpause,
                    getPlayIcon(isPlaying)
                )
            }
        })
    }

    private fun buildPendingIntent(
        context: Context,
        action: String,
        serviceName: ComponentName?
    ): PendingIntent {
        val intent = Intent(action)
        intent.component = serviceName
        return PendingIntent.getService(context, 0, intent, 0)
    }


    private fun linkButtons(notificationLayout: RemoteViews) {
        var pendingIntent: PendingIntent?
        val serviceName = ComponentName(service, MusicService::class.java)

        // Previous track
        pendingIntent = buildPendingIntent(service!!, ACTION_REWIND, serviceName)
        notificationLayout.setOnClickPendingIntent(R.id.prev, pendingIntent)

        // Play and pause
        pendingIntent = buildPendingIntent(service!!, ACTION_TOGGLE_PAUSE, serviceName)
        notificationLayout.setOnClickPendingIntent(R.id.playpause, pendingIntent)

        // Next track
        pendingIntent = buildPendingIntent(service!!, ACTION_SKIP, serviceName)
        notificationLayout.setOnClickPendingIntent(R.id.next, pendingIntent)
        // close
        pendingIntent = buildPendingIntent(service!!, ACTION_STOP, serviceName)
        notificationLayout.setOnClickPendingIntent(R.id.close, pendingIntent)
    }
}