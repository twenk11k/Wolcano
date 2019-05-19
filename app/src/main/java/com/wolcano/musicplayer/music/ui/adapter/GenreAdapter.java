package com.wolcano.musicplayer.music.ui.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.models.Genre;
import com.wolcano.musicplayer.music.utils.Utils;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Genre> arraylist;
    private Context context;

    public GenreAdapter(Context context, List<Genre> arraylist){
        this.context = context;
        this.arraylist = arraylist;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist, parent, false);
        viewHolder = new GenreAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GenreAdapter.ViewHolder viewHolder = (GenreAdapter.ViewHolder) holder;

        Genre genre = arraylist.get(position);

        viewHolder.line1.setText(genre.name);
        viewHolder.line2.setText(Utils.createStr(context, R.plurals.Nsongs, genre.songCount));

    }

    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView line1;
        TextView line2;
        public ViewHolder(View view) {
            super(view);
            line1 =  itemView.findViewById(R.id.line1);
            line2 =  itemView.findViewById(R.id.line2);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

           Genre genre = arraylist.get(getAdapterPosition());
           long genreId = genre.id;
           String genreName = genre.name;
           Utils.navigateToGenre(context, genreId, genreName);

        }
    }
}

