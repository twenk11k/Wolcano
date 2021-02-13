package com.wolcano.musicplayer.music.repository

import android.provider.MediaStore
import androidx.annotation.WorkerThread
import com.skydoves.whatif.whatIfNotNull
import com.wolcano.musicplayer.music.App
import com.wolcano.musicplayer.music.model.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AlbumRepository {

    @WorkerThread
    fun retrieveAlbums() = flow<List<Album>> {
        val list = ArrayList<Album>()
        val cursor = App.getContext().contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, arrayOf(
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST
            ),
            null,
            null, MediaStore.Audio.Albums.ALBUM_KEY
        )
        while (cursor != null && cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Albums._ID))
            val name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM))
            val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST))
            val album = Album(id, name, artist)
            list.add(album)
        }
        cursor?.close()
        list.apply {
            this.whatIfNotNull {
                emit(it)
            }
        }
    }.flowOn(Dispatchers.IO)

}