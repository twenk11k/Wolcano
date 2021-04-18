package com.wolcano.musicplayer.music.ui.fragment.library.artist

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.wolcano.musicplayer.music.base.LiveCoroutinesViewModel
import com.wolcano.musicplayer.music.data.model.Artist
import com.wolcano.musicplayer.music.data.repository.ArtistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ArtistViewModel @Inject constructor(private val artistRepository: ArtistRepository) : LiveCoroutinesViewModel() {

    private var _artistsLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val artistsLiveData: LiveData<List<Artist>> = _artistsLiveData.switchMap {
        artistRepository.retrieveArtists().asLiveDataOnViewModelScope()
    }

    @MainThread
    fun retrieveArtists() {
        _artistsLiveData.value = true
    }

}