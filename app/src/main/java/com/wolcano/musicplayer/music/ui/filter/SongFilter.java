package com.wolcano.musicplayer.music.ui.filter;

import android.widget.Filter;

import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.ui.adapter.SongAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class SongFilter extends Filter {

    private SongAdapter songAdapter;
    private List<Song> songList;

    public SongFilter(List<Song> songList, SongAdapter songAdapter) {
        this.songList = songList;
        this.songAdapter = songAdapter;
    }
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        songAdapter.setFilterList((List<Song>) results.values);
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResults = new FilterResults();
        if (constraint != null && constraint.length() > 0) {
            String charSeq1 = constraint.toString().trim();
            String charSeq_lower = constraint.toString().trim().toLowerCase();
            String charSeq_upper = constraint.toString().trim().toUpperCase();
            String charSeq_upper_us = constraint.toString().trim().toUpperCase(Locale.US);
            String charSeq_lower_us = constraint.toString().trim().toLowerCase(Locale.US);
            List<Song> yeniList = new ArrayList<>();
            for (int i = 0; i < this.songList.size(); i++) {
                if (this.songList.get(i).getTitle().trim().contains(charSeq1) || this.songList.get(i).getArtist().contains(charSeq1) || this.songList.get(i).getAlbum().contains(charSeq1) || this.songList.get(i).getTitle().toLowerCase().contains(charSeq_lower) || this.songList.get(i).getArtist().toLowerCase().contains(charSeq_lower) || this.songList.get(i).getAlbum().toLowerCase().contains(charSeq_lower) || this.songList.get(i).getTitle().toUpperCase().contains(charSeq_upper) || this.songList.get(i).getArtist().toUpperCase().contains(charSeq_upper) || this.songList.get(i).getAlbum().toUpperCase().contains(charSeq_upper) || this.songList.get(i).getTitle().toUpperCase(Locale.US).contains(charSeq_upper_us) || this.songList.get(i).getArtist().toUpperCase(Locale.US).contains(charSeq_upper_us) || this.songList.get(i).getAlbum().toUpperCase(Locale.US).contains(charSeq_upper_us) || this.songList.get(i).getTitle().toLowerCase(Locale.US).contains(charSeq_lower_us) || this.songList.get(i).getArtist().toLowerCase(Locale.US).contains(charSeq_lower_us) || this.songList.get(i).getAlbum().toLowerCase(Locale.US).contains(charSeq_lower_us))
                    yeniList.add(this.songList.get(i));
            }
            filterResults.count = yeniList.size();
            filterResults.values = yeniList;
        } else {
            filterResults.count = this.songList.size();
            filterResults.values = this.songList;
        }
        return filterResults;
    }


}
