package com.wolcano.musicplayer.music.constants;

import android.provider.MediaStore;

import org.junit.Test;

import static com.wolcano.musicplayer.music.constants.Constants.ACTION_PAUSE;
import static com.wolcano.musicplayer.music.constants.Constants.ACTION_QUIT;
import static com.wolcano.musicplayer.music.constants.Constants.ACTION_REWIND;
import static com.wolcano.musicplayer.music.constants.Constants.ACTION_SKIP;
import static com.wolcano.musicplayer.music.constants.Constants.ACTION_STOP;
import static com.wolcano.musicplayer.music.constants.Constants.ACTION_STOP_SLEEP;
import static com.wolcano.musicplayer.music.constants.Constants.ACTION_TOGGLE_PAUSE;
import static com.wolcano.musicplayer.music.constants.Constants.ALBUM_ID;
import static com.wolcano.musicplayer.music.constants.Constants.ALBUM_NAME;
import static com.wolcano.musicplayer.music.constants.Constants.ERROR_TAG;
import static com.wolcano.musicplayer.music.constants.Constants.GENRE_ID;
import static com.wolcano.musicplayer.music.constants.Constants.GENRE_NAME;
import static com.wolcano.musicplayer.music.constants.Constants.MAIN_BASE_URL;
import static com.wolcano.musicplayer.music.constants.Constants.MAIN_BASE_URL_2;
import static com.wolcano.musicplayer.music.constants.Constants.PACKAGE_NAME;
import static com.wolcano.musicplayer.music.constants.Constants.PLAYLIST_ID;
import static com.wolcano.musicplayer.music.constants.Constants.PLAYLIST_NAME;
import static com.wolcano.musicplayer.music.constants.Constants.SONG_LIBRARY;
import static com.wolcano.musicplayer.music.constants.Constants.SONG_ONLY_SELECTION;
import static org.junit.Assert.*;

public class ConstantsTest {

    @Test
    public void isAlbumIdCorrect(){
        assertEquals("album_id",ALBUM_ID);
    }

    @Test
    public void isAlbumNameCorrect(){
        assertEquals("album_name",ALBUM_NAME);
    }

    @Test
    public void isGenreIdCorrect(){
        assertEquals("genre_id",GENRE_ID);
    }

    @Test
    public void isGenreNameCorrect(){
        assertEquals("genre_name",GENRE_NAME);
    }

    @Test
    public void isPlaylistIdCorrect(){
        assertEquals("playlist_id",PLAYLIST_ID);
    }

    @Test
    public void isPlaylistNameCorrect(){
        assertEquals("playlist_name",PLAYLIST_NAME);
    }

    @Test
    public void isMainBaseUrlCorrect(){
        assertEquals("https://mp3-pn.com/search/s/f/",MAIN_BASE_URL);
    }

    @Test
    public void isSongOnlySelectionCorrect(){
        assertEquals(MediaStore.Audio.AudioColumns.IS_MUSIC + "=1"
                + " AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''",SONG_ONLY_SELECTION);
    }

    @Test
    public void isSongLibraryCorrect(){
        assertEquals("song_library",SONG_LIBRARY);
    }
    @Test
    public void isPackageNameCorrect(){
        assertEquals("com.wolcano.musicplayer.music",PACKAGE_NAME);
    }
    @Test
    public void isActionRewindCorrect(){
        assertEquals(PACKAGE_NAME + ".rewind",ACTION_REWIND);
    }

    @Test
    public void isMainBaseUrl2Correct(){
        assertEquals("page/",MAIN_BASE_URL_2);
    }

    @Test
    public void isActionSkipCorrect(){
        assertEquals(PACKAGE_NAME + ".skip",ACTION_SKIP);
    }

    @Test
    public void isActionTogglePauseCorrect(){
        assertEquals(PACKAGE_NAME + ".togglepause",ACTION_TOGGLE_PAUSE);
    }

    @Test
    public void isActionStopCorrect(){
        assertEquals(PACKAGE_NAME + ".stop",ACTION_STOP);
    }

    @Test
    public void isActionQuitCorrect(){
        assertEquals(PACKAGE_NAME + ".quitservice",ACTION_QUIT);
    }

    @Test
    public void isActionPauseCorrect(){
        assertEquals(PACKAGE_NAME + ".pause",ACTION_PAUSE);
    }

    @Test
    public void isActionStopSleepCorrect(){
        assertEquals(PACKAGE_NAME + ".stopsleep",ACTION_STOP_SLEEP);
    }

    @Test
    public void isErrorTagCorrect(){
        assertEquals("error_tag",ERROR_TAG);
    }



}