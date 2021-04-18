package com.wolcano.musicplayer.music.data.repository

import android.provider.BaseColumns
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import com.skydoves.whatif.whatIfNotNull
import com.wolcano.musicplayer.music.App
import com.wolcano.musicplayer.music.data.model.Song
import com.wolcano.musicplayer.music.utils.SongUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SongRepository {

    @WorkerThread
    fun retrieveSongs(
        sort: String
    ) = flow<List<Song>> {

        val list = ArrayList<Song>()
        val cursor = App.getContext().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, arrayOf(
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
            null, sort
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
        list.apply {
            this.whatIfNotNull {
                emit(it)
            }
        }
    }.flowOn(Dispatchers.IO)

    @WorkerThread
    fun retrieveGenreSongs(
        sort: String,
        genreId: Long?
    ) = flow<List<Song>> {
        if (genreId == null)
            return@flow
        val list: MutableList<Song> = java.util.ArrayList()
        val selection = "is_music=1 AND title != '' AND genre_id=$genreId"
        val cursor = App.getContext().contentResolver.query(
            MediaStore.Audio.Genres.Members.getContentUri("external", genreId), arrayOf(
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
            selection,
            null, sort
        )
        while (cursor != null && cursor.moveToNext()) {
            val isMusic =
                cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC))
            if (isMusic == 0) {
                continue
            }
            if (getSongsSize(sort) > 0) {
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
                val fileSize =
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE))
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
        }
        cursor?.close()
        list.apply {
            this.whatIfNotNull {
                emit(it)
            }
        }
    }.flowOn(Dispatchers.IO)

    @WorkerThread
    fun retrievePlaylistSongs(
        sort: String,
        playlistId: Long?
    ) = flow<List<Song>> {
        if (playlistId == null)
            return@flow
        val list: MutableList<Song> = java.util.ArrayList()
        val mSelection = StringBuilder()
        mSelection.append(MediaStore.Audio.AudioColumns.IS_MUSIC + "=1")
        mSelection.append(" AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''")
        val cursor = App.getContext().contentResolver.query(
            MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId), arrayOf(
                MediaStore.Audio.Playlists.Members._ID,
                MediaStore.Audio.Playlists.Members.AUDIO_ID,
                BaseColumns._ID,
                MediaStore.Audio.AudioColumns.IS_MUSIC,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.ALBUM_ID,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                MediaStore.Audio.AudioColumns.SIZE,
                MediaStore.Audio.AudioColumns.DURATION,
                MediaStore.Audio.Playlists.Members.PLAY_ORDER
            ),
            mSelection.toString(),
            null, sort
        )
        while (cursor.moveToNext()) {
            val isMusic =
                cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC))
            if (isMusic == 0) {
                continue
            }
            val id =
                cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID))
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
        cursor.close()
        list.apply {
            this.whatIfNotNull {
                emit(it)
            }
        }
    }.flowOn(Dispatchers.IO)

    @WorkerThread
    fun retrieveArtistSongs(
        sort: String,
        artistId: Long?
    ) = flow<List<Song>> {

        val list: MutableList<Song> = java.util.ArrayList()
        val selection = "is_music=1 AND title != '' AND artist_id=$artistId"
        val cursor = App.getContext().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, arrayOf(
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
            selection,
            null, sort
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
        list.apply {
            this.whatIfNotNull {
                emit(it)
            }
        }
    }.flowOn(Dispatchers.IO)


    @WorkerThread
    fun retrieveAlbumSongs(
        sort: String,
        albumId: Long?
    ) = flow<List<Song>> {

        val list: MutableList<Song> = java.util.ArrayList()

        val selection = "is_music=1 AND title != '' AND album_id=$albumId"
        val cursor = App.getContext().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, arrayOf(
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
            selection,
            null, sort
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
        list.apply {
            this.whatIfNotNull {
                emit(it)
            }
        }
    }.flowOn(Dispatchers.IO)

    private fun getSongsSize(sort: String): Int {
        val list = java.util.ArrayList<Song>()
        val cursor = App.getContext().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, arrayOf(
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
            null, sort
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