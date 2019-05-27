package com.wolcano.musicplayer.music.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wolcano.musicplayer.music.mvp.models.Song;

import java.util.ArrayList;

public class SongsViewModel extends AndroidViewModel {


    private MutableLiveData<ArrayList<Song>> songList = new MutableLiveData<>();


    public SongsViewModel(@NonNull Application application) {
        super(application);

    }


}
