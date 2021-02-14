package com.wolcano.musicplayer.music.ui.activity.main

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.wolcano.musicplayer.music.base.LiveCoroutinesViewModel
import com.wolcano.musicplayer.music.model.ModelBitmap
import com.wolcano.musicplayer.music.model.Playlist
import com.wolcano.musicplayer.music.model.Song
import com.wolcano.musicplayer.music.repository.MainRepository
import com.wolcano.musicplayer.music.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val playlistRepository: PlaylistRepository
) : LiveCoroutinesViewModel() {

    private var _bitmapsLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val bitmapsLiveData: LiveData<List<ModelBitmap?>>

    private var _playlistsLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val playlistsLiveData: LiveData<List<Playlist>>

    private var song: Song? = null

    init {
        bitmapsLiveData = _bitmapsLiveData.switchMap {
            mainRepository.retrieveBitmaps(song).asLiveDataOnViewModelScope()
        }

        playlistsLiveData = _playlistsLiveData.switchMap {
            playlistRepository.retrievePlaylists().asLiveDataOnViewModelScope()
        }
    }

    @MainThread
    fun retrievePlaylists() {
        _playlistsLiveData.value = true
    }

    @MainThread
    fun retrieveBitmaps(song: Song?) {
        this.song = song
        _bitmapsLiveData.value = true
    }

}