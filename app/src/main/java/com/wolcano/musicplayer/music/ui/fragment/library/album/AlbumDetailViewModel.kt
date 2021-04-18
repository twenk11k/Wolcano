package com.wolcano.musicplayer.music.ui.fragment.library.album

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
class AlbumDetailViewModel @Inject constructor(private val songRepository: SongRepository, private val playlistRepository: PlaylistRepository): LiveCoroutinesViewModel() {

    private var _songsLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val songsLiveData: LiveData<List<Song>>

    private var _playlistsLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val playlistsLiveData: LiveData<List<Playlist>>

    val sort = MediaStore.Audio.Media.DEFAULT_SORT_ORDER
    private var albumId: Long? = null

    init {
        songsLiveData = _songsLiveData.switchMap {
            songRepository.retrieveAlbumSongs(
                sort,
                albumId
            ).asLiveDataOnViewModelScope()
        }
        playlistsLiveData = _playlistsLiveData.switchMap {
            playlistRepository.retrievePlaylists().asLiveDataOnViewModelScope()
        }
    }

    @MainThread
    fun retrieveAlbumSongs(albumId: Long) {
        this.albumId = albumId
        _songsLiveData.value = true
    }

    @MainThread
    fun retrievePlaylists() {
        _playlistsLiveData.value = true
    }

}