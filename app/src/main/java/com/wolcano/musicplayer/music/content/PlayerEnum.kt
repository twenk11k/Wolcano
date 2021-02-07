package com.wolcano.musicplayer.music.content

enum class PlayerEnum(val value: Int) {
    NORMAL(0),
    SHUFFLE(1),
    REPEAT(2);

    companion object {
        fun valueOf(value: Int) = values().find { it.value == value }
    }
}