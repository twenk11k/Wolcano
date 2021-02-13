package com.wolcano.musicplayer.music.provider.notifications

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.constants.Constants.ACTION_QUIT
import com.wolcano.musicplayer.music.constants.Constants.ACTION_REWIND
import com.wolcano.musicplayer.music.constants.Constants.ACTION_SKIP
import com.wolcano.musicplayer.music.constants.Constants.ACTION_TOGGLE_PAUSE
import com.wolcano.musicplayer.music.model.Song
import com.wolcano.musicplayer.music.provider.MusicService
import com.wolcano.musicplayer.music.provider.RemotePlay.isPlaying

class NotificationLatestImpl : Notification() {

    @Synchronized
    override fun update(song: Song?) {
        if (song == null) {
            return
        }
        stopped = false
        val albumName = song.album
        val artistName = song.artist
        val text = if (TextUtils.isEmpty(albumName)) artistName else "$artistName - $albumName"
        if (service != null) {
            val action = service!!.packageManager
                .getLaunchIntentForPackage(service!!.packageName)
                .setPackage(null)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            val clickIntent = PendingIntent.getActivity(service, 0, action, 0)
            val serviceName = ComponentName(service, MusicService::class.java)
            val intent = Intent(ACTION_QUIT)
            intent.component = serviceName
            val deleteIntent = PendingIntent.getService(service, 0, intent, 0)
            service?.runOnUiThread {
                val contentURI = "content://media/external/audio/media/" + song.songId + "/albumart"
                Picasso.get().load(contentURI).into(object : Target {

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: LoadedFrom?) {
                        update(bitmap)
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        update(null)
                    }

                    private fun update(bitmap: Bitmap?) {
                        var bitmap = bitmap
                        if (bitmap == null) {
                            bitmap =
                                BitmapFactory.decodeResource(
                                    service!!.resources,
                                    R.drawable.album_art
                                )
                        }
                        val isPlaying = isPlaying()
                        val playButtonResId: Int =
                            if (isPlaying) R.drawable.baseline_pause_black_36 else R.drawable.baseline_play_arrow_black_36
                        val playPauseAction = NotificationCompat.Action(
                            playButtonResId,
                            service!!.getString(R.string.action_play_pause),
                            retrievePlaybackAction(ACTION_TOGGLE_PAUSE)
                        )
                        val previousAction: NotificationCompat.Action =
                            NotificationCompat.Action(
                                R.drawable.baseline_skip_previous_black_36,
                                service?.getString(R.string.action_previous),
                                retrievePlaybackAction(ACTION_REWIND)
                            )
                        val nextAction: NotificationCompat.Action =
                            NotificationCompat.Action(
                                R.drawable.baseline_skip_next_black_36,
                                service?.getString(R.string.action_next),
                                retrievePlaybackAction(ACTION_SKIP)
                            )
                        if (bitmap == null) {
                            bitmap =
                                BitmapFactory.decodeResource(
                                    service!!.resources,
                                    R.drawable.album_art
                                )
                        }
                        val builder = NotificationCompat.Builder(
                            service!!, NOTIFICATION_CHANNEL_ID
                        )
                            .setSmallIcon(R.drawable.baseline_music_note_white_24)
                            .setLargeIcon(bitmap)
                            .setContentIntent(clickIntent)
                            .setDeleteIntent(deleteIntent)
                            .setContentTitle(song.title)
                            .setContentText(text)
                            .setOngoing(isPlaying)
                            .setShowWhen(false)
                            .addAction(previousAction)
                            .addAction(playPauseAction)
                            .addAction(nextAction)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            if(service != null && service?.getSessionManager() != null && service?.getSessionManager()?.mediaSessionCompat != null) {
                                builder.setStyle(
                                    androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(
                                        service!!.getSessionManager()!!.mediaSessionCompat!!.sessionToken
                                    ).setShowActionsInCompactView(0, 1, 2)
                                )
                                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            }
                        }
                        if (stopped) return
                        updateNotifyModeAndPostNotification(builder.build())
                    }
                })
            }
        }
    }

    private fun retrievePlaybackAction(action: String): PendingIntent? {
        val serviceName = ComponentName(service, MusicService::class.java)
        val intent = Intent(action)
        intent.component = serviceName
        return PendingIntent.getService(service, 0, intent, 0)
    }

}