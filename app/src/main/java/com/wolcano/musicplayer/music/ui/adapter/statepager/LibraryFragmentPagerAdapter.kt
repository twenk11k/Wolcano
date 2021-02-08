package com.wolcano.musicplayer.music.ui.adapter.statepager

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.ui.fragment.library.FragmentAlbums
import com.wolcano.musicplayer.music.ui.fragment.library.FragmentArtists
import com.wolcano.musicplayer.music.ui.fragment.library.FragmentGenres
import com.wolcano.musicplayer.music.ui.fragment.library.FragmentSongs

class LibraryFragmentPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentStatePagerAdapter(
        fm
    ) {

    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            FragmentSongs()
        } else if (position == 1) {
            FragmentAlbums()
        } else if (position == 2) {
            FragmentArtists()
        } else {
            FragmentGenres()
        }
    }

    override fun getCount(): Int {
        return 4
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(R.string.songs)
            1 -> context.getString(R.string.albums)
            2 -> context.getString(R.string.artists)
            3 -> context.getString(R.string.genres)
            else -> null
        }
    }

}