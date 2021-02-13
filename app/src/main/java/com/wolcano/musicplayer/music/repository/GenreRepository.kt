package com.wolcano.musicplayer.music.repository

import android.provider.BaseColumns
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import com.skydoves.whatif.whatIfNotNull
import com.wolcano.musicplayer.music.App
import com.wolcano.musicplayer.music.model.Genre
import com.wolcano.musicplayer.music.model.Song
import com.wolcano.musicplayer.music.utils.SongUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GenreRepository {

    @WorkerThread
    fun retrieveGenres() = flow<List<Genre>> {
        val list = ArrayList<Genre>()
        val cursor = App.getContext().contentResolver.query(
            MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, arrayOf(
                MediaStore.Audio.Genres._ID,
                MediaStore.Audio.Genres.NAME
            ),
            null,
            null, MediaStore.Audio.Genres.DEFAULT_SORT_ORDER
        )
        while (cursor != null && cursor.moveToNext()) {
            val genre = Genre(-1, "", 0)
            genre.id = cursor.getInt(0).toLong()
            genre.name = cursor.getString(1)
            genre.songCount = getSongCountForGenre(genre.id)
            if (genre.songCount > 0) {
                list.add(genre)
            } else {
                try {
                    App.getContext().contentResolver.delete(
                        MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
                        MediaStore.Audio.Genres._ID + " == " + genre.id,
                        null
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        cursor?.close()
        list.apply {
            this.whatIfNotNull {
                emit(it)
            }
        }
    }.flowOn(Dispatchers.IO)

    private fun getSongCountForGenre(genreId: Long): Int {
        val list: MutableList<Song> = ArrayList()
            val cursor = App.getContext().contentResolver.query(
                MediaStore.Audio.Genres.Members.getContentUri("external", genreId),
                arrayOf(
                    BaseColumns._ID,
                    MediaStore.Audio.AudioColumns.IS_MUSIC,
                    MediaStore.Audio.AudioColumns.TITLE,
                    MediaStore.Audio.AudioColumns.ARTIST,
                    MediaStore.Audio.AudioColumns.ALBUM,
                    MediaStore.Audio.AudioColumns.ALBUM_ID,
                    MediaStore.Audio.AudioColumns.DATA,
                    MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                    MediaStore.Audio.AudioColumns.SIZE,
                    MediaStore.Audio.AudioColumns.DURATION
                ),
                SongUtils.SELECTION,
                null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER
            )
            while (cursor != null && cursor.moveToNext()) {
                val isMusic =
                    cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC))
                if (isMusic == 0) {
                    continue
                }
                val id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID))
                val title =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE))
                val artist =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST))
                val album =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM))
                val albumId =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID))
                val duration =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                val path =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA))
                val fileName =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME))
                val fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE))
                val song = Song()
                song.songId = id
                song.type = Song.Tip.MODEL0
                song.title = title
                song.artist = artist
                song.album = album
                song.albumId = albumId
                song.duration = duration
                song.path = path
                song.dosName = fileName
                song.dosSize = fileSize
                list.add(song)
            }
            cursor?.close()
            return list.size
    }
    
}