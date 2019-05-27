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
import com.wolcano.musicplayer.music.mvp.models.Copy;

import java.util.ArrayList;


public class CopyItemAdapter extends RecyclerView.Adapter<CopyItemAdapter.ViewHolder> {

    private final ArrayList<Copy> copyList;
    private final Context context;
    private ItemCallback itemCallback;

    public CopyItemAdapter(Context context, ArrayList<Copy> copyList) {
        this.context = context;
        this.copyList = copyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCopyBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_copy, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setCopy(copyList.get(position));
        holder.binding.executePendingBindings();
        Copy copy = holder.binding.getCopy();
        holder.binding.text.setText(copy.getText());
        if (copy.getIcon() == 0)
            holder.binding.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.baseline_music_note_white_24));
        if (copy.getIcon() == 1)
            holder.binding.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.account_circle_white));
        if (copy.getIcon() == 2)
            holder.binding.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.baseline_album_white_36));

    }

    @Override
    public int getItemCount() {
        return copyList.size();
    }

    public void setCallback(ItemCallback callback) {
        this.itemCallback = callback;
    }

    public interface ItemCallback {

        void onItemClicked(int itemIndex);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemCopyBinding binding;

        public ViewHolder(ItemCopyBinding binding) {
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
