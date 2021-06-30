package com.wolcano.musicplayer.music.content

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri

class TaskQuery(
    private val uri: Uri,
    private val projection: Array<String>,
    private val selection: String,
    private val selectionArgs: Array<String>?,
    private val sort_order: String
) {
    fun runQuery(resolver: ContentResolver): Cursor? {
        return queryResolver(resolver, uri, projection, selection, selectionArgs, sort_order)
    }

    companion object {
        private fun queryResolver(
            resolver: ContentResolver,
            uri: Uri,
            projection: Array<String>,
            selection: String,
            selectionArgs: Array<String>?,
            sortOrder: String
        ): Cursor? {
            var cursor: Cursor? = null
            try {
                cursor = resolver.query(uri, projection, selection, selectionArgs, sortOrder)
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
            return cursor
        }
    }

}