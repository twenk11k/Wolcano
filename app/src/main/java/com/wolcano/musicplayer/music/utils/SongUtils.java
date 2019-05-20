package com.wolcano.musicplayer.music.utils;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import android.text.Html;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.models.Album;
import com.wolcano.musicplayer.music.mvp.models.Artist;
import com.wolcano.musicplayer.music.mvp.models.Genre;
import com.wolcano.musicplayer.music.mvp.models.SongOnline;
import com.wolcano.musicplayer.music.mvp.models.Playlist;
import com.wolcano.musicplayer.music.mvp.models.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.provider.MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
import static com.wolcano.musicplayer.music.Constants.SONG_ONLY_SELECTION;


public class SongUtils {
    private static final String SELECTION = MediaStore.Audio.Media.IS_MUSIC + " != 0";
    private static ContentValues[] mContentValuesCache = null;

    @NonNull
    public static List<Song> scanSongs(@NonNull Context context, String sort) {
        List<Song> alist = new ArrayList<>();

        if (context != null) {
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
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
                    },
                    SELECTION,
                    null, sort);
            if (cursor == null) {
                return alist;
            }

            int i = 0;
            while (cursor.moveToNext()) {
                int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC));
                if (isMusic == 0) {
                    continue;
                }

                long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
                String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
                String album = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)));
                long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
                String fileName = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME)));
                long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));

                Song song = new Song();
                song.setSongId(id);
                song.setTip(Song.Tip.MODEL0);
                song.setTitle(title);
                song.setArtist(artist);
                song.setAlbum(album);
                song.setAlbumId(albumId);
                song.setDura(duration);
                song.setPath(path);
                song.setDosName(fileName);
                song.setDosSize(fileSize);
                alist.add(song);
            }
            cursor.close();
            return alist;

        }
        return alist;
    }

    @NonNull
    public static List<Song> scanSongsCursor(@NonNull Context context, Cursor cursor) {
        List<Song> alist = new ArrayList<>();

        if (context != null) {

            if (cursor == null) {
                return alist;
            }

            int i = 0;
            while (cursor.moveToNext()) {
                int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC));
                if (isMusic == 0) {
                    continue;
                }

                long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
                String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
                String album = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)));
                long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
                String fileName = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME)));
                long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));

                Song song = new Song();
                song.setSongId(id);
                song.setTip(Song.Tip.MODEL0);
                song.setTitle(title);
                song.setArtist(artist);
                song.setAlbum(album);
                song.setAlbumId(albumId);
                song.setDura(duration);
                song.setPath(path);
                song.setDosName(fileName);
                song.setDosSize(fileSize);
                alist.add(song);
            }
            cursor.close();
            return alist;

        }
        return alist;
    }

    @NonNull
    public static List<Song> scanSongsforAlbum(Context context, String sortOrder, long albumid) {
        List<Song> alist = new ArrayList<>();

        if (context != null) {

            String selection = "is_music=1 AND title != '' AND album_id=" + albumid;
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
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
                    },
                    selection,
                    null, sortOrder);
            if (cursor == null) {
                return alist;
            }

            int i = 0;
            while (cursor.moveToNext()) {
                int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC));
                if (isMusic == 0) {
                    continue;
                }

                long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
                String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
                String album = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)));
                long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
                String fileName = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME)));
                long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));

                Song song = new Song();
                song.setSongId(id);
                song.setTip(Song.Tip.MODEL0);
                song.setTitle(title);
                song.setArtist(artist);
                song.setAlbum(album);
                song.setAlbumId(albumId);
                song.setDura(duration);
                song.setPath(path);
                song.setDosName(fileName);
                song.setDosSize(fileSize);
                alist.add(song);
            }
            cursor.close();

            return alist;
        }
        return alist;
    }

    @NonNull
    public static List<Song> scanSongsforArtist(Context context, String sort, long artistID) {
        List<Song> alist = new ArrayList<>();
        if (context != null) {

            String selection = "is_music=1 AND title != '' AND artist_id=" + artistID;

            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    new String[]{
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
                    },
                    selection,
                    null, sort);
            if (cursor == null) {
                return alist;
            }

            while (cursor.moveToNext()) {
                int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC));
                if (isMusic == 0) {
                    continue;
                }

                long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
                String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
                String album = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)));
                long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
                String fileName = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME)));
                long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));

                Song song = new Song();
                song.setSongId(id);
                song.setTip(Song.Tip.MODEL0);
                song.setTitle(title);
                song.setArtist(artist);
                song.setAlbum(album);
                song.setAlbumId(albumId);
                song.setDura(duration);
                song.setPath(path);
                song.setDosName(fileName);
                song.setDosSize(fileSize);
                alist.add(song);
            }
            cursor.close();

            return alist;
        }
        return alist;
    }

    @NonNull
    public static List<Song> scanSongsforGenre(Context context, String sort, long genreID) {
        List<Song> alist = new ArrayList<>();
        if (context != null) {
            String selection = "is_music=1 AND title != '' AND genre_id=" + genreID;

            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Genres.Members.getContentUri("external", genreID),
                    new String[]{
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
                    },
                    selection,
                    null, sort);
            if (cursor == null) {
                return alist;
            }

            int i = 0;
            while (cursor.moveToNext()) {
                int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC));
                if (isMusic == 0) {
                    continue;
                }
                scanSongs(context, sort);
                if (scanSongs(context, sort).size() > 0) {
                    long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
                    String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
                    String album = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)));
                    long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
                    long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
                    String fileName = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME)));
                    long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));

                    Song song = new Song();
                    song.setSongId(id);
                    song.setTip(Song.Tip.MODEL0);
                    song.setTitle(title);
                    song.setArtist(artist);
                    song.setAlbum(album);
                    song.setAlbumId(albumId);
                    song.setDura(duration);
                    song.setPath(path);
                    song.setDosName(fileName);
                    song.setDosSize(fileSize);
                    alist.add(song);
                }

            }
            cursor.close();

            return alist;
        }
        return alist;
    }

    @NonNull
    public static List<Song> scanSongsforPlaylist(Context context, String sort, long playlistID) {
        List<Song> alist = new ArrayList<>();
        if (context != null) {
            final StringBuilder mSelection = new StringBuilder();
            mSelection.append(MediaStore.Audio.AudioColumns.IS_MUSIC + "=1");
            mSelection.append(" AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''");

            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Playlists.Members.getContentUri("external", playlistID),
                    new String[]{
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
                            MediaStore.Audio.Playlists.Members.PLAY_ORDER,
                    },
                    mSelection.toString(),
                    null, sort);
            if (cursor == null) {
                return alist;
            }

            int i = 0;
            while (cursor.moveToNext()) {
                int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC));
                if (isMusic == 0) {
                    continue;
                }
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID));
                String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
                String album = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)));
                long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
                String fileName = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME)));
                long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));

                Song song = new Song();
                song.setSongId(id);
                song.setTip(Song.Tip.MODEL0);
                song.setTitle(title);
                song.setArtist(artist);
                song.setAlbum(album);
                song.setAlbumId(albumId);
                song.setDura(duration);
                song.setPath(path);
                song.setDosName(fileName);
                song.setDosSize(fileSize);
                alist.add(song);

            }
            cursor.close();

            return alist;
        }
        return alist;

    }

    @NonNull
    public static List<Playlist> scanPlaylist(Context context) {
        List<Playlist> playlistList = new ArrayList<>();
        if (context != null) {
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                    new String[]{
                            BaseColumns._ID,
                            MediaStore.Audio.PlaylistsColumns.NAME
                    },
                    null,
                    null, MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER);
            if (cursor == null) {
                return playlistList;
            }

            int i = 0;
            while (cursor.moveToNext()) {
                final long id = cursor.getLong(0);

                final String name = cursor.getString(1);

                final int songCount = getSongCountForPlaylist(context, id);

                final Playlist playlist = new Playlist(id, name, songCount);

                playlistList.add(playlist);
            }
            cursor.close();

            return playlistList;
        }
        return playlistList;

    }

    private static int getSongCountForPlaylist(final Context context, final long playlistId) {
        if (context != null) {
            Cursor c = context.getContentResolver().query(
                    MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId),
                    new String[]{BaseColumns._ID}, SONG_ONLY_SELECTION, null, null);

            if (c != null) {
                int count = 0;
                if (c.moveToFirst()) {
                    count = c.getCount();
                }
                c.close();
                c = null;
                return count;
            }

            return 0;
        }
        return 0;
    }

    public static void addToPlaylist(final Context context, final long id, final long playlistid, String musicTitle) {
        if (context != null) {
            final int size = 1;
            final ContentResolver resolver = context.getContentResolver();
            final String[] projection = new String[]{
                    "max(" + "play_order" + ")",
            };
            final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistid);
            Cursor cursor = null;
            int base = 0;

            try {
                cursor = resolver.query(uri, projection, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    base = cursor.getInt(0) + 1;
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }

            int numinserted = 0;
            for (int offSet = 0; offSet < size; offSet += 1000) {
                makeInsertItems(id, offSet, 1000, base);
                numinserted += resolver.bulkInsert(uri, mContentValuesCache);
            }
            final CharSequence message = Html.fromHtml(context.getString(R.string.number_song_add_playlist, musicTitle, getNameFromPlaylist(context, playlistid)));
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();

        }
    }

    public static void makeInsertItems(final long id, final int offset, int len, final int base) {
        if (offset + len > 1) {
            len = 1 - offset;
        }

        if (mContentValuesCache == null || mContentValuesCache.length != len) {
            mContentValuesCache = new ContentValues[len];
        }
        for (int i = 0; i < len; i++) {
            if (mContentValuesCache[i] == null) {
                mContentValuesCache[i] = new ContentValues();
            }
            mContentValuesCache[i].put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, base + offset + i);
            mContentValuesCache[i].put(MediaStore.Audio.Playlists.Members.AUDIO_ID, id);
        }
    }

    public static long createPlaylist(final Context context, final String name) {
        if (context != null) {
            if (name != null && name.length() > 0) {
                final ContentResolver resolver = context.getContentResolver();
                final String[] projection = new String[]{
                        MediaStore.Audio.PlaylistsColumns.NAME
                };
                final String selection = MediaStore.Audio.PlaylistsColumns.NAME + " = '" + name + "'";
                Cursor cursor = resolver.query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                        projection, selection, null, null);
                if (cursor.getCount() <= 0) {
                    final ContentValues values = new ContentValues(1);
                    values.put(MediaStore.Audio.PlaylistsColumns.NAME, name);
                    final Uri uri = resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                            values);
                    return Long.parseLong(uri.getLastPathSegment());
                }
                cursor.close();
                cursor = null;
                return -1;
            }
            return -1;
        }
        return -1;

    }

    public static String getNameFromPlaylist(@NonNull final Context context, final long id) {
        if (context != null) {
            try {
                Cursor cursor = context.getContentResolver().query(EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Audio.PlaylistsColumns.NAME},
                        BaseColumns._ID + "=?",
                        new String[]{String.valueOf(id)},
                        null);
                if (cursor != null) {
                    try {
                        if (cursor.moveToFirst()) {
                            return cursor.getString(0);
                        }
                    } finally {
                        cursor.close();
                    }
                }
            } catch (SecurityException ignored) {
            }
            return "";
        }
        return "";
    }

    public static void downPerform(Context context, Song song) {
        if (context != null) {
            Toast.makeText(context.getApplicationContext(), R.string.itsstart, Toast.LENGTH_SHORT).show();
            String path = song.getPath();
            File file = new File(Environment.getExternalStorageDirectory()
                    + "/" + context.getString(R.string.folder_name));
            if (!file.exists()) {
                file.mkdirs();
            }
            DownloadManager loadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request loadRequest = new DownloadManager.Request(Uri.parse(path));
            loadRequest.setTitle(song.getTitle());
            loadRequest.setDescription(context.getString(R.string.artist_notification) + song.getArtist());
            loadRequest.allowScanningByMediaScanner();
            loadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            String fileStr = URLUtil.guessFileName(path, null, MimeTypeMap.getFileExtensionFromUrl(path));
            String fileStr2 = "";
            if (fileStr.contains(context.getResources().getString(R.string.base_path_prefix))) {
                fileStr2 = fileStr.replace(context.getResources().getString(R.string.base_path_prefix), "");
            } else {
                fileStr2 = fileStr;
            }
            try {
                loadRequest.setDestinationInExternalPublicDir("/" + context.getString(R.string.folder_name), fileStr2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(loadManager!=null){
                loadManager.enqueue(loadRequest);
            }
        }
    }

    public static void downPerform(Context context, SongOnline songOnline) {
        Toast.makeText(context.getApplicationContext(), R.string.itsstart, Toast.LENGTH_SHORT).show();
        String path = songOnline.getPath();
        File file = new File(Environment.getExternalStorageDirectory()
                + "/" + context.getString(R.string.folder_name));
        if (!file.exists()) {
            file.mkdirs();
        }
        DownloadManager loadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request loadRequest = new DownloadManager.Request(Uri.parse(path));
        loadRequest.setTitle(songOnline.getTitle());
        loadRequest.setDescription(context.getString(R.string.artist_notification) + songOnline.getArtistName());
        loadRequest.allowScanningByMediaScanner();
        loadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        String fileStr = URLUtil.guessFileName(path, null, MimeTypeMap.getFileExtensionFromUrl(path));
        String fileStr2 = "";
        if (fileStr.contains(context.getResources().getString(R.string.base_path_prefix))) {
            fileStr2 = fileStr.replace(context.getResources().getString(R.string.base_path_prefix), "");
        } else {
            fileStr2 = fileStr;
        }
        try {
            loadRequest.setDestinationInExternalPublicDir("/" + context.getString(R.string.folder_name), fileStr2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(loadManager!=null){
            loadManager.enqueue(loadRequest);
        }
}

    public static void renamePlaylist(final Context context, long playlistid, String newName) {
        if (context != null) {
            final ContentResolver resolver = context.getContentResolver();
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Audio.Playlists.NAME, newName);
            resolver.update(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                    values, "_id=" + playlistid, null);
        }
    }

    public static void deletePlaylists(Context context, long playlistId) {
        if (context != null) {
            Uri localUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
            StringBuilder localStringBuilder = new StringBuilder();
            localStringBuilder.append("_id IN (");
            localStringBuilder.append((playlistId));
            localStringBuilder.append(")");
            context.getContentResolver().delete(localUri, localStringBuilder.toString(), null);
        }
    }

    public static void removeFromPlaylist(final Context context, final long[] ids,
                                          final long playlistId) {
        if (context != null) {
            final Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
            final ContentResolver resolver = context.getContentResolver();

            final StringBuilder selection = new StringBuilder();
            selection.append(MediaStore.Audio.Playlists.Members.AUDIO_ID + " IN (");
            for (int i = 0; i < ids.length; i++) {
                selection.append(ids[i]);
                if (i < ids.length - 1) {
                    selection.append(",");
                }
            }
            selection.append(")");

            resolver.delete(uri, selection.toString(), null);
        }
    }

    @NonNull
    public static List<Genre> scanGenre(Context context) {
        List<Genre> genreList = new ArrayList<>();
        if (context != null) {
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
                    new String[]{
                            MediaStore.Audio.Genres._ID,
                            MediaStore.Audio.Genres.NAME
                    },
                    null,
                    null, MediaStore.Audio.Genres.DEFAULT_SORT_ORDER);
            if (cursor == null) {
                return genreList;
            }

            int i = 0;
            while (cursor.moveToNext()) {
                Genre genre = getGenreFromCursor(context, cursor);
                if (genre.songCount > 0) {
                    genreList.add(genre);
                } else {
                    try {
                        context.getContentResolver().delete(MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI, MediaStore.Audio.Genres._ID + " == " + genre.id, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            cursor.close();

            return genreList;
        }
        return genreList;
    }

    @NonNull
    public static List<Artist> scanArtists(Context context) {
        List<Artist> artists = new ArrayList<>();
        if (context != null) {
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.Artists._ID,
                            MediaStore.Audio.Artists.ARTIST,
                            MediaStore.Audio.Artists.NUMBER_OF_TRACKS},
                    null,
                    null, MediaStore.Audio.Artists.ARTIST_KEY);
            if (cursor == null) {
                return artists;
            }

            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Artists._ID));
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST));
                int songCount = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS));
                Artist artist = new Artist(id, name, songCount);
                artists.add(artist);
            }
            cursor.close();

            return artists;
        }
        return artists;
    }

    @NonNull
    public static List<Album> scanAlbums(Context context) {
        List<Album> albums = new ArrayList<>();
        if (context != null) {
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.Albums._ID,
                            MediaStore.Audio.Albums.ALBUM,
                            MediaStore.Audio.Albums.ARTIST},
                    null,
                    null, MediaStore.Audio.Albums.ALBUM_KEY);
            if (cursor == null) {
                return albums;
            }

            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Albums._ID));
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
                Album album = new Album(id, name, artist);
                albums.add(album);
            }
            cursor.close();

            return albums;
        }
        return albums;
    }

    @NonNull
    private static Genre getGenreFromCursor(@NonNull final Context context, @NonNull final Cursor cursor) {
        if (context != null) {
            final int id = cursor.getInt(0);
            final String name = cursor.getString(1);
            final int songs = scanSongsForGenre(context, id).size();
            return new Genre(id, name, songs);
        }
        return new Genre(-1, "", 0);

    }

    @NonNull
    public static List<Song> scanSongsForGenre(@NonNull final Context context, int genreID) {
        List<Song> alist = new ArrayList<>();
        if (context != null) {
            Cursor cursor = context.getContentResolver().query(
                    MediaStore.Audio.Genres.Members.getContentUri("external", genreID),
                    new String[]{
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
                    },
                    SELECTION,
                    null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            if (cursor == null) {
                return alist;
            }

            int i = 0;
            while (cursor.moveToNext()) {
                int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC));
                if (isMusic == 0) {
                    continue;
                }

                long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
                String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
                String album = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)));
                long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
                String fileName = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME)));
                long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));

                Song song = new Song();
                song.setSongId(id);
                song.setTip(Song.Tip.MODEL0);
                song.setTitle(title);
                song.setArtist(artist);
                song.setAlbum(album);
                song.setAlbumId(albumId);
                song.setDura(duration);
                song.setPath(path);
                song.setDosName(fileName);
                song.setDosSize(fileSize);
                alist.add(song);
            }
            cursor.close();

            return alist;
        }
        return alist;

    }

    public static List<Song> buildSongListFromFile(Context context, File file) {
        Cursor cur = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Audio.Media.DATA + " like ?",
                new String[]{"%" + file.getParent() + "/%"},
                MediaStore.Audio.Media.DATA + " ASC");
        if (cur == null) {
            return null;
        }
        List<Song> songs = scanSongsCursor(context, cur);
        cur.close();
        return songs;
    }
}
