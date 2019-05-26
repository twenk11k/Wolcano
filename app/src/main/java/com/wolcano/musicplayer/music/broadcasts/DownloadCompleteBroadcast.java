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
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Cursor cursor = manager.query(query);

        if (cursor.moveToFirst()) {

            int stat = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            if (stat == DownloadManager.STATUS_SUCCESSFUL) {
                Utils.setCountSave(context, Utils.getCountSave(context)+1);
            }
        }
        cursor.close();
    }
}
