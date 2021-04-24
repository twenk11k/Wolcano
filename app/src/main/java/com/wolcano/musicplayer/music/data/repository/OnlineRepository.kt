package com.wolcano.musicplayer.music.data.repository

import androidx.annotation.WorkerThread
import com.skydoves.whatif.whatIfNotNull
import com.wolcano.musicplayer.music.data.model.SearchHistory
import com.wolcano.musicplayer.music.data.model.SongOnline
import com.wolcano.musicplayer.music.data.persistence.SearchHistoryDao
import com.wolcano.musicplayer.music.utils.Constants
import com.wolcano.musicplayer.music.utils.Constants.ARTIST_QUERY
import com.wolcano.musicplayer.music.utils.Constants.BASE_QUERY
import com.wolcano.musicplayer.music.utils.Constants.DURATION_QUERY
import com.wolcano.musicplayer.music.utils.Constants.SINGLE_QUERY
import com.wolcano.musicplayer.music.utils.Constants.TITLE_QUERY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.*
import javax.inject.Inject

class OnlineRepository @Inject constructor(private val searchHistoryDao: SearchHistoryDao) {

    @WorkerThread
    fun searchQuery(
        searchText: String?,
        page: Int,
        isAutoSearch: Boolean,
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit
    ) = flow {
        if (searchText != null && searchText.isNotEmpty()) {
            if (!isAutoSearch) {
                searchHistoryDao.insert(SearchHistory(searchText))
            }
            val list = ArrayList<SongOnline>()
            val doc: Document? = if (page == 1) {
                Jsoup.connect("${Constants.MAIN_BASE_URL}$searchText/").timeout(10000)
                    .ignoreHttpErrors(true).get()
            } else {
                Jsoup.connect(
                    "${Constants.MAIN_BASE_URL}$searchText/${Constants.MAIN_BASE_URL_2}$page/"
                ).timeout(10000).ignoreHttpErrors(true).get()
            }
            if (doc != null) {
                val baseElements = doc.select(BASE_QUERY)
                val artistElements = doc.select(ARTIST_QUERY)
                val titleElements = doc.select(TITLE_QUERY)
                val durationElements = doc.select(DURATION_QUERY)
                for (j in baseElements.indices) {
                    val title = titleElements[j].text()
                    val artist = artistElements[j].text()
                    val duration = durationElements[j].text()
                    val baseUrl = baseElements[j].attr(SINGLE_QUERY)
                    val songOnline = SongOnline(baseUrl, artist, title, convertDuration(duration))
                    list.add(songOnline)
                }
            }
            if (list.isEmpty() && page == 1) {
                onError("Can't find any results for $searchText")
            }
            emit(list)
        }
    }.onStart { onStart() }.onCompletion { onComplete() }.flowOn(Dispatchers.IO)

    private fun convertDuration(durationStr: String): Long {
        val duration = java.lang.StringBuilder(durationStr)
        var i = 0
        while (i < duration.length - 1) {
            val current = duration[i]
            if (i == 0 && current == '0') {
                duration.deleteCharAt(i)
            }
            if (current == ':') {
                duration.deleteCharAt(i)
            } else {
                i++
            }
        }
        val durationAsInt = duration.toString().toInt()
        val strStage1 = durationAsInt / 100
        val strStage2 = durationAsInt % 100
        val finalStage = strStage1 * 60 + strStage2
        return finalStage.toLong()
    }

    @WorkerThread
    fun getLastSearches() = flow {
        val lastSearch = searchHistoryDao.getLastSearches()
        lastSearch.apply {
            this.whatIfNotNull {
                emit(it)
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun removeSearchHistory(searchText: String) {
        searchHistoryDao.delete(SearchHistory(searchText))
    }

}