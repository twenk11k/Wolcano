package com.wolcano.musicplayer.music.constants;

import android.provider.MediaStore;

import javax.inject.Singleton;


@Singleton
public class Constants {

    public static final String ALBUM_ID = "album_id";
    public static final String ALBUM_NAME = "album_name";
    public static final String ARTIST_ID = "artist_id";
    public static final String ARTIST_NAME = "artist_name";
    public static final String GENRE_ID = "genre_id";
    public static final String GENRE_NAME = "genre_name";
    public static final String PLAYLIST_ID = "playlist_id";
    public static final String PLAYLIST_NAME = "playlist_name";
    public static final String MAIN_BASE_URL = "https://wax.click/search/s/f/";
    public static final String SONG_ONLY_SELECTION = MediaStore.Audio.AudioColumns.IS_MUSIC + "=1"
            + " AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''";
    public static final String SONG_LIBRARY= "song_library";
    public static final String PACKAGE_NAME = "com.wolcano.musicplayer.music";
    public static final String ACTION_REWIND = PACKAGE_NAME + ".rewind";
    public static final String MAIN_BASE_URL_2 = "page/";
    public static final String ACTION_SKIP = PACKAGE_NAME + ".skip";
    public static final String ACTION_TOGGLE_PAUSE = PACKAGE_NAME + ".togglepause";
    public static final String ACTION_STOP = PACKAGE_NAME + ".stop";
    public static final String ACTION_QUIT = PACKAGE_NAME + ".quitservice";
    public static final String ACTION_PAUSE = PACKAGE_NAME + ".pause";
    public static final String ACTION_STOP_SLEEP = PACKAGE_NAME + ".stopsleep";
    public static final String ERROR_TAG = "error_tag";

}
