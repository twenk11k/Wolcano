package com.wolcano.musicplayer.music.ui.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.databinding.ItemPlaylistBinding;
import com.wolcano.musicplayer.music.mvp.models.Playlist;
import com.wolcano.musicplayer.music.utils.SongUtils;
import com.wolcano.musicplayer.music.utils.Utils;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private List<Playlist> playlistList;
    private Context context;

    public PlaylistAdapter(Context context, List<Playlist> playlistList) {
        this.context = context;
        this.playlistList = playlistList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPlaylistBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_playlist, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        holder.binding.setPlaylist(playlistList.get(position));
        holder.binding.executePendingBindings();

        Playlist playlist = holder.binding.getPlaylist();

        holder.binding.line1.setText(playlist.getName());
        holder.binding.line2.setText(Utils.createStr(context, R.plurals.Nsongs, playlist.getSongCount()));
        holder.binding.playlistImg.setColorFilter(ContextCompat.getColor(context, R.color.grey0));
        setOnPopupMenuListener(holder, position);


    }

    private void setOnPopupMenuListener(PlaylistAdapter.ViewHolder holder, final int position) {
        holder.binding.more.setOnClickListener(v -> {
            try {
                ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(v.getContext(), R.style.PopupMenuToolbar);

                PopupMenu popup = new PopupMenu(contextThemeWrapper, v);
                popup.getMenuInflater().inflate(R.menu.popup_menu_playlist, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.rename:
                            new MaterialDialog.Builder(context)
                                    .title(R.string.rename)
                                    .positiveText(R.string.change)
                                    .negativeText(R.string.cancel)
                                    .positiveColor(Utils.getAccentColor(context))
                                    .negativeColor(Utils.getAccentColor(context))
                                    .input(null, playlistList.get(position).getName(), false, new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                            SongUtils.renamePlaylist(context, playlistList.get(position).getId(), input.toString());
                                            playlistList.get(position).setName(input.toString());
                                            holder.binding.line1.setText(input.toString());
                                            Toast.makeText(context, R.string.rename_playlist_success, Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .show();
                            break;
                        case R.id.delete:
                            new MaterialDialog.Builder(context)
                                    .title(playlistList.get(position).getName())
                                    .content(R.string.delete_playlist)
                                    .positiveText(R.string.delete)
                                    .negativeText(R.string.cancel)
                                    .positiveColor(Utils.getAccentColor(context))
                                    .negativeColor(Utils.getAccentColor(context))
                                    .onPositive((dialog, which) -> {
                                        SongUtils.deletePlaylists(context, playlistList.get(position).getId());
                                        playlistList.remove(holder.getAdapterPosition());
                                        notifyItemRemoved(holder.getAdapterPosition());
                                        notifyItemRangeChanged(holder.getAdapterPosition(), getItemCount() - holder.getAdapterPosition());

                                        Toast.makeText(context, R.string.delete_playlist_success, Toast.LENGTH_SHORT).show();
                                    })
                                    .onNegative((dialog, which) -> dialog.dismiss())
                                    .show();
                            break;
                    }
                    return true;
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != playlistList ? playlistList.size() : 0);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemPlaylistBinding binding;

        public ViewHolder(ItemPlaylistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.getRoot().setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            Playlist playlist = playlistList.get(getAdapterPosition());
            long playlistID = playlist.getId();
            String playlistName = playlist.getName();
            Utils.navigateToPlaylist(context, playlistID,
                    playlistName);

        }
    }
}
