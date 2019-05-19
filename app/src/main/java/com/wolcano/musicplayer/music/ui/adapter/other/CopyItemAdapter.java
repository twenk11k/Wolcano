package com.wolcano.musicplayer.music.ui.adapter.other;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.models.Copy;

import java.util.ArrayList;


public class CopyItemAdapter extends RecyclerView.Adapter<CopyItemAdapter.ViewHolder> {

    private final ArrayList<Copy> items;
    private final Context context;
    public ItemCallback itemCallback;

    public CopyItemAdapter(Context context, ArrayList<Copy> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public CopyItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.copy_item, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.text.setText(items.get(position).text);
        if (items.get(position).icon == 0)
            holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.baseline_song));
        if (items.get(position).icon == 1)
            holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.account_circle_white));
        if (items.get(position).icon == 2)
            holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.baseline_album_white_36));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setCallback(ItemCallback callback) {
        this.itemCallback = callback;
    }

    public interface ItemCallback {

        void onItemClicked(int itemIndex);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView text;
        private ImageView icon;
        final CopyItemAdapter adapter;

        ViewHolder(View itemView, CopyItemAdapter adapter) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            icon = itemView.findViewById(R.id.icon);
            this.adapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            adapter.itemCallback.onItemClicked(getAdapterPosition());
        }
    }
}
