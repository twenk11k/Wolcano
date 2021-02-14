package com.wolcano.musicplayer.music.repository

import android.provider.BaseColumns
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import com.skydoves.whatif.whatIfNotNull
import com.wolcano.musicplayer.music.App
import com.wolcano.musicplayer.music.constants.Constants
import com.wolcano.musicplayer.music.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PlaylistRepository {

    @WorkerThread
    fun retrievePlaylists() = flow<List<Playlist>> {
        val list = ArrayList<Playlist>()
        val cursor = App.getContext().contentResolver.query(
            MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, arrayOf(
                BaseColumns._ID,
                MediaStore.Audio.PlaylistsColumns.NAME
            ),
            null,
            null, MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER
        )
        while (cursor != null && cursor.moveToNext()) {
            val id = cursor.getLong(0)
            val name = cursor.getString(1)
            val songCount = getSongCountForPlaylist(id)
            val playlist = Playlist(id, name, songCount)
            list.add(playlist)
        }
        cursor?.close()
        list.apply {
            this.whatIfNotNull {
                emit(it)
            }
        }
    }.flowOn(Dispatchers.IO)

    private fun getSongCountForPlaylist(playlistId: Long): Int {
        val c = App.getContext().contentResolver.query(
            MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId),
            arrayOf(BaseColumns._ID),
            Constants.SONG_ONLY_SELECTION,
            null,
            null
        )
        if (c != null) {
            var count = 0
            if (c.moveToFirst()) {
                count = c.count
            }
            c.close()
            return count
        }
        return 0
    }

}