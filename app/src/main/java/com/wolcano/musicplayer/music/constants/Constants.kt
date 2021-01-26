package com.wolcano.musicplayer.music.constants

import android.provider.MediaStore

object Constants {
    const val ALBUM_ID = "album_id"
    const val ALBUM_NAME = "album_name"
    const val ARTIST_ID = "artist_id"
    const val ARTIST_NAME = "artist_name"
    const val GENRE_ID = "genre_id"
    const val GENRE_NAME = "genre_name"
    const val PLAYLIST_ID = "playlist_id"
    const val PLAYLIST_NAME = "playlist_name"
    const val MAIN_BASE_URL = "https://wax.click/search/s/f/"
    const val SONG_ONLY_SELECTION = (MediaStore.Audio.AudioColumns.IS_MUSIC + "=1"
            + " AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''")
    const val SONG_LIBRARY = "song_library"
    const val PACKAGE_NAME = "com.wolcano.musicplayer.music"
    const val ACTION_REWIND = "$PACKAGE_NAME.rewind"
    const val MAIN_BASE_URL_2 = "page/"
    const val ACTION_SKIP = "$PACKAGE_NAME.skip"
    const val ACTION_TOGGLE_PAUSE = "$PACKAGE_NAME.togglepause"
    const val ACTION_STOP = "$PACKAGE_NAME.stop"
    const val ACTION_QUIT = "$PACKAGE_NAME.quitservice"
    const val ACTION_PAUSE = "$PACKAGE_NAME.pause"
    const val ACTION_STOP_SLEEP = "$PACKAGE_NAME.stopsleep"
    const val ERROR_TAG = "error_tag"
}
