package com.wolcano.musicplayer.music.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "searchHistory")
class SearchHistory(
    @PrimaryKey @ColumnInfo(name = "search_text") var searchText: String
)