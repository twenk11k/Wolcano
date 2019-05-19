package com.wolcano.musicplayer.music.content;

public enum LeftButtonEnumVals {


    DUZ(0),
    KARISIK(1),
    TEK(2);
    private int val;
    public static LeftButtonEnumVals valueOf(int value) {
        switch (value) {
            case 1:
                return KARISIK;
            case 2:
                return TEK;
            case 0:
            default:
                return DUZ;
        }
    }
    public int getVal() {
        return val;
    }

    LeftButtonEnumVals(int val) {
        this.val = val;
    }


}
