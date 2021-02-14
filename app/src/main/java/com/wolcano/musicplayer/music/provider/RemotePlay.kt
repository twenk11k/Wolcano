package com.wolcano.musicplayer.music.provider

import android.content.*
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.content.PlayerEnum
import com.wolcano.musicplayer.music.content.managers.SessionManager.updateSessionMetaData
import com.wolcano.musicplayer.music.content.managers.SessionManager.updateSessionPlaybackState
import com.wolcano.musicplayer.music.content.managers.SoundManager
import com.wolcano.musicplayer.music.listener.OnServiceListener
import com.wolcano.musicplayer.music.model.Song
import com.wolcano.musicplayer.music.persistence.AppDatabase
import com.wolcano.musicplayer.music.provider.MusicService.ServiceInit
import com.wolcano.musicplayer.music.utils.ToastUtils
import com.wolcano.musicplayer.music.utils.Utils
import java.io.IOException
import java.util.*
import javax.inject.Singleton

object RemotePlay {

    private var soundManager: SoundManager? = null
    private var mediaPlayer: MediaPlayer? = null
    private var intentFilter: IntentFilter? = null
    private var songList: MutableList<Song?>? = null

    private val listenerList: MutableList<OnServiceListener> = ArrayList()
    private const val IDLE = 0
    private const val PLAY = 2

    private var handler: Handler? = null

    private const val GUNCEL = 300L
    private const val PREPARE = 1

    private var state = IDLE
    private var musicService: MusicService? = null
    private const val PAUSE = 3
    private var serviceConnection: ServiceConnection? = null

    fun init(context: Context) {

        songList = AppDatabase.getInstance(context).songDao().getAll() as MutableList<Song?>
        soundManager = SoundManager(context)
        mediaPlayer = MediaPlayer()
        handler = Handler(Looper.getMainLooper())


        bindService(context)
        intentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        mediaPlayer?.setOnCompletionListener {
            next(
                context,
                true
            )
        }
        mediaPlayer?.setOnPreparedListener {
            if (isPreparing()) {
                startRemotePlay(context)
            }
        }
        mediaPlayer?.setOnBufferingUpdateListener { _: MediaPlayer?, percent: Int ->
            for (listener in listenerList) {
                listener.onBufferingUpdate(percent)
            }
        }
    }

    private fun bindService(context: Context) {
        val intent = Intent()
        intent.setClass(context, MusicService::class.java)
        serviceConnection = RemoteServiceConn()
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun playAdd(context: Context, alist: List<Song?>, song: Song) {
        songList?.clear()
        songList?.addAll(alist)
        val position = songList!!.indexOf(song)
        AppDatabase.getInstance(context).songDao().deleteAll()
        AppDatabase.getInstance(context).songDao().insert(song)
        playSong(context, position)
    }


    fun onListener(listener: OnServiceListener) {
        if (!listenerList.contains(listener)) {
            listenerList.add(listener)
        }
    }

    fun removeListener(listener: OnServiceListener?) {
        listenerList.remove(listener)
    }

    fun buttonClick(context: Context) {
        if (isPlaying()) {
            pauseRemotePlay(context)
        } else if (isPausing()) {
            startRemotePlay(context)
        } else {
            playSong(context, getRemotePlayPos(context))
        }
    }

    fun playSong(context: Context, position: Int) {
        var position = position
        if (songList!!.isEmpty()) {
            return
        }
        if (position < 0) {
            position = songList!!.size - 1
        } else if (position >= songList!!.size) {
            position = 0
        }
        setRemotePlayPos(context, position)
        val song: Song? = getPlayMusic(context)
        try {
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(song!!.path)
            if (song!!.type == Song.Tip.MODEL1) {
                mediaPlayer?.prepareAsync()
            } else {
                mediaPlayer?.prepare()
            }
            state = PREPARE
            for (listener in listenerList) {
                listener.onChangeSong(song)
            }
            musicService?.getNotification()?.update(song)
            updateSessionMetaData(song)
            updateSessionPlaybackState()
        } catch (e: IOException) {
            e.printStackTrace()
            ToastUtils.show(context.applicationContext, context.getString(R.string.cannot_play))
        }
    }

    fun next(context: Context, isCompletion: Boolean) {
        if (songList!!.isEmpty()) {
            return
        }
        val mode = PlayerEnum.valueOf(Utils.getPlaylistId(context))
        when (mode) {
            PlayerEnum.SHUFFLE -> playSong(context, Random().nextInt(songList!!.size))
            PlayerEnum.REPEAT -> if (isCompletion) {
                playSong(context, getRemotePlayPos(context))
            } else {
                playSong(context, getRemotePlayPos(context) + 1)
            }
            PlayerEnum.NORMAL -> playSong(context, getRemotePlayPos(context) + 1)
            else -> playSong(context, getRemotePlayPos(context) + 1)
        }
    }

    fun deleteFromRemotePlay(context: Context, size: Int, position: Int, song: Song) {
        val playPosition: Int = getRemotePlayPos(context)
        if (position <= songList!!.size - 1) {
            val song1 = songList!![position]
            if (size == songList!!.size) {
                if (song1!!.songId == song.songId) {
                    songList?.removeAt(position)
                    AppDatabase.getInstance(context).songDao().delete(song1)
                }
            }
            if (song1!!.songId == song.songId) {
                var isClosed = false
                if (playPosition > position) {
                    setRemotePlayPos(context, playPosition - 1)
                } else if (playPosition == position) {
                    if (position == 0 && playPosition == 0) {
                        stopRemotePlay(context)
                        musicService?.getNotification()!!.stop()
                        for (listener in listenerList) {
                            listener.onChangeSong(getPlayMusic(context))
                        }
                    } else if (isPlaying() || isPreparing()) {
                        next(context, false)
                        setRemotePlayPos(context, playPosition - 1)
                    } else if (!isClosed) {
                        stopRemotePlay(context)
                        for (listener in listenerList) {
                            listener.onChangeSong(getPlayMusic(context))
                        }
                    }
                }
            }
        }
    }

    fun prev(context: Context) {
        if (songList!!.isEmpty()) {
            return
        }
        when (PlayerEnum.valueOf(Utils.getPlaylistId(context))) {
            PlayerEnum.SHUFFLE -> playSong(context, Random().nextInt(songList!!.size))
            PlayerEnum.REPEAT -> playSong(context, getRemotePlayPos(context) - 1)
            PlayerEnum.NORMAL -> playSong(context, getRemotePlayPos(context) - 1)
            else -> playSong(context, getRemotePlayPos(context) - 1)
        }
    }

    fun startRemotePlay(context: Context) {
        if (!isPreparing() && !isPausing()) {
            return
        }
        if (soundManager!!.requestAudioFocus()) {
            mediaPlayer?.start()
            musicService?.registerReceiv()
            state = PLAY
            handler?.post(remoteRunnable)
            musicService?.getNotification()!!.update(getPlayMusic(context))
            updateSessionPlaybackState()
            for (listener in listenerList) {
                listener.onPlayStart()
            }
        }
    }

    fun pauseRemotePlay(context: Context) {
        pauseRemotePlay(context, true)
    }

    fun pauseRemotePlay(context: Context, abandonAudioFocus: Boolean) {
        if (!isPlaying()) {
            return
        }
        mediaPlayer?.pause()
        state = PAUSE
        handler?.removeCallbacks(remoteRunnable)
        musicService?.getNotification()!!.update(getPlayMusic(context))
        updateSessionPlaybackState()
        if (abandonAudioFocus) {
            soundManager?.abandonAudioFocus()
        }
        for (listener in listenerList) {
            listener.onPlayPause()
        }
    }


    private val remoteRunnable: Runnable = object : Runnable {
        override fun run() {
            if (isPlaying()) {
                for (listener in listenerList) {
                    listener.onProgressChange(mediaPlayer!!.currentPosition)
                }
            }
            handler?.postDelayed(this, GUNCEL)
        }
    }

    fun stopRemotePlay(context: Context) {
        if (isItIDLE()) {
            return
        }
        pauseRemotePlay(context)
        mediaPlayer?.reset()
        state = IDLE
    }

    fun getPlayerCurrentPosition(): Long {
        return if (isPlaying() || isPausing()) {
            mediaPlayer!!.currentPosition.toLong()
        } else {
            0
        }
    }

    fun seekTo(msec: Int) {
        if (isPlaying() || isPausing()) {
            mediaPlayer?.seekTo(msec)
            updateSessionPlaybackState()
            for (listener in listenerList) {
                listener.onProgressChange(msec)
            }
        }
    }

    fun isPreparing(): Boolean {
        return state == PREPARE
    }

    private fun isItIDLE(): Boolean {
        return state == IDLE
    }

    fun getSongList(): MutableList<Song?>? {
        return songList
    }

    fun getPlayMusic(context: Context): Song? {
        return if (songList == null || songList!!.isEmpty()) {
            null
        } else songList!![getRemotePlayPos(context)]
    }

    fun getRemotePlayPos(context: Context): Int {
        var position = Utils.getPlaylistPos(context)
        if (position < 0 || position >= songList!!.size) {
            position = 0
            Utils.savePlayPosition(context, position)
        }
        return position
    }

    fun isPlaying(): Boolean {
        return state == PLAY
    }

    fun isPausing(): Boolean {
        return state == PAUSE
    }

    fun getMediaPlayer(): MediaPlayer? {
        return mediaPlayer
    }

    private fun setRemotePlayPos(context: Context, position: Int) {
        Utils.savePlayPosition(context, position)
    }

    @Singleton
    private class RemoteServiceConn : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            musicService = (service as ServiceInit).musicService
        }

        override fun onServiceDisconnected(name: ComponentName) {}
    }

}