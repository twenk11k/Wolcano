package com.wolcano.musicplayer.music.ui.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.models.Album;
import com.wolcano.musicplayer.music.ui.dialog.Dialogs;
import com.wolcano.musicplayer.music.utils.Utils;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Album> arraylist;
    private Activity context;

    public AlbumAdapter(Activity context, List<Album> arraylist) {
        this.context = context;
        this.arraylist = arraylist;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        viewHolder = new AlbumAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AlbumAdapter.ViewHolder viewHolder = (AlbumAdapter.ViewHolder) holder;
        Album album = arraylist.get(position);
        viewHolder.albumName.setText(album.getName());
        viewHolder.artistName.setText(album.getArtist());
        String albumUri = "content://media/external/audio/albumart/" + album.getId();
        Picasso.get().load(albumUri).placeholder(R.drawable.album_art).into(viewHolder.icon);
        setOnPopupMenuListener(viewHolder, position);

    }
    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }
    private void setOnPopupMenuListener(AlbumAdapter.ViewHolder holder, final int position) {
        holder.more.setOnClickListener(v -> {
            try {
                ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(v.getContext(), R.style.PopupMenuToolbar);

                PopupMenu popup = new PopupMenu(contextThemeWrapper, v);
                popup.getMenuInflater().inflate(R.menu.popup_menu_libary_albums, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(item -> {

                    switch (item.getItemId()) {
                        case R.id.copy_to_clipboard:
                            Dialogs.copyDialog(context, arraylist.get(position).getName(),arraylist.get(position).getArtist());
                            break;
                        default:
                            break;
                    }

                    return true;
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView albumName;
        TextView artistName;
        ImageView icon,more;


        public ViewHolder(View itemView) {
            super(itemView);
            albumName = itemView.findViewById(R.id.line1);
            artistName = itemView.findViewById(R.id.line2);
            icon = itemView.findViewById(R.id.albumArt);
            more = itemView.findViewById(R.id.more);
            icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            try {

                Album album = arraylist.get(getAdapterPosition());
                Utils.navigateToAlbum(context, album.getId(),
                        album.getName());


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

