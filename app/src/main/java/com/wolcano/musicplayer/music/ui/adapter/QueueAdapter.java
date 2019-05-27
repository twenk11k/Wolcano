package com.wolcano.musicplayer.music.ui.adapter;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.databinding.QueueAdapterItemBinding;
import com.wolcano.musicplayer.music.mvp.listener.PlaylistListener;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.provider.RemotePlay;
import com.wolcano.musicplayer.music.utils.PermissionUtils;
import com.wolcano.musicplayer.music.ui.dialog.Dialogs;
import com.wolcano.musicplayer.music.utils.SongUtils;
import com.wolcano.musicplayer.music.utils.ToastUtils;
import com.wolcano.musicplayer.music.utils.Utils;
import java.util.ArrayList;
import java.util.List;

public class QueueAdapter extends RecyclerView.Adapter<QueueAdapter.ViewHolder> {

    private List<Song> songList;
    private AppCompatActivity activity;
    private boolean isPlaying = false;
    private int downloadCount = 0;
    private PlaylistListener playlistListener;

    public QueueAdapter(AppCompatActivity activity, List<Song> songList,PlaylistListener playlistListener) {
        this.activity = activity;
        this.songList = songList;
        this.playlistListener = playlistListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        QueueAdapterItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.queue_adapter_item,parent,false);
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
        holder.binding.indicator.setVisibility((isPlaying && position == RemotePlay.get().getRemotePlayPos(activity)) ? View.VISIBLE : View.INVISIBLE);
        holder.binding.indicator.setBackgroundColor(Utils.getAccentColor(activity.getApplicationContext()));
        holder.binding.line1.setText(song.getTitle());
        holder.binding.line2.setText((duration.isEmpty() ? "" : duration + " | ") + songList.get(position).getArtist());
        String albumUri = "content://media/external/audio/media/" + song.getSongId() + "/albumart";
        Picasso.get()
                .load(albumUri)
                .placeholder(R.drawable.album_art)
                .into(holder.binding.albumArt);
        
        if (song.getType() == Song.Tip.MODEL0)
            setOnSongPopupMenuListener(holder, position);
        else
            setOnOnlinePopupMenuListener(holder, position);


    }

    public void setIsPlaylist(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    private void setOnSongPopupMenuListener(QueueAdapter.ViewHolder holder, final int position) {
        holder.binding.more.setOnClickListener(v -> {
            try {
                ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(v.getContext(), R.style.PopupMenuToolbar);

                PopupMenu popup = new PopupMenu(contextThemeWrapper, v);
                popup.getMenuInflater().inflate(R.menu.popup_menu_option_queue, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(item -> {

                    switch (item.getItemId()) {
                        case R.id.action_remove_from_queue:
                            RemotePlay.get().deleteFromRemotePlay(activity,songList.size(), position, songList.get(position));
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, getItemCount()-position);

                            break;
                        case R.id.copy_to_clipboard:
                            Dialogs.copyDialog(activity, songList.get(position));
                            break;
                        case R.id.delete:
                            Song song = songList.get(position);
                            CharSequence title,artist;
                            int content;
                            title = song.getTitle();
                            artist = song.getArtist();
                            content = R.string.delete_song_content;
                            Uri URI = Uri.parse("content://media/external/audio/media/" + song.getSongId() + "/albumart");
                            Picasso.get().load(URI).into(new Target() {
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
                                private void update(Bitmap bitmap){
                                    if(bitmap==null){
                                        bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.album_art);
                                    }
                                    Drawable albumart = new BitmapDrawable(activity.getResources(), bitmap);
                                    String wholeStr = title+"\n"+artist;
                                    SpannableString spanTitle = new SpannableString(wholeStr);
                                    spanTitle.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.grey0)), (title+"\n").length(),wholeStr.length() , 0);

                                    new MaterialDialog.Builder(activity)
                                            .title(spanTitle)
                                            .content(content)
                                            .positiveText(R.string.yes)
                                            .negativeText(R.string.no)
                                            .positiveColor(Utils.getAccentColor(activity.getApplicationContext()))
                                            .negativeColor(Utils.getAccentColor(activity.getApplicationContext()))
                                            .onPositive((dialog, which) -> {
                                                if (activity == null)
                                                    return;
                                                RemotePlay.get().deleteFromRemotePlay(activity,songList.size(), position, song);
                                                List<Song> alist = new ArrayList<>();
                                                alist.add(song);
                                                Utils.deleteTracks(activity, alist);
                                                songList.remove(position);
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(position, getItemCount()-position);

                                            })
                                            .icon(albumart)
                                            .limitIconToDefaultSize()
                                            .show();
                                }
                            });

                            break;
                        case R.id.set_as_ringtone:
                            Utils.setRingtone(activity, songList.get(position).getSongId());
                            break;
                        case R.id.add_to_playlist:
                            playlistListener.handlePlaylistDialog(songList.get(position));
                            break;
                        case R.id.share:
                            Dialogs.shareDialog(activity, songList.get(position), false);
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

    private void setOnOnlinePopupMenuListener(ViewHolder viewHolder, final int position) {
        viewHolder.binding.more.setOnClickListener(v -> {
            try {
                ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(v.getContext(), R.style.PopupMenuToolbar);

                PopupMenu popup = new PopupMenu(contextThemeWrapper, v);
                popup.getMenuInflater().inflate(R.menu.popup_menu_online_queue, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.action_remove_from_queue:
                            RemotePlay.get().deleteFromRemotePlay(activity,songList.size(), position, songList.get(position));
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, getItemCount() - position);
                            break;
                        case R.id.popup_song_copyto_clipboard:
                            Dialogs.copyDialog(activity, songList.get(position));
                            break;
                        case R.id.action_down:
                            PermissionUtils.with(activity)
                                    .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    .result(new PermissionUtils.PermInterface() {
                                        @Override
                                        public void onPermGranted() {
                                            downloadCount++;
                                            SongUtils.downPerform(activity, songList.get(position));
                                        }

                                        @Override
                                        public void onPermUnapproved() {
                                            ToastUtils.show(activity.getApplicationContext(),R.string.no_perm_save_file);
                                        }
                                    })
                                    .reqPerm();
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

    @Override
    public int getItemCount() {
        return (null != songList ? songList.size() : 0);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private QueueAdapterItemBinding binding;

        public ViewHolder(QueueAdapterItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.getRoot().setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            RemotePlay.get().playSong(activity,getAdapterPosition());
        }
    }
}
