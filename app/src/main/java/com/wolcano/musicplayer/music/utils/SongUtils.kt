package com.wolcano.musicplayer.music.utils

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Playlists
import android.text.Html
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import android.widget.Toast
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.constants.Constants.SONG_ONLY_SELECTION
import com.wolcano.musicplayer.music.mvp.models.*
import java.io.File
import java.util.*

object SongUtils {

    private const val SELECTION = MediaStore.Audio.Media.IS_MUSIC + " != 0"
    private var mContentValuesCache: Array<ContentValues?>? = null

    @JvmStatic
    fun scanSongs(context: Context?, sort: String?): List<Song> {
        val alist: MutableList<Song> = ArrayList()
        if (context != null) {
            val cursor = context.contentResolver.query(
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
                SELECTION,
                null, sort
            ) ?: return alist
            while (cursor.moveToNext()) {
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
                alist.add(song)
            }
            cursor.close()
            return alist
        }
        return alist
    }

    fun scanSongsCursor(context: Context?, cursor: Cursor?): List<Song> {
        val alist: MutableList<Song> = ArrayList()
        if (context != null) {
            if (cursor == null) {
                return alist
            }
            while (cursor.moveToNext()) {
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
                alist.add(song)
            }
            cursor.close()
            return alist
        }
        return alist
    }

    @JvmStatic
    fun scanSongsforAlbum(context: Context?, sortOrder: String?, albumid: Long): List<Song> {
        val alist: MutableList<Song> = ArrayList()
        if (context != null) {
            val selection = "is_music=1 AND title != '' AND album_id=$albumid"
            val cursor = context.contentResolver.query(
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
                null, sortOrder
            ) ?: return alist
            while (cursor.moveToNext()) {
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
                alist.add(song)
            }
            cursor.close()
            return alist
        }
        return alist
    }

    @JvmStatic
    fun scanSongsforArtist(context: Context?, sort: String?, artistID: Long): List<Song> {
        val alist: MutableList<Song> = ArrayList()
        if (context != null) {
            val selection = "is_music=1 AND title != '' AND artist_id=$artistID"
            val cursor = context.contentResolver.query(
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
            ) ?: return alist
            while (cursor.moveToNext()) {
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
                alist.add(song)
            }
            cursor.close()
            return alist
        }
        return alist
    }

    @JvmStatic
    fun scanSongsforGenre(context: Context?, sort: String?, genreID: Long): List<Song> {
        val alist: MutableList<Song> = ArrayList()
        if (context != null) {
            val selection = "is_music=1 AND title != '' AND genre_id=$genreID"
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Genres.Members.getContentUri("external", genreID), arrayOf(
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
            ) ?: return alist
            while (cursor.moveToNext()) {
                val isMusic =
                    cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC))
                if (isMusic == 0) {
                    continue
                }
                scanSongs(context, sort)
                if (scanSongs(context, sort).size > 0) {
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
                    alist.add(song)
                }
            }
            cursor.close()
            return alist
        }
        return alist
    }

    @JvmStatic
    fun scanSongsforPlaylist(context: Context?, sort: String?, playlistID: Long): List<Song> {
        val alist: MutableList<Song> = ArrayList()
        if (context != null) {
            val mSelection = StringBuilder()
            mSelection.append(MediaStore.Audio.AudioColumns.IS_MUSIC + "=1")
            mSelection.append(" AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''")
            val cursor = context.contentResolver.query(
                Playlists.Members.getContentUri("external", playlistID),
                arrayOf(
                    Playlists.Members._ID,
                    Playlists.Members.AUDIO_ID,
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
                    Playlists.Members.PLAY_ORDER
                ),
                mSelection.toString(),
                null,
                sort
            ) ?: return alist
            while (cursor.moveToNext()) {
                val isMusic =
                    cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC))
                if (isMusic == 0) {
                    continue
                }
                val id = cursor.getLong(cursor.getColumnIndex(Playlists.Members.AUDIO_ID))
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
                alist.add(song)
            }
            cursor.close()
            return alist
        }
        return alist
    }

    @JvmStatic
    fun scanPlaylist(context: Context?): List<Playlist> {
        val playlistList: MutableList<Playlist> = ArrayList()
        if (context != null) {
            val cursor = context.contentResolver.query(
                Playlists.EXTERNAL_CONTENT_URI, arrayOf(
                    BaseColumns._ID,
                    MediaStore.Audio.PlaylistsColumns.NAME
                ),
                null,
                null, Playlists.DEFAULT_SORT_ORDER
            ) ?: return playlistList
            while (cursor.moveToNext()) {
                val id = cursor.getLong(0)
                val name = cursor.getString(1)
                val songCount = getSongCountForPlaylist(context, id)
                val playlist = Playlist(id, name, songCount)
                playlistList.add(playlist)
            }
            cursor.close()
            return playlistList
        }
        return playlistList
    }

    private fun getSongCountForPlaylist(context: Context?, playlistId: Long): Int {
        if (context != null) {
            var c = context.contentResolver.query(
                Playlists.Members.getContentUri("external", playlistId),
                arrayOf(BaseColumns._ID),
                SONG_ONLY_SELECTION,
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
        return 0
    }

    fun addToPlaylist(context: Context?, id: Long, playlistid: Long, musicTitle: String?) {
        if (context != null) {
            val size = 1
            val resolver = context.contentResolver
            val projection = arrayOf(
                "max(" + "play_order" + ")"
            )
            val uri = Playlists.Members.getContentUri("external", playlistid)
            var cursor: Cursor? = null
            var base = 0
            try {
                cursor = resolver.query(uri, projection, null, null, null)
                if (cursor != null && cursor.moveToFirst()) {
                    base = cursor.getInt(0) + 1
                }
            } finally {
                if (cursor != null) {
                    cursor.close()
                }
            }
            var numinserted = 0
            var offSet = 0
            while (offSet < size) {
                makeInsertItems(id, offSet, 1000, base)
                numinserted += resolver.bulkInsert(uri, mContentValuesCache)
                offSet += 1000
            }
            val message: CharSequence = Html.fromHtml(
                context.getString(
                    R.string.number_song_add_playlist,
                    musicTitle,
                    getNameFromPlaylist(context, playlistid)
                )
            )
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    fun makeInsertItems(id: Long, offset: Int, len: Int, base: Int) {
        var len = len
        if (offset + len > 1) {
            len = 1 - offset
        }
        if (mContentValuesCache == null || mContentValuesCache!!.size != len) {
            mContentValuesCache = arrayOfNulls(len)
        }
        for (i in 0 until len) {
            if (mContentValuesCache!![i] == null) {
                mContentValuesCache!![i] = ContentValues()
            }
            mContentValuesCache!![i]!!
                .put(Playlists.Members.PLAY_ORDER, base + offset + i)
            mContentValuesCache!![i]!!
                .put(Playlists.Members.AUDIO_ID, id)
        }
    }

    fun createPlaylist(context: Context?, name: String?): Long {
        if (context != null) {
            if (name != null && name.length > 0) {
                val resolver = context.contentResolver
                val projection = arrayOf(
                    MediaStore.Audio.PlaylistsColumns.NAME
                )
                val selection = MediaStore.Audio.PlaylistsColumns.NAME + " = '" + name + "'"
                var cursor = resolver.query(
                    Playlists.EXTERNAL_CONTENT_URI,
                    projection, selection, null, null
                )
                if (cursor!!.count <= 0) {
                    val values = ContentValues(1)
                    values.put(MediaStore.Audio.PlaylistsColumns.NAME, name)
                    val uri = resolver.insert(
                        Playlists.EXTERNAL_CONTENT_URI,
                        values
                    )
                    return uri.lastPathSegment.toLong()
                }
                cursor.close()
                return -1
            }
            return -1
        }
        return -1
    }

    fun getNameFromPlaylist(context: Context?, id: Long): String {
        if (context != null) {
            try {
                val cursor = context.contentResolver.query(
                    Playlists.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Audio.PlaylistsColumns.NAME),
                    BaseColumns._ID + "=?", arrayOf(id.toString()),
                    null
                )
                if (cursor != null) {
                    try {
                        if (cursor.moveToFirst()) {
                            return cursor.getString(0)
                        }
                    } finally {
                        cursor.close()
                    }
                }
            } catch (ignored: SecurityException) {
            }
            return ""
        }
        return ""
    }

    fun downPerform(context: Context?, song: Song?) {
        if (context != null) {
            Toast.makeText(context.applicationContext, R.string.itsstart, Toast.LENGTH_SHORT).show()
            val path = song?.path
            val file = File(
                Environment.getExternalStorageDirectory()
                    .toString() + "/" + context.getString(R.string.folder_name)
            )
            if (!file.exists()) {
                file.mkdirs()
            }
            val loadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val loadRequest = DownloadManager.Request(Uri.parse(path))
            loadRequest.setTitle(song?.title)
            loadRequest.setDescription(context.getString(R.string.artist_notification) + song?.artist)
            loadRequest.allowScanningByMediaScanner()
            loadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            val fileStr =
                URLUtil.guessFileName(path, null, MimeTypeMap.getFileExtensionFromUrl(path))
            val fileStr2: String? =
                if (fileStr.contains(context.resources.getString(R.string.base_path_prefix))) {
                    fileStr.replace(context.resources.getString(R.string.base_path_prefix), "")
                } else {
                    fileStr
                }
            try {
                loadRequest.setDestinationInExternalPublicDir(
                    "/" + context.getString(R.string.folder_name),
                    fileStr2
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            loadManager.enqueue(loadRequest)
        }
    }

    fun downPerform(context: Context, songOnline: SongOnline) {
        Toast.makeText(context.applicationContext, R.string.itsstart, Toast.LENGTH_SHORT).show()
        val path = songOnline.path
        val file = File(
            Environment.getExternalStorageDirectory()
                .toString() + "/" + context.getString(R.string.folder_name)
        )
        if (!file.exists()) {
            file.mkdirs()
        }
        val loadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val loadRequest = DownloadManager.Request(Uri.parse(path))
        loadRequest.setTitle(songOnline.title)
        loadRequest.setDescription(context.getString(R.string.artist_notification) + songOnline.artistName)
        loadRequest.allowScanningByMediaScanner()
        loadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        val fileStr = URLUtil.guessFileName(path, null, MimeTypeMap.getFileExtensionFromUrl(path))
        val fileStr2 =
            if (fileStr.contains(context.resources.getString(R.string.base_path_prefix))) {
                fileStr.replace(context.resources.getString(R.string.base_path_prefix), "")
            } else {
                fileStr
            }
        try {
            loadRequest.setDestinationInExternalPublicDir(
                "/" + context.getString(R.string.folder_name),
                fileStr2
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        loadManager.enqueue(loadRequest)
    }

    fun renamePlaylist(context: Context?, playlistid: Long, newName: String?) {
        if (context != null) {
            val resolver = context.contentResolver
            val values = ContentValues(1)
            values.put(Playlists.NAME, newName)
            resolver.update(
                Playlists.EXTERNAL_CONTENT_URI,
                values, "_id=$playlistid", null
            )
        }
    }

    fun deletePlaylists(context: Context?, playlistId: Long) {
        if (context != null) {
            val localUri = Playlists.EXTERNAL_CONTENT_URI
            val localStringBuilder = StringBuilder()
            localStringBuilder.append("_id IN (")
            localStringBuilder.append(playlistId)
            localStringBuilder.append(")")
            context.contentResolver.delete(localUri, localStringBuilder.toString(), null)
        }
    }

    fun removeFromPlaylist(
        context: Context?, ids: LongArray,
        playlistId: Long
    ) {
        if (context != null) {
            val uri = Playlists.Members.getContentUri("external", playlistId)
            val resolver = context.contentResolver
            val selection = StringBuilder()
            selection.append(Playlists.Members.AUDIO_ID + " IN (")
            for (i in ids.indices) {
                selection.append(ids[i])
                if (i < ids.size - 1) {
                    selection.append(",")
                }
            }
            selection.append(")")
            resolver.delete(uri, selection.toString(), null)
        }
    }

    @JvmStatic
    fun scanGenre(context: Context?): List<Genre> {
        val genreList: MutableList<Genre> = ArrayList()
        if (context != null) {
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, arrayOf(
                    MediaStore.Audio.Genres._ID,
                    MediaStore.Audio.Genres.NAME
                ),
                null,
                null, MediaStore.Audio.Genres.DEFAULT_SORT_ORDER
            ) ?: return genreList
            while (cursor.moveToNext()) {
                val genre = getGenreFromCursor(context, cursor)
                if (genre.songCount > 0) {
                    genreList.add(genre)
                } else {
                    try {
                        context.contentResolver.delete(
                            MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
                            MediaStore.Audio.Genres._ID + " == " + genre.id,
                            null
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            cursor.close()
            return genreList
        }
        return genreList
    }

    @JvmStatic
    fun scanArtists(context: Context?): List<Artist> {
        val artists: MutableList<Artist> = ArrayList()
        if (context != null) {
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, arrayOf(
                    MediaStore.Audio.Artists._ID,
                    MediaStore.Audio.Artists.ARTIST,
                    MediaStore.Audio.Artists.NUMBER_OF_TRACKS
                ),
                null,
                null, MediaStore.Audio.Artists.ARTIST_KEY
            ) ?: return artists
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Artists._ID))
                val name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST))
                val songCount =
                    cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS))
                val artist = Artist(id, name, songCount)
                artists.add(artist)
            }
            cursor.close()
            return artists
        }
        return artists
    }

    @JvmStatic
    fun scanAlbums(context: Context?): List<Album> {
        val albums: MutableList<Album> = ArrayList()
        if (context != null) {
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, arrayOf(
                    MediaStore.Audio.Albums._ID,
                    MediaStore.Audio.Albums.ALBUM,
                    MediaStore.Audio.Albums.ARTIST
                ),
                null,
                null, MediaStore.Audio.Albums.ALBUM_KEY
            ) ?: return albums
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Albums._ID))
                val name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM))
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST))
                val album = Album(id, name, artist)
                albums.add(album)
            }
            cursor.close()
            return albums
        }
        return albums
    }

    private fun getGenreFromCursor(context: Context?, cursor: Cursor): Genre {
        if (context != null) {
            val id = cursor.getInt(0)
            val name = cursor.getString(1)
            val songs = scanSongsForGenre(context, id).size
            return Genre(id.toLong(), name, songs)
        }
        return Genre(-1, "", 0)
    }

    fun scanSongsForGenre(context: Context?, genreID: Int): List<Song> {
        val alist: MutableList<Song> = ArrayList()
        if (context != null) {
            val cursor = context.contentResolver.query(
                MediaStore.Audio.Genres.Members.getContentUri("external", genreID.toLong()),
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
                SELECTION,
                null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER
            ) ?: return alist
            while (cursor.moveToNext()) {
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
                alist.add(song)
            }
            cursor.close()
            return alist
        }
        return alist
    }

    fun buildSongListFromFile(context: Context, file: File): List<Song?>? {
        val cur = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            MediaStore.Audio.Media.DATA + " like ?", arrayOf("%" + file.parent + "/%"),
            MediaStore.Audio.Media.DATA + " ASC"
        ) ?: return null
        val songs = scanSongsCursor(context, cur)
        cur.close()
        return songs
    }

}