package com.wolcano.musicplayer.music.ui.fragment.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wolcano.musicplayer.music.App;
import com.wolcano.musicplayer.music.di.component.ApplicationComponent;

public abstract class BaseFragmentAlbumDetail extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupComponent(((App) getActivity().getApplication()).getApplicationComponent());
    }

    public abstract void setupComponent(ApplicationComponent applicationComponent);

}
