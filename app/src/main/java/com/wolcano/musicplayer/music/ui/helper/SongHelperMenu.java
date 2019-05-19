package com.wolcano.musicplayer.music.ui.helper;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.listener.GetDisposable;
import com.wolcano.musicplayer.music.mvp.listener.InterstitialListener;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.provider.RemotePlay;
import com.wolcano.musicplayer.music.utils.Perms;
import com.wolcano.musicplayer.music.ui.dialog.Dialogs;
import com.wolcano.musicplayer.music.utils.SongUtils;
import com.wolcano.musicplayer.music.utils.ToastMsgUtils;
import com.wolcano.musicplayer.music.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SongHelperMenu {
    private static int dCount = 0;

    public static void handleMenuLocal(Context context, View v, Song song, GetDisposable getDisposable) {
        try {
            ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, R.style.PopupMenuToolbar);

            PopupMenu popup = new PopupMenu(contextThemeWrapper, v);
            popup.getMenuInflater().inflate(R.menu.menu_song, popup.getMenu());
            popup.show();
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.copy_to_clipboard:
                            Dialogs.copyDialog(context, song);
                            break;
                        case R.id.delete:
                            CharSequence title, artist;
                            int content;
                            title = song.getTitle();
                            artist = song.getArtist();
                            content = R.string.delete_song_content;
                            Uri albumUri = Uri.parse("content://media/external/audio/media/" + song.getSongId() + "/albumart");
                            Picasso.get().load(albumUri).into(new Target() {
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

                                                RemotePlay.get().deleteFromRemotePlay(context, 1, RemotePlay.get().getRemotePlayPos(context), song);
                                                List<Song> alist = new ArrayList<>();
                                                alist.add(song);
                                                Utils.deleteTracks(context, alist);
                                            })
                                            .icon(albumart)
                                            .limitIconToDefaultSize()
                                            .show();
                                }
                            });

                            break;
                        case R.id.set_as_ringtone:
                            Utils.setRingtone(context, song.getSongId());
                            break;
                        case R.id.add_to_playlist:
                            getDisposable.handlePlaylistDialog(song);
                            break;
                        case R.id.share:
                            Dialogs.shareDialog(context, song, false);
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

    public static void handleMenuFolder(AppCompatActivity context, View v, Song song, InterstitialListener listener) {
        try {
            ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(v.getContext(), R.style.PopupMenuToolbar);

            android.widget.PopupMenu popup = new android.widget.PopupMenu(contextThemeWrapper, v);
            popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());
            popup.show();
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.popup_song_copyto_clipboard:
                        Dialogs.copyDialog(context, song);
                        break;
                    case R.id.action_down:
                        Perms.with(context)
                                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .result(new Perms.PermInterface() {
                                    @Override
                                    public void onPermGranted() {
                                        dCount++;
                                        if (dCount % 2 == 1 && dCount != 2 || dCount == 1) {
                                            listener.showInterstitial();
                                        }
                                        SongUtils.downPerform(context, song);
                                    }

                                    @Override
                                    public void onPermUnapproved() {
                                        ToastMsgUtils.show(context.getApplicationContext(), R.string.no_perm_save_file);
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
    }
}
