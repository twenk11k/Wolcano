package com.wolcano.musicplayer.music.ui.fragment.online

import android.app.Activity
import com.wolcano.musicplayer.music.model.Song
import com.wolcano.musicplayer.music.model.SongOnline

abstract class PlayModelLocal(activity: Activity, private val songOnlineList: List<SongOnline>?) :
    PlayModelOnline(
        activity
    ) {

    override fun setPlayModel() {
        songList = ArrayList()
        for (i in songOnlineList!!.indices) {
            val artist = songOnlineList[i].artistName
            val title = songOnlineList[i].title
            song = Song()
            song?.type = Song.Tip.MODEL1
            song?.title = title
            song?.artist = artist
            song?.path = songOnlineList[i].path
            song?.duration = songOnlineList[i].duration * 1000
            songList?.add(song!!)
            if (i == songOnlineList.size - 1) {
                onTaskDone(songList)
            }
        }

    }

}