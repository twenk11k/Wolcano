package com.wolcano.musicplayer.music.broadcasts

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wolcano.musicplayer.music.utils.Utils

class DownloadCompleteBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val query = DownloadManager.Query()
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val cursor = downloadManager.query(query)
        if (cursor.moveToFirst()) {
            val downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {
                Utils.setCountDownload(context, Utils.getCountDownload(context) + 1)
            }
        }
        cursor.close()
    }

}