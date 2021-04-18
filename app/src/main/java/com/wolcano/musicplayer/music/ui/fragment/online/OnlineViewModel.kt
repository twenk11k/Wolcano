package com.wolcano.musicplayer.music.ui.fragment.online

import androidx.annotation.MainThread
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.wolcano.musicplayer.music.base.LiveCoroutinesViewModel
import com.wolcano.musicplayer.music.model.SearchHistory
import com.wolcano.musicplayer.music.model.SongOnline
import com.wolcano.musicplayer.music.repository.OnlineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnlineViewModel @Inject constructor(private val onlineRepository: OnlineRepository) :
    LiveCoroutinesViewModel() {

    private val searchOnlinePage: MutableStateFlow<Int> = MutableStateFlow(1)
    val searchOnlineListLiveData: LiveData<List<SongOnline>>

    val lastSearches: LiveData<List<SearchHistory>>

    var toastMessage: ObservableField<String> = ObservableField()

    var query: ObservableField<String> = ObservableField()

    var isAutoSearch: ObservableBoolean = ObservableBoolean(false)

    var isLoading = ObservableBoolean(false)

    init {
        searchOnlineListLiveData = searchOnlinePage.asLiveData().switchMap { page ->
            onlineRepository.searchQuery(
                searchText = query.get(),
                page = page,
                isAutoSearch = isAutoSearch.get(),
                onStart = { isLoading.set(true) },
                onComplete = { isLoading.set(false) },
                onError = {
                    toastMessage.set(it)
                    isLoading.set(false)
                }
            ).asLiveDataOnViewModelScope()
        }

        lastSearches = onlineRepository.getLastSearches().asLiveDataOnViewModelScope()
    }

    @MainThread
    fun fetchNextOnlineList() {
        if (!isLoading.get() && searchOnlinePage.value < 5) {
            this.searchOnlinePage.value++
        }
    }

    fun getSearchOnlinePage(): Int {
        return searchOnlinePage.value
    }

    fun setQuery(query: String, isAutoSearch: Boolean) {
        this.query.set(query)
        this.isAutoSearch.set(isAutoSearch)
        resetSearchOnlinePage()
    }

    private fun resetSearchOnlinePage() {
        searchOnlinePage.value = 0
        searchOnlinePage.value = 1
    }

    fun removeSearchHistory(searchText: String) = viewModelScope.launch {
        onlineRepository.removeSearchHistory(searchText)
    }

}