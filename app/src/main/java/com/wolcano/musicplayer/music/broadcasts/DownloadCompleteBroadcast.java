package com.wolcano.musicplayer.music.broadcasts;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.wolcano.musicplayer.music.utils.Utils;

public class DownloadCompleteBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        DownloadManager.Query query = new DownloadManager.Query();
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Cursor cursor = downloadManager.query(query);

        if (cursor.moveToFirst()) {

            int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {
                Utils.setCountDownload(context, Utils.getCountDownload(context)+1);
            }
        }
        cursor.close();
    }
}
