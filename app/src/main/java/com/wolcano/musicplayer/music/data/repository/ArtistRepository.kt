package com.wolcano.musicplayer.music.data.repository

import android.provider.MediaStore
import androidx.annotation.WorkerThread
import com.skydoves.whatif.whatIfNotNull
import com.wolcano.musicplayer.music.App
import com.wolcano.musicplayer.music.data.model.Artist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ArtistRepository {

    @WorkerThread
    fun retrieveArtists() = flow<List<Artist>> {
        val list = ArrayList<Artist>()
        val cursor = App.getContext().contentResolver.query(
            MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, arrayOf(
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS
            ),
            null,
            null, MediaStore.Audio.Artists.ARTIST_KEY
        )
        while (cursor != null && cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Artists._ID))
            val name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST))
            val songCount =
                cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS))
            val artist = Artist(id, name, songCount)
            list.add(artist)
        }
        cursor?.close()
        list.apply {
            this.whatIfNotNull {
                emit(it)
            }
        }
    }.flowOn(Dispatchers.IO)

}