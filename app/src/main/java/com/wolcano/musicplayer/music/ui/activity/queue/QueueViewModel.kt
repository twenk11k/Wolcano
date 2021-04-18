package com.wolcano.musicplayer.music.ui.activity.queue

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.wolcano.musicplayer.music.base.LiveCoroutinesViewModel
import com.wolcano.musicplayer.music.data.model.Playlist
import com.wolcano.musicplayer.music.data.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(private val playlistRepository: PlaylistRepository) :
    LiveCoroutinesViewModel() {

    private var _playlistsLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val playlistsLiveData: LiveData<List<Playlist>> = _playlistsLiveData.switchMap {
        playlistRepository.retrievePlaylists().asLiveDataOnViewModelScope()
    }

    @MainThread
    fun retrievePlaylists() {
        _playlistsLiveData.value = true
    }

}