package com.wolcano.musicplayer.music.persistence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.wolcano.musicplayer.music.model.Song

@Dao
interface SongDao {

    @Query("SELECT * FROM song")
    fun getAll(): List<Song>

    @Insert
    fun insert(song: Song)

    @Query("DELETE FROM song")
    fun deleteAll()

    @Delete
    fun delete(song: Song)

}