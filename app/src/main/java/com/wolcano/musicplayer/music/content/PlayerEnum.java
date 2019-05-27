package com.wolcano.musicplayer.music.content;

public enum PlayerEnum {


    NORMAL(0),
    SHUFFLE(1),
    REPEAT(2);
    private int value;
    public static PlayerEnum valueOf(int value) {
        switch (value) {
            case 1:
                return SHUFFLE;
            case 2:
                return REPEAT;
            case 0:
            default:
                return NORMAL;
        }
    }
    public int getVal() {
        return value;
    }

    PlayerEnum(int value) {
        this.value = value;
    }


}
