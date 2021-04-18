package com.wolcano.musicplayer.music.data.persistence

import androidx.room.*
import com.wolcano.musicplayer.music.data.model.SearchHistory

@Dao
interface SearchHistoryDao {

    @Query("SELECT * FROM searchHistory")
    fun getLastSearches(): List<SearchHistory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(searchHistory: SearchHistory)

    @Delete
    suspend fun delete(searchHistory: SearchHistory)

}