package com.wolcano.musicplayer.music.ui.adapter;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.listener.AdapterClickListener;
import com.wolcano.musicplayer.music.mvp.listener.GetDisposable;
import com.wolcano.musicplayer.music.mvp.listener.InterstitialQueueListener;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.provider.RemotePlay;
import com.wolcano.musicplayer.music.utils.Perms;
import com.wolcano.musicplayer.music.ui.dialog.Dialogs;
import com.wolcano.musicplayer.music.utils.SongUtils;
import com.wolcano.musicplayer.music.utils.ToastMsgUtils;
import com.wolcano.musicplayer.music.utils.Utils;
import java.util.ArrayList;
import java.util.List;

public class QueueAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Song> arraylist;
    private AppCompatActivity context;
    private boolean isPlaying = false;
    private AdapterClickListener listener;
    private InterstitialQueueListener listener1;
    private int dCount = 0;
    private GetDisposable getDisposable;

    public QueueAdapter(AppCompatActivity activity, List<Song> arraylist, AdapterClickListener listener, InterstitialQueueListener listener1,GetDisposable getDisposable) {
        this.context = activity;
        this.arraylist = arraylist;
        this.listener = listener;
        this.listener1 = listener1;
        this.getDisposable = getDisposable;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.queue_adapter_item, parent, false);
        viewHolder = new QueueAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        QueueAdapter.ViewHolder viewHolder = (QueueAdapter.ViewHolder) holder;

        Song song = arraylist.get(position);
        String dura = "";
        try {
            dura = Utils.getDura(song.getDura() / 1000);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        viewHolder.indicatorView.setVisibility((isPlaying && position == RemotePlay.get().getRemotePlayPos(context)) ? View.VISIBLE : View.INVISIBLE);
        viewHolder.indicatorView.setBackgroundColor(Utils.getAccentColor(context));
        viewHolder.line1.setText(song.getTitle());
        viewHolder.line2.setText((dura.isEmpty() ? "" : dura + " | ") + arraylist.get(position).getArtist());
        String albumUri = "content://media/external/audio/media/" + song.getSongId() + "/albumart";
        Picasso.get()
                .load(albumUri)
                .placeholder(R.drawable.album_art)
                .into(viewHolder.albumArt);
        
        if (song.getTip() == Song.Tip.MODEL0)
            setOnSongPopupMenuListener(viewHolder, position);
        else
            setOnModel1PopupMenuListener(viewHolder, position);


    }

    public void setIsPlaylist(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    private void setOnSongPopupMenuListener(QueueAdapter.ViewHolder holder, final int position) {
        holder.more.setOnClickListener(v -> {
            try {
                ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(v.getContext(), R.style.PopupMenuToolbar);

                PopupMenu popup = new PopupMenu(contextThemeWrapper, v);
                popup.getMenuInflater().inflate(R.menu.menu_option_queue, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(item -> {

                    switch (item.getItemId()) {
                        case R.id.action_remove_from_queue:
                            RemotePlay.get().deleteFromRemotePlay(context,arraylist.size(), position, arraylist.get(position));
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, getItemCount()-position);

                            break;
                        case R.id.copy_to_clipboard:
                            Dialogs.copyDialog(context, arraylist.get(position));
                            break;
                        case R.id.delete:
                            Song song = arraylist.get(position);
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
                                        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.album_art);
                                    }
                                    Drawable albumart = new BitmapDrawable(context.getResources(), bitmap);
                                    String wholeStr = title+"\n"+artist;
                                    SpannableString spanTitle = new SpannableString(wholeStr);
                                    spanTitle.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.grey0)), (title+"\n").length(),wholeStr.length() , 0);

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
                                                RemotePlay.get().deleteFromRemotePlay(context,arraylist.size(), position, song);
                                                List<Song> alist = new ArrayList<>();
                                                alist.add(song);
                                                Utils.deleteTracks(context, alist);
                                                arraylist.remove(position);
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
                            Utils.setRingtone(context, arraylist.get(position).getSongId());
                            break;
                        case R.id.add_to_playlist:
                            getDisposable.handlePlaylistDialog(arraylist.get(position));
                            break;
                        case R.id.share:
                            Dialogs.shareDialog(context, arraylist.get(position), false);
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

    private void setOnModel1PopupMenuListener(ViewHolder viewHolder, final int position) {
        viewHolder.more.setOnClickListener(v -> {
            try {
                ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(v.getContext(), R.style.PopupMenuToolbar);

                PopupMenu popup = new PopupMenu(contextThemeWrapper, v);
                popup.getMenuInflater().inflate(R.menu.menu_main_queue, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.action_remove_from_queue:
                            RemotePlay.get().deleteFromRemotePlay(context,arraylist.size(), position, arraylist.get(position));
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, getItemCount() - position);
                            break;
                        case R.id.popup_song_copyto_clipboard:
                            Dialogs.copyDialog(context, arraylist.get(position));
                            break;
                        case R.id.action_down:
                            Perms.with(context)
                                    .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    .result(new Perms.PermInterface() {
                                        @Override
                                        public void onPermGranted() {
                                            dCount++;
                                            if (dCount % 2 == 1 && dCount != 2 || dCount == 1){
                                                listener1.showInterstitial();
                                            }
                                            SongUtils.downPerform(context, arraylist.get(position));
                                        }

                                        @Override
                                        public void onPermUnapproved() {
                                            ToastMsgUtils.show(context.getApplicationContext(),R.string.no_perm_save_file);
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
        return (null != arraylist ? arraylist.size() : 0);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView line1;
        TextView line2;
        ImageView albumArt, more;
        private View indicatorView;

        public ViewHolder(View view) {
            super(view);
            this.line1 = view.findViewById(R.id.line1);
            this.line2 = view.findViewById(R.id.line2);
            this.albumArt = view.findViewById(R.id.albumArt);
            this.more = view.findViewById(R.id.more);
            indicatorView = view.findViewById(R.id.indicator);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            RemotePlay.get().playSong(context,listener.getOriginalPosition(getAdapterPosition()));
        }
    }
}
