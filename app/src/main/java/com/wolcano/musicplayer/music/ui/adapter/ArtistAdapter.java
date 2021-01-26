package com.wolcano.musicplayer.music.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.databinding.ItemArtistBinding;
import com.wolcano.musicplayer.music.mvp.models.Artist;
import com.wolcano.musicplayer.music.utils.Utils;

import java.util.List;


public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {

    private List<Artist> artistList;
    private Activity context;

    public ArtistAdapter(Activity context, List<Artist> artistList){
        this.context = context;
        this.artistList = artistList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemArtistBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_artist, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.setArtist(artistList.get(position));
        holder.binding.executePendingBindings();

        Artist artist = holder.binding.getArtist();

        holder.binding.line1.setText(artist.getName());
        holder.binding.line2.setText(Utils.createStr(context, R.plurals.Nsongs, artist.getSongCount()));

    }

    @Override
    public int getItemCount() {
        return (null != artistList ? artistList.size() : 0);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemArtistBinding binding;

        public ViewHolder(ItemArtistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.getRoot().setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {

            Artist artist = artistList.get(getAdapterPosition());
            long artistId = artist.getId();
            String artistName = artist.getName();
            Utils.navigateToArtist(context, artistId, artistName);

        }
    }
}

