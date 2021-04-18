package com.wolcano.musicplayer.music.data.repository

import androidx.annotation.WorkerThread
import com.skydoves.whatif.whatIfNotNull
import com.wolcano.musicplayer.music.App
import com.wolcano.musicplayer.music.data.model.ModelBitmap
import com.wolcano.musicplayer.music.data.model.Song
import com.wolcano.musicplayer.music.widgets.SongCover
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.*

class MainRepository {

    @WorkerThread
    fun retrieveBitmaps(song: Song?) = flow<List<ModelBitmap?>> {
        val list: MutableList<ModelBitmap?> = ArrayList()
        list.add(SongCover.loadBlurredModel(App.getContext(), song))
        list.add(SongCover.loadOvalModel(App.getContext(), song))
        list.apply { 
            this.whatIfNotNull { 
                emit(it)
            }
        }
    }.flowOn(Dispatchers.IO)

}