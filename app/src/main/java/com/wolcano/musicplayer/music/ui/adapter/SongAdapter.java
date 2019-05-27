package com.wolcano.musicplayer.music.ui.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.databinding.ItemSongBinding;
import com.wolcano.musicplayer.music.mvp.listener.FilterListener;
import com.wolcano.musicplayer.music.mvp.listener.PlaylistListener;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.provider.RemotePlay;
import com.wolcano.musicplayer.music.ui.dialog.Dialogs;
import com.wolcano.musicplayer.music.ui.filter.SongFilter;
import com.wolcano.musicplayer.music.utils.Utils;
import java.util.ArrayList;
import java.util.List;



public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> implements Filterable {

    private List<Song> songList;
    private AppCompatActivity context;
    private SongFilter filter;
    private List<Song> filterList;
    private FilterListener filterListener;
    private PlaylistListener playlistListener;


    public SongAdapter(AppCompatActivity context, List<Song> songList, FilterListener filterListener,PlaylistListener playlistListener) {
        this.context = context;
        this.songList = songList;
        this.filterList = songList;
        this.filterListener = filterListener;
        this.playlistListener = playlistListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSongBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.item_song,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.setSong(songList.get(position));
        holder.binding.executePendingBindings();
        Song song = holder.binding.getSong();
        String duration = "";
        try {
            duration = Utils.getDuration(song.getDuration() / 1000);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        holder.binding.line2.setText((duration.isEmpty() ? "" : duration + " | ") + songList.get(position).getArtist());
        holder.binding.line1.setText(song.getTitle());
        String contentURI = "content://media/external/audio/media/" + song.getSongId() + "/albumart";
        Picasso.get()
                .load(contentURI)
                .placeholder(R.drawable.album_art)
                .into(holder.binding.albumArt);
        setOnPopupMenuListener(holder, position);
    }

    public void setFilterList(List<Song> tempList){
        this.songList = tempList;
        notifyDataSetChanged();
    }

    private void setOnPopupMenuListener(SongAdapter.ViewHolder holder, final int position) {
        holder.binding.more.setOnClickListener(v -> {
            try {
                ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(v.getContext(), R.style.PopupMenuToolbar);

                PopupMenu popup = new PopupMenu(contextThemeWrapper, v);
                popup.getMenuInflater().inflate(R.menu.popup_menu_song, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.add_to_playlist:
                                playlistListener.handlePlaylistDialog(songList.get(position));
                                break;
                            case R.id.copy_to_clipboard:
                                Dialogs.copyDialog(context, songList.get(position));
                                break;
                            case R.id.set_as_ringtone:
                                Utils.setRingtone(context, songList.get(position).getSongId());
                                break;
                            case R.id.delete:

                                Song song = songList.get(position);
                                CharSequence title, artist;
                                int content;
                                title = song.getTitle();
                                artist = song.getArtist();
                                content = R.string.delete_song_content;
                                Uri contentURI = Uri.parse("content://media/external/audio/media/" + song.getSongId() + "/albumart");
                                Picasso.get().load(contentURI).into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        update(bitmap);
                                    }

                                    @Override
                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                        update(null);
                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                                    }

                                    private void update(Bitmap bitmap) {
                                        if (bitmap == null) {
                                            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.album_art);
                                        }
                                        Drawable albumart = new BitmapDrawable(context.getResources(), bitmap);
                                        String wholeStr = title + "\n" + artist;
                                        SpannableString spanTitle = new SpannableString(wholeStr);
                                        spanTitle.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.grey0)), (title + "\n").length(), wholeStr.length(), 0);

                                        new MaterialDialog.Builder(context)
                                                .title(spanTitle)
                                                .content(content)
                                                .positiveText(R.string.yes)
                                                .negativeText(R.string.no)
                                                .positiveColor(Utils.getAccentColor(context))
                                                .negativeColor(Utils.getAccentColor(context))
                                                .onPositive((dialog, which) -> {
                                                    if (context == null)
                                                        return;
                                                    RemotePlay.get().deleteFromRemotePlay(context, songList.size(), position, song);
                                                    List<Song> alist = new ArrayList<>();
                                                    alist.add(song);
                                                    Utils.deleteTracks(context, alist);
                                                    songList.remove(position);
                                                    notifyItemRemoved(position);
                                                    notifyItemRangeChanged(position, getItemCount());
                                                })
                                                .icon(albumart)
                                                .limitIconToDefaultSize()
                                                .show();
                                    }
                                });

                                break;
                            case R.id.share:
                                Dialogs.shareDialog(context, songList.get(position), false);
                                break;
                            default:
                                break;
                        }

                        return true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (songList.size() <= 30) {
            filterListener.setFastScrollIndexer(false);
        } else {
            filterListener.setFastScrollIndexer(true);
        }
        return (null != songList ? songList.size() : 0);
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new SongFilter(filterList, this);
        }
        return filter;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemSongBinding binding;

        public ViewHolder(ItemSongBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.getRoot().setOnClickListener(this::onClick);

        }
        @Override
        public void onClick(View v) {
            Utils.hideKeyboard(context);
            Song song = songList.get(getAdapterPosition());
            RemotePlay.get().playAdd(context, songList, song);
        }
    }


}
