package com.wolcano.musicplayer.music.ui.adapter.customdialog;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.databinding.ItemCopyBinding;
import com.wolcano.musicplayer.music.mvp.models.Copy;

import java.util.ArrayList;

public class LikeItemAdapter extends RecyclerView.Adapter<LikeItemAdapter.ViewHolder> {

    private ArrayList<Copy> likeList;
    private Context context;
    private ItemCallback itemCallback;

    public LikeItemAdapter(Context context, ArrayList<Copy> likeList) {
        this.context = context;
        this.likeList = likeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCopyBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_copy, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.setCopy(likeList.get(position));
        holder.binding.executePendingBindings();

        Copy like = holder.binding.getCopy();

        holder.binding.text.setText(like.getText());
        if (like.getIcon() == 0)
            holder.binding.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.baseline_star_rate_white_48));
        if (like.getIcon() == 1)
            holder.binding.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.baseline_share_white_18));

    }

    @Override
    public int getItemCount() {
        return likeList.size();
    }

    public void setCallback(LikeItemAdapter.ItemCallback callback) {
        this.itemCallback = callback;
    }

    public interface ItemCallback {

        void onItemClicked(int itemIndex);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemCopyBinding binding;

        ViewHolder(ItemCopyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.getRoot().setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            itemCallback.onItemClicked(getAdapterPosition());
        }
    }
}
