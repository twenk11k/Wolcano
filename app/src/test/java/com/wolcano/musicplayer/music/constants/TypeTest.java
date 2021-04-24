package com.wolcano.musicplayer.music.constants;

import org.junit.Test;

import static com.wolcano.musicplayer.music.data.model.Type.TYPE_FOOTER;
import static com.wolcano.musicplayer.music.data.model.Type.TYPE_SONG;
import static org.junit.Assert.assertEquals;

public class TypeTest {

    @Test
    public void isTypeSongCorrect(){
        assertEquals(1,TYPE_SONG);
    }
    @Test
    public void isTypeFooterCorrect(){
        assertEquals(2,TYPE_FOOTER);
    }

}