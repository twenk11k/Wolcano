package com.wolcano.musicplayer.music.ui.fragment.library.genre

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.wolcano.musicplayer.music.base.LiveCoroutinesViewModel
import com.wolcano.musicplayer.music.model.Genre
import com.wolcano.musicplayer.music.repository.GenreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GenreViewModel @Inject constructor(private val genreRepository: GenreRepository):
    LiveCoroutinesViewModel() {

    private var _genresLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val genresLiveData: LiveData<List<Genre>> = _genresLiveData.switchMap {
        genreRepository.retrieveGenres().asLiveDataOnViewModelScope()
    }

    @MainThread
    fun retrieveGenres() {
        _genresLiveData.value = true
    }

}