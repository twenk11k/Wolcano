package com.wolcano.musicplayer.music.ui.fragment.recentlyadded

import android.provider.MediaStore
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.wolcano.musicplayer.music.base.LiveCoroutinesViewModel
import com.wolcano.musicplayer.music.data.model.Playlist
import com.wolcano.musicplayer.music.data.model.Song
import com.wolcano.musicplayer.music.data.repository.PlaylistRepository
import com.wolcano.musicplayer.music.data.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecentlyViewModel @Inject constructor(private val songRepository: SongRepository, private val playlistRepository: PlaylistRepository): LiveCoroutinesViewModel() {

    private var _songsLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val songsLiveData: LiveData<List<Song>>

    private var _playlistsLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val playlistsLiveData: LiveData<List<Playlist>>

    private val sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC"

    init {
        songsLiveData = _songsLiveData.switchMap {
            songRepository.retrieveSongs(
                sortOrder
            ).asLiveDataOnViewModelScope()
        }
        playlistsLiveData = _playlistsLiveData.switchMap {
            playlistRepository.retrievePlaylists().asLiveDataOnViewModelScope()
        }
    }

    @MainThread
    fun retrieveSongs() {
        _songsLiveData.value = true
    }

    @MainThread
    fun retrievePlaylists() {
        _playlistsLiveData.value = true
    }
}