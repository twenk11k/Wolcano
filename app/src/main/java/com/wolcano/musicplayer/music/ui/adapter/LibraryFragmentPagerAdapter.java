package com.wolcano.musicplayer.music.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.ui.fragments.innerfragment.FragmentAlbums;
import com.wolcano.musicplayer.music.ui.fragments.innerfragment.FragmentArtists;
import com.wolcano.musicplayer.music.ui.fragments.innerfragment.FragmentGenres;
import com.wolcano.musicplayer.music.ui.fragments.innerfragment.FragmentSongs;


public class LibraryFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private Context context;

    public LibraryFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new FragmentSongs();
        } else if (position == 1) {
            return new FragmentAlbums();
        } else if (position == 2) {
            return new FragmentArtists();
        } else {
            return new FragmentGenres();
        }
    }


    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.songs);
            case 1:
                return context.getString(R.string.albums);
            case 2:
                return context.getString(R.string.artists);
            case 3:
                return context.getString(R.string.genres);
            default:
                return null;
        }
    }

}