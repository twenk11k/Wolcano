package com.wolcano.musicplayer.music.ui.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.databinding.ItemAlbumBinding;
import com.wolcano.musicplayer.music.mvp.listener.Bind;
import com.wolcano.musicplayer.music.mvp.models.Album;
import com.wolcano.musicplayer.music.ui.dialog.Dialogs;
import com.wolcano.musicplayer.music.utils.Utils;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private List<Album> arraylist;
    private Activity context;

    public AlbumAdapter(Activity context, List<Album> arraylist) {
        this.context = context;
        this.arraylist = arraylist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAlbumBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_album, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.setAlbum(arraylist.get(position));
        holder.binding.executePendingBindings();
        Album album = holder.binding.getAlbum();
        holder.binding.line1.setText(album.getName());
        holder.binding.line2.setText(album.getArtist());
        String albumUri = "content://media/external/audio/albumart/" + album.getId();
        Picasso.get().load(albumUri).placeholder(R.drawable.album_art).into(holder.binding.albumArt);
        setOnPopupMenuListener(holder, position);

    }
    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }
    private void setOnPopupMenuListener(AlbumAdapter.ViewHolder holder, final int position) {
        holder.binding.more.setOnClickListener(v -> {
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

        private ItemAlbumBinding binding;

        public ViewHolder(ItemAlbumBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.getRoot().setOnClickListener(this::onClick);
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

