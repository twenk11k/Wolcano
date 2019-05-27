package com.wolcano.musicplayer.music.ui.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.databinding.ItemGenreBinding;
import com.wolcano.musicplayer.music.mvp.models.Genre;
import com.wolcano.musicplayer.music.utils.Utils;
import java.util.List;


public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {

    private List<Genre> genreList;
    private Context context;

    public GenreAdapter(Context context, List<Genre> genreList){
        this.context = context;
        this.genreList = genreList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGenreBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_genre, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.setGenre(genreList.get(position));
        holder.binding.executePendingBindings();

        Genre genre = holder.binding.getGenre();

        holder.binding.line1.setText(genre.getName());
        holder.binding.line2.setText(Utils.createStr(context, R.plurals.Nsongs, genre.getSongCount()));

    }

    @Override
    public int getItemCount() {
        return (null != genreList ? genreList.size() : 0);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemGenreBinding binding;

        public ViewHolder(ItemGenreBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.getRoot().setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {

           Genre genre = genreList.get(getAdapterPosition());
           long genreId = genre.getId();
           String genreName = genre.getName();
           Utils.navigateToGenre(context, genreId, genreName);

        }
    }
}

