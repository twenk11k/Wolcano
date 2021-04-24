package com.wolcano.musicplayer.music.utils

import android.annotation.TargetApi
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore

object UriFilesUtils {

    private const val DOCUMENTS_AUTHORITY = "com.android.externalstorage.documents"
    private const val DOWNLOADS_AUTHORITY = "com.android.providers.downloads.documents"
    private const val MEDIA_AUTHORITY = "com.android.providers.media.documents"

    fun getPathFromUri(context: Context, contentUri: Uri): String? {
        if ("file" == contentUri.scheme) {
            return contentUri.path
        }
        val docProviderPath = getPathFromDocumentProviderUri(context, contentUri)
        if (docProviderPath != null && docProviderPath.trim { it <= ' ' }.isNotEmpty()) {
            return docProviderPath
        }
        return if ("content" == contentUri.scheme) {
            getPathFromGeneralUri(context, contentUri)
        } else null
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun getPathFromDocumentProviderUri(context: Context, uri: Uri): String? {
        if (!DocumentsContract.isDocumentUri(context, uri)) {
            return null
        }
        return if (DOCUMENTS_AUTHORITY == uri.authority) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":").toTypedArray()
            val type = split[0]
            if ("primary".equals(type, ignoreCase = true)) {
                Environment.getExternalStorageDirectory().toString() + "/" + split[1]
            } else {
                null
            }
        } else if (DOWNLOADS_AUTHORITY == uri.authority) {
            val id = DocumentsContract.getDocumentId(uri)
            val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
            )
            getPathFromGeneralUri(context, contentUri)
        } else if (MEDIA_AUTHORITY == uri.authority) {
            val id = DocumentsContract.getDocumentId(uri).split(":").toTypedArray()[1]
            getPathFromMediaStore(context, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
        } else {
            null
        }
    }

    private fun getPathFromMediaStore(context: Context, uri: Uri, id: String): String? {
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        val selection = MediaStore.MediaColumns._ID + " = ?"
        val selectionArgs = arrayOf(id)
        val cur = context.contentResolver.query(
                uri, projection, selection,
                selectionArgs, null
        ) ?: return null
        var path: String? = null
        if (cur.moveToFirst()) {
            path = cur.getString(cur.getColumnIndex(MediaStore.MediaColumns.DATA))
        }
        cur.close()
        return path
    }

    private fun getPathFromGeneralUri(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        return cursor.use {
            if (it != null && it.moveToFirst()) {
                it.getString(it.getColumnIndex(MediaStore.Audio.Media.DATA))
            } else {
                null
            }
        }
    }

}