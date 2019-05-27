package com.wolcano.musicplayer.music.ui.adapter.other;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.databinding.ItemCopyBinding;
import com.wolcano.musicplayer.music.mvp.listener.ItemCallback;
import com.wolcano.musicplayer.music.mvp.models.Copy;

import java.util.ArrayList;

public class ShareItemAdapter extends RecyclerView.Adapter<ShareItemAdapter.ViewHolder> {

    private final ArrayList<Copy> shareList;
    private final Context context;
    private ItemCallback itemCallback;

    public ShareItemAdapter(Context context, ArrayList<Copy> shareList) {
        this.context = context;
        this.shareList = shareList;
    }

    @NonNull
    @Override
    public ShareItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCopyBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_copy, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ShareItemAdapter.ViewHolder holder, int position) {
        holder.binding.setCopy(shareList.get(position));
        holder.binding.executePendingBindings();
        Copy share = holder.binding.getCopy();
        holder.binding.text.setText(share.getText());
        if (share.getIcon() == 0)
            holder.binding.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.baseline_insert_drive_file_white_24));
        if (share.getIcon() == 1)
            holder.binding.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.baseline_text_format_white_24));

    }

    @Override
    public int getItemCount() {
        return shareList.size();
    }

    public void setCallback(ItemCallback callback) {
        this.itemCallback = callback;
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
