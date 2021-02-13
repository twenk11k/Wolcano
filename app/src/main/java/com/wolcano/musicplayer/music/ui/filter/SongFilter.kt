package com.wolcano.musicplayer.music.ui.filter

import android.widget.Filter
import com.wolcano.musicplayer.music.model.Song
import com.wolcano.musicplayer.music.ui.adapter.SongAdapter
import java.util.*
import kotlin.collections.ArrayList

class SongFilter(private val songList: MutableList<Song>, private val songAdapter: SongAdapter): Filter() {

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        songAdapter.setFilterList(results!!.values as ArrayList<Song>)
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val filterResults = FilterResults()
        if (constraint != null && constraint.isNotEmpty()) {
            val charSeq1 = constraint.toString().trim { it <= ' ' }
            val charSeqLower = constraint.toString().trim { it <= ' ' }.toLowerCase()
            val charSeqUpper = constraint.toString().trim { it <= ' ' }.toUpperCase()
            val charSeqUpperUs = constraint.toString().trim { it <= ' ' }.toUpperCase(Locale.US)
            val charSeqLowerUs = constraint.toString().trim { it <= ' ' }.toLowerCase(Locale.US)
            val yeniList: MutableList<Song> = ArrayList()
            for (i in this.songList.indices) {
                if (this.songList[i].title.trim { it <= ' ' }
                        .contains(charSeq1) || this.songList[i].artist
                        .contains(charSeq1) || this.songList[i].album
                        .contains(charSeq1) || this.songList[i].title.toLowerCase()
                        .contains(charSeqLower) || this.songList[i].artist.toLowerCase()
                        .contains(charSeqLower) || this.songList[i].album.toLowerCase()
                        .contains(charSeqLower) || this.songList[i].title.toUpperCase()
                        .contains(charSeqUpper) || this.songList[i].artist.toUpperCase()
                        .contains(charSeqUpper) || this.songList[i].album.toUpperCase()
                        .contains(charSeqUpper) || this.songList[i].title.toUpperCase(
                        Locale.US
                    ).contains(charSeqUpperUs) || this.songList[i].artist.toUpperCase(
                        Locale.US
                    ).contains(charSeqUpperUs) || this.songList[i].album.toUpperCase(
                        Locale.US
                    ).contains(charSeqUpperUs) || this.songList[i].title.toLowerCase(
                        Locale.US
                    ).contains(charSeqLowerUs) || this.songList[i].artist.toLowerCase(
                        Locale.US
                    ).contains(charSeqLowerUs) || this.songList[i].album.toLowerCase(
                        Locale.US
                    ).contains(charSeqLowerUs)
                ) yeniList.add(this.songList[i])
            }
            filterResults.count = yeniList.size
            filterResults.values = yeniList
        } else {
            filterResults.count = this.songList.size
            filterResults.values = this.songList
        }
        return filterResults
    }
}