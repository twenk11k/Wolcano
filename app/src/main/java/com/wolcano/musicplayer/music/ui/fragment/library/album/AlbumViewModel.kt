package com.wolcano.musicplayer.music.ui.fragment.library.album

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.wolcano.musicplayer.music.base.LiveCoroutinesViewModel
import com.wolcano.musicplayer.music.model.Album
import com.wolcano.musicplayer.music.repository.AlbumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(private val albumRepository: AlbumRepository) :
    LiveCoroutinesViewModel() {

    private var _albumsLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val albumsLiveData: LiveData<List<Album>> = _albumsLiveData.switchMap {
        albumRepository.retrieveAlbums().asLiveDataOnViewModelScope()
    }

    @MainThread
    fun retrieveAlbums() {
        _albumsLiveData.value = true
    }

}