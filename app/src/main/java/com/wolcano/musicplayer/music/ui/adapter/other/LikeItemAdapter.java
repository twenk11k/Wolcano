package com.wolcano.musicplayer.music.ui.adapter.other;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.models.Copy;

import java.util.ArrayList;

public class LikeItemAdapter extends RecyclerView.Adapter<LikeItemAdapter.ViewHolder> {

    private final ArrayList<Copy> items;
    private final Context context;
    private LikeItemAdapter.ItemCallback itemCallback;

    public LikeItemAdapter(Context context, ArrayList<Copy> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public LikeItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.copy_item, parent, false);
        return new LikeItemAdapter.ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull LikeItemAdapter.ViewHolder holder, int position) {

        holder.text.setText(items.get(position).text);
        if (items.get(position).icon == 0)
            holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.baseline_star_rate_white_48));
        if (items.get(position).icon == 1)
            holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.baseline_share_white_18));

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setCallback(LikeItemAdapter.ItemCallback callback) {
        this.itemCallback = callback;
    }

    public interface ItemCallback {

        void onItemClicked(int itemIndex);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView text;
        private ImageView icon;
        final LikeItemAdapter adapter;

        ViewHolder(View itemView, LikeItemAdapter adapter) {
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
