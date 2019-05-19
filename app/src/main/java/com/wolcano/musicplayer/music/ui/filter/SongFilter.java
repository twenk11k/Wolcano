package com.wolcano.musicplayer.music.ui.filter;

import android.widget.Filter;

import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.ui.fragments.innerfragment.FragmentSongs;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * Class for filter recyclerview items
 * 2015
 */
public class SongFilter extends Filter {
    FragmentSongs.SongsAdapter songsAdapter;
    List<Song> arrayList;
    public SongFilter(List<Song> arrayList1, FragmentSongs.SongsAdapter adapter1) {
        this.arrayList = arrayList1;
        this.songsAdapter = adapter1;
    }
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        songsAdapter.arrayList = (List<Song>) results.values;
        songsAdapter.notifyDataSetChanged();
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
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).getTitle().trim().contains(charSeq1) || arrayList.get(i).getArtist().contains(charSeq1) || arrayList.get(i).getAlbum().contains(charSeq1) || arrayList.get(i).getTitle().toLowerCase().contains(charSeq_lower) || arrayList.get(i).getArtist().toLowerCase().contains(charSeq_lower) || arrayList.get(i).getAlbum().toLowerCase().contains(charSeq_lower) || arrayList.get(i).getTitle().toUpperCase().contains(charSeq_upper) || arrayList.get(i).getArtist().toUpperCase().contains(charSeq_upper) || arrayList.get(i).getAlbum().toUpperCase().contains(charSeq_upper) || arrayList.get(i).getTitle().toUpperCase(Locale.US).contains(charSeq_upper_us) || arrayList.get(i).getArtist().toUpperCase(Locale.US).contains(charSeq_upper_us) || arrayList.get(i).getAlbum().toUpperCase(Locale.US).contains(charSeq_upper_us) || arrayList.get(i).getTitle().toLowerCase(Locale.US).contains(charSeq_lower_us) || arrayList.get(i).getArtist().toLowerCase(Locale.US).contains(charSeq_lower_us) || arrayList.get(i).getAlbum().toLowerCase(Locale.US).contains(charSeq_lower_us))
                    yeniList.add(arrayList.get(i));
            }
            filterResults.count = yeniList.size();
            filterResults.values = yeniList;
        } else {
            filterResults.count = arrayList.size();
            filterResults.values = arrayList;
        }
        return filterResults;
    }


}
