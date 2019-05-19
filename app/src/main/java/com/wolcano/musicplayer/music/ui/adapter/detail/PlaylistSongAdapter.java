package com.wolcano.musicplayer.music.ui.adapter.detail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.provider.RemotePlay;
import com.wolcano.musicplayer.music.ui.dialog.Dialogs;
import com.wolcano.musicplayer.music.utils.SongUtils;
import com.wolcano.musicplayer.music.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class PlaylistSongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Song> arraylist;
    private Context context;
    private long playlistID;
    private AdapterClickListener listener;
    private GetDisposable getDisposable;
    public PlaylistSongAdapter(Context context, List<Song> arraylist, long playlistID, AdapterClickListener listener,GetDisposable getDisposable){
        this.context = context;
        this.arraylist = arraylist;
        this.playlistID = playlistID;
        this.listener = listener;
        this.getDisposable = getDisposable;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        viewHolder = new PlaylistSongAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PlaylistSongAdapter.ViewHolder viewHolder = (PlaylistSongAdapter.ViewHolder) holder;

        Song song = arraylist.get(position);
        String dura = "";
        try {
            dura = Utils.getDura(song.getDura() / 1000);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        viewHolder.line2.setText((dura.isEmpty() ? "" : dura + " | ") + arraylist.get(position).getArtist());
        viewHolder.line1.setText(song.getTitle());
        String contentURI = "content://media/external/audio/media/" + song.getSongId() + "/albumart";
        Picasso.get()
                .load(contentURI)
                .placeholder(R.drawable.album_art)
                .into(viewHolder.albumArt);
        setOnPopupMenuListener(viewHolder, position);


    }
    private void setOnPopupMenuListener(PlaylistSongAdapter.ViewHolder holder, final int position) {
        holder.more.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(v.getContext(), R.style.PopupMenuToolbar);

                    PopupMenu popup = new PopupMenu(contextThemeWrapper, v);
                    popup.getMenuInflater().inflate(R.menu.menu_playlist_song, popup.getMenu());
                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {
                                case R.id.remove_from_playlist:
                                    SongUtils.removeFromPlaylist(context, new long[]{arraylist.get(position).getSongId()},playlistID);
                                    arraylist.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, getItemCount() - position);
                                    break;
                                case R.id.copy_to_clipboard:
                                    Dialogs.copyDialog(context,arraylist.get(position));
                                    break;
                                case R.id.delete:
                                    Song song = arraylist.get(position);
                                    CharSequence title,artist;
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
                                                        notifyItemRangeChanged(position, getItemCount());
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
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return (null != arraylist ? arraylist.size() : 0);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView line1;
        ImageView albumArt, more;
        TextView line2;
        public ViewHolder(View view) {
            super(view);
            this.line1 =  view.findViewById(R.id.line1);
            this.line2 =  view.findViewById(R.id.line2);
            this.albumArt =  view.findViewById(R.id.albumArt);
            this.more = view.findViewById(R.id.more);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Song song = arraylist.get(listener.getOriginalPosition(getAdapterPosition()));
            RemotePlay.get().playAdd(context,arraylist,song);
        }
    }
}
