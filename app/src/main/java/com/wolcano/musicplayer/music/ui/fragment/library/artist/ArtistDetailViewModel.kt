package com.wolcano.musicplayer.music.ui.fragment.library.artist

import android.provider.MediaStore
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.wolcano.musicplayer.music.base.LiveCoroutinesViewModel
import com.wolcano.musicplayer.music.model.Playlist
import com.wolcano.musicplayer.music.model.Song
import com.wolcano.musicplayer.music.repository.PlaylistRepository
import com.wolcano.musicplayer.music.repository.SongRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(private val songRepository: SongRepository, private val playlistRepository: PlaylistRepository): LiveCoroutinesViewModel() {

    private var _songsLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val songsLiveData: LiveData<List<Song>>

    private var _playlistsLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val playlistsLiveData: LiveData<List<Playlist>>

    val sort = MediaStore.Audio.Media.DEFAULT_SORT_ORDER
    private var artistId: Long? = null

    init {
        songsLiveData = _songsLiveData.switchMap {
            songRepository.retrieveArtistSongs(
                sort,
                artistId
            ).asLiveDataOnViewModelScope()
        }
        playlistsLiveData = _playlistsLiveData.switchMap {
            playlistRepository.retrievePlaylists().asLiveDataOnViewModelScope()
        }
    }

    @MainThread
    fun retrieveArtistSongs(artistId: Long) {
        this.artistId = artistId
        _songsLiveData.value = true
    }

    @MainThread
    fun retrievePlaylists() {
        _playlistsLiveData.value = true
    }

}