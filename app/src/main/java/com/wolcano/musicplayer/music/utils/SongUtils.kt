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
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import android.widget.Toast
import androidx.core.text.HtmlCompat
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.data.model.Song
import com.wolcano.musicplayer.music.data.model.SongOnline
import java.io.File

object SongUtils {

    const val SELECTION = MediaStore.Audio.Media.IS_MUSIC + " != 0"
    private var mContentValuesCache: Array<ContentValues?>? = null

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
                song.fileName = fileName
                song.fileSize = fileSize
                alist.add(song)
            }
            cursor.close()
            return alist
        }
        return alist
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
            val message: CharSequence = HtmlCompat.fromHtml(
                context.getString(
                    R.string.number_song_add_playlist,
                    musicTitle,
                    getNameFromPlaylist(context, playlistid)
                ),
                HtmlCompat.FROM_HTML_MODE_LEGACY
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
                if (fileStr.contains(context.getString(R.string.base_path_prefix))) {
                    fileStr.replace(context.getString(R.string.base_path_prefix), "")
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
            if (fileStr.contains(context.getString(R.string.base_path_prefix))) {
                fileStr.replace(context.getString(R.string.base_path_prefix), "")
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