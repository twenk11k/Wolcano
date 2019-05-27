package com.wolcano.musicplayer.music.ui.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.models.Playlist;
import com.wolcano.musicplayer.music.utils.SongUtils;
import com.wolcano.musicplayer.music.utils.Utils;

import java.util.List;

public class PlaylistAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Playlist> arraylist;
    private Context context;

    public PlaylistAdapter(Context context, List<Playlist> arraylist){
        this.context = context;
        this.arraylist = arraylist;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist, parent, false);
        viewHolder = new PlaylistAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PlaylistAdapter.ViewHolder viewHolder = (PlaylistAdapter.ViewHolder) holder;

        Playlist playlist = arraylist.get(position);

        viewHolder.line2.setText(Utils.createStr(context, R.plurals.Nsongs, playlist.getSongCount()));
        viewHolder.line1.setText(playlist.getName());
        viewHolder.albumArt.setColorFilter(ContextCompat.getColor(context,R.color.grey0));
        setOnPopupMenuListener(viewHolder, position);


    }
    private void setOnPopupMenuListener(PlaylistAdapter.ViewHolder holder, final int position) {
        holder.more.setOnClickListener(v -> {
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
                                    .input(null, arraylist.get(position).getName(), false, new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                            SongUtils.renamePlaylist(context,arraylist.get(position).getId(), input.toString());
                                            arraylist.get(position).setName(input.toString());
                                            holder.line1.setText(input.toString());
                                            Toast.makeText(context, R.string.rename_playlist_success, Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .show();
                            break;
                        case R.id.delete:
                            new MaterialDialog.Builder(context)
                                    .title(arraylist.get(position).getName())
                                    .content(R.string.delete_playlist)
                                    .positiveText(R.string.delete)
                                    .negativeText(R.string.cancel)
                                    .positiveColor(Utils.getAccentColor(context))
                                    .negativeColor(Utils.getAccentColor(context))
                                    .onPositive((dialog, which) -> {
                                        SongUtils.deletePlaylists(context,arraylist.get(position).getId());
                                        arraylist.remove(holder.getAdapterPosition());
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
        return (null != arraylist ? arraylist.size() : 0);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView line1;
        TextView line2;
        ImageView albumArt, more;
        public ViewHolder(View view) {
            super(view);
            this.line1 =  view.findViewById(R.id.line1);
            this.line2 =  view.findViewById(R.id.line2);
            this.albumArt =  view.findViewById(R.id.playlistImg);
            this.more = view.findViewById(R.id.more);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Playlist playlist = arraylist.get(getAdapterPosition());
                long playlistID = playlist.getId();
                String playlistName = playlist.getName();
                Utils.navigateToPlaylist(context, playlistID,
                        playlistName);

        }
    }
}
