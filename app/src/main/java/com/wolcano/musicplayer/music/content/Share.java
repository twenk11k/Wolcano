package com.wolcano.musicplayer.music.content;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.widget.Toast;

import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.TaskQuery;

import java.io.File;

/**
 * The class for share audio files
 * 2016
 */
public class Share {

    private static final int TYPE_ARTIST = 0;
    private static final int TYPE_ALBUM = 1;
    public static final int TYPE_SONG = 2;

    private static final String DEFAULT_SORT = "artist_key,album_key,track";
    private static final String ALBUM_SORT = "album_key,track";

    public static void shareSong(Context ctx, int type, long id) {

        ContentResolver resolver = ctx.getContentResolver();
        String[] projection = new String[]{MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DATA};
        Cursor cursor = buildQuery(type, id, projection, null).runQuery(resolver);
        if (cursor == null) {
            return;
        }

        try {
            while (cursor.moveToNext()) {
                File songFile = new File(cursor.getString(1));
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("audio/*");
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(songFile));
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                ctx.startActivity(Intent.createChooser(share, ctx.getString(R.string.sharefile)));
            }
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(ctx, "Error", Toast.LENGTH_SHORT).show();
        } finally {
            cursor.close();
        }


    }

    private static TaskQuery buildQuery(int type, long id, String[] projection, String selection) {
        switch (type) {
            case TYPE_ARTIST:
            case TYPE_ALBUM:
            case TYPE_SONG:
                return buildMediaQuery(type, id, projection, selection);
            default:
                throw new IllegalArgumentException("Specified type not valid: " + type);
        }
    }
    private static TaskQuery buildMediaQuery(int type, long id, String[] projection, String select) {
        Uri media = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        StringBuilder selection = new StringBuilder();
        String sort = DEFAULT_SORT;

        switch (type) {
            case TYPE_SONG:
                selection.append(MediaStore.Audio.Media._ID);
                break;
            case TYPE_ARTIST:
                selection.append(MediaStore.Audio.Media.ARTIST_ID);
                break;
            case TYPE_ALBUM:
                selection.append(MediaStore.Audio.Media.ALBUM_ID);
                sort = ALBUM_SORT;
                break;
            default:
                throw new IllegalArgumentException("Invalid type specified: " + type);
        }

        selection.append('=');
        selection.append(id);
        selection.append(" AND length(_data) AND " + MediaStore.Audio.Media.IS_MUSIC);

        if (select != null) {
            selection.append(" AND ");
            selection.append(select);
        }

        TaskQuery result = new TaskQuery(media, projection, selection.toString(), null, sort);
        result.type = type;
        return result;
    }

}
