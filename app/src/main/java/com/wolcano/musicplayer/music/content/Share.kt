package com.wolcano.musicplayer.music.content

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.MediaStore
import android.widget.Toast
import com.wolcano.musicplayer.music.R
import java.io.File

object Share {

    private const val TYPE_ARTIST = 0
    private const val TYPE_ALBUM = 1
    private const val TYPE_SONG = 2

    private const val DEFAULT_SORT = "artist_key,album_key,track"
    private const val ALBUM_SORT = "album_key,track"

    fun shareSong(ctx: Context, type: Int, id: Long) {
        val resolver = ctx.contentResolver
        val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA)
        val cursor: Cursor =
            buildQuery(type, id, projection, null).runQuery(resolver)
                ?: return
        try {
            while (cursor.moveToNext()) {
                val songFile = File(cursor.getString(1))
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "audio/*"
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(songFile))
                val builder = VmPolicy.Builder()
                StrictMode.setVmPolicy(builder.build())
                ctx.startActivity(Intent.createChooser(intent, ctx.getString(R.string.sharefile)))
            }
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(ctx, "Error", Toast.LENGTH_SHORT).show()
        } finally {
            cursor.close()
        }
    }

    private fun buildQuery(
        type: Int,
        id: Long,
        projection: Array<String>,
        selection: String?
    ): TaskQuery {
        return when (type) {
            TYPE_ARTIST, TYPE_ALBUM, TYPE_SONG -> buildMediaQuery(
                type,
                id,
                projection,
                selection
            )
            else -> throw IllegalArgumentException("Specified type not valid: $type")
        }
    }

    private fun buildMediaQuery(
        type: Int,
        id: Long,
        projection: Array<String>,
        select: String?
    ): TaskQuery {
        val media = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = StringBuilder()
        var sort = DEFAULT_SORT
        when (type) {
            TYPE_SONG -> selection.append(MediaStore.Audio.Media._ID)
            TYPE_ARTIST -> selection.append(MediaStore.Audio.Media.ARTIST_ID)
            TYPE_ALBUM -> {
                selection.append(MediaStore.Audio.Media.ALBUM_ID)
                sort = ALBUM_SORT
            }
            else -> throw java.lang.IllegalArgumentException("Invalid type specified: $type")
        }
        selection.append('=')
        selection.append(id)
        selection.append(" AND length(_data) AND " + MediaStore.Audio.Media.IS_MUSIC)
        if (select != null) {
            selection.append(" AND ")
            selection.append(select)
        }
        return TaskQuery(media, projection, selection.toString(), null, sort)
    }

}