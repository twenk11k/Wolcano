package com.wolcano.musicplayer.music.ui.filter

import android.widget.Filter
import com.wolcano.musicplayer.music.data.model.Song
import com.wolcano.musicplayer.music.ui.adapter.SongAdapter
import java.util.*
import kotlin.collections.ArrayList

class SongFilter(private val songList: MutableList<Song>, private val songAdapter: SongAdapter) : Filter() {

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        songAdapter.setFilterList(results!!.values as ArrayList<Song>)
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val filterResults = FilterResults()

        if (!constraint.isNullOrEmpty()) {
            val charSeq = constraint.toString().trim { it <= ' ' }
            val charSeqLower = constraint.toString().trim { it <= ' ' }.toLowerCase(Locale.getDefault())
            val charSeqUpper = constraint.toString().trim { it <= ' ' }.toUpperCase(Locale.getDefault())
            val charSeqUpperUs = constraint.toString().trim { it <= ' ' }.toUpperCase(Locale.US)
            val charSeqLowerUs = constraint.toString().trim { it <= ' ' }.toLowerCase(Locale.US)
            val result: MutableList<Song> = ArrayList()
            for (song in songList) {
                if (song.title.trim { it <= ' ' }
                                .contains(charSeq) || song.artist
                                .contains(charSeq) || song.album
                                .contains(charSeq) || song.title.toLowerCase()
                                .contains(charSeqLower) || song.artist.toLowerCase()
                                .contains(charSeqLower) || song.album.toLowerCase()
                                .contains(charSeqLower) || song.title.toUpperCase()
                                .contains(charSeqUpper) || song.artist.toUpperCase()
                                .contains(charSeqUpper) || song.album.toUpperCase()
                                .contains(charSeqUpper) || song.title.toUpperCase(
                                Locale.US
                        ).contains(charSeqUpperUs) || song.artist.toUpperCase(
                                Locale.US
                        ).contains(charSeqUpperUs) || song.album.toUpperCase(
                                Locale.US
                        ).contains(charSeqUpperUs) || song.title.toLowerCase(
                                Locale.US
                        ).contains(charSeqLowerUs) || song.artist.toLowerCase(
                                Locale.US
                        ).contains(charSeqLowerUs) || song.album.toLowerCase(
                                Locale.US
                        ).contains(charSeqLowerUs)
                ) result.add(song)
            }
            filterResults.count = result.size
            filterResults.values = result
        } else {
            filterResults.count = this.songList.size
            filterResults.values = this.songList
        }
        return filterResults
    }

}