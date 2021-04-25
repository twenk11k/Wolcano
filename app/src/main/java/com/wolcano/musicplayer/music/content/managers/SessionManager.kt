package com.wolcano.musicplayer.music.content.managers

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import com.wolcano.musicplayer.music.AppCache
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.data.model.Song
import com.wolcano.musicplayer.music.provider.MusicService
import com.wolcano.musicplayer.music.provider.RemotePlay

object SessionManager {

    lateinit var musicService: MusicService
    var mediaSessionCompat: MediaSessionCompat? = null

    fun setSessionManager(musicService: MusicService) {
        this.musicService = musicService
        initMediaSessionCompat()
    }

    fun updateSessionPlaybackState() {
        val state =
            if (RemotePlay.isPlaying() || RemotePlay.isPreparing()) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
        val mediaSessionActions = (PlaybackStateCompat.ACTION_PLAY
                or PlaybackStateCompat.ACTION_SEEK_TO
                or PlaybackStateCompat.ACTION_PLAY_PAUSE
                or PlaybackStateCompat.ACTION_PAUSE
                or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                or PlaybackStateCompat.ACTION_STOP)
        mediaSessionCompat?.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setActions(mediaSessionActions)
                .setState(state, RemotePlay.getPlayerCurrentPosition(), 1f)
                .build()
        )
    }

    private fun initMediaSessionCompat() {
        mediaSessionCompat = MediaSessionCompat(musicService, "SessionManager")
        mediaSessionCompat?.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mediaSessionCompat?.setCallback(sessionCallback)
        mediaSessionCompat?.isActive = true
    }

    private val sessionCallback: MediaSessionCompat.Callback =
        object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                RemotePlay.buttonClick(musicService)
            }

            override fun onPause() {
                RemotePlay.buttonClick(musicService)
            }

            override fun onSkipToNext() {
                RemotePlay.next(musicService, false)
            }

            override fun onSkipToPrevious() {
                RemotePlay.prev(musicService)
            }

            override fun onStop() {
                RemotePlay.stopRemotePlay(musicService)
            }

            override fun onSeekTo(pos: Long) {
                RemotePlay.seekTo(pos.toInt())
            }
        }

    fun updateSessionMetaData(song: Song?) {
        if (song == null) {
            mediaSessionCompat?.setMetadata(null)
            return
        }
        val contentURI = "content://media/external/audio/media/" + song.songId + "/albumart"
        Picasso.get().load(contentURI).into(object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
                update(bitmap)
            }

            override fun onBitmapFailed(e: Exception, errorDrawable: Drawable?) {}
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                update(null)
            }

            private fun update(bitmap: Bitmap?) {
                var bitmap = bitmap
                if (bitmap == null) {
                    bitmap =
                        BitmapFactory.decodeResource(musicService.resources, R.drawable.album_art)
                }
                val metaData = MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, song.artist)
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, song.album)
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST, song.artist)
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, song.duration)
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, bitmap)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    metaData.putLong(
                        MediaMetadataCompat.METADATA_KEY_NUM_TRACKS,
                        AppCache.songList.size.toLong()
                    )
                }
                mediaSessionCompat?.setMetadata(metaData.build())
            }
        })
    }

}