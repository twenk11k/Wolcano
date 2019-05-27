package com.wolcano.musicplayer.music.ui.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.content.Share;
import com.wolcano.musicplayer.music.mvp.models.Copy;
import com.wolcano.musicplayer.music.mvp.models.SongOnline;
import com.wolcano.musicplayer.music.mvp.models.Playlist;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.ui.adapter.other.CopyItemAdapter;
import com.wolcano.musicplayer.music.ui.adapter.other.LikeItemAdapter;
import com.wolcano.musicplayer.music.ui.adapter.other.ShareItemAdapter;
import com.wolcano.musicplayer.music.utils.SongUtils;
import com.wolcano.musicplayer.music.utils.Utils;
import java.util.ArrayList;
import java.util.List;

public class Dialogs {

    private static Toast toast;

    public static void copyDialog(Context context, Song song) {
        ArrayList<Copy> copylist = new ArrayList<>();
        if (!song.getTitle().equals("<unknown>") && !song.getTitle().equals(""))
            copylist.add(new Copy(song.getTitle(), 0));
        if (!song.getArtist().equals("<unknown>") && !song.getArtist().equals(""))
            copylist.add(new Copy(song.getArtist(), 1));
        if (song.getAlbum() != null) {
            if (!song.getAlbum().equals("<unknown>") && !song.getAlbum().equals(""))
                copylist.add(new Copy(song.getAlbum(), 2));
        }
        final CopyItemAdapter adapter = new CopyItemAdapter(context, copylist);
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.copy_question)
                .itemsColor(ContextCompat.getColor(context, R.color.grey0))
                .adapter(adapter, null)
                .itemsCallback((dialog1, view, which, text) -> {
                    String replacedStr = text.toString().replaceAll("\"", "");
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(context.getString(R.string.copy_song_infos), replacedStr);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, Html.fromHtml(context.getString(R.string.copy_to_clipboard, replacedStr)), Toast.LENGTH_SHORT).show();
                })
                .dividerColor(context.getResources().getColor(R.color.gray1))
                .theme(Theme.DARK)
                .show();
        adapter.setCallback(itemIndex -> showToastCopy(context, copylist.get(itemIndex).getText(), dialog));

    }
    public static void copyDialog(Context context,String album,String artist) {
        ArrayList<Copy> copylist = new ArrayList<>();

        if (!artist.equals("<unknown>") && !artist.equals(""))
            copylist.add(new Copy(artist, 1));
        if (album != null) {
            if (!album.equals("<unknown>") && !album.equals(""))
                copylist.add(new Copy(album, 2));
        }
        final CopyItemAdapter adapter = new CopyItemAdapter(context, copylist);
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.copy_question)
                .itemsColor(ContextCompat.getColor(context, R.color.grey0))
                .adapter(adapter, null)
                .itemsCallback((dialog1, view, which, text) -> {
                    String replacedStr = text.toString().replaceAll("\"", "");
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(context.getString(R.string.copy_song_infos), replacedStr);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, Html.fromHtml(context.getString(R.string.copy_to_clipboard, replacedStr)), Toast.LENGTH_SHORT).show();
                })
                .dividerColor(context.getResources().getColor(R.color.gray1))
                .theme(Theme.DARK)
                .show();
        adapter.setCallback(itemIndex -> showToastCopy(context, copylist.get(itemIndex).getText(), dialog));

    }
    public static void copyDialog(Context context, SongOnline song) {
        ArrayList<Copy> copylist = new ArrayList<>();
        if (!song.getTitle().equals("<unknown>") && !song.getTitle().equals(""))
            copylist.add(new Copy(song.getTitle(), 0));
        if (!song.getArtistName().equals("<unknown>") && !song.getArtistName().equals(""))
            copylist.add(new Copy(song.getArtistName(), 1));
        final CopyItemAdapter adapter = new CopyItemAdapter(context, copylist);

        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.copy_question)
                .itemsColor(ContextCompat.getColor(context, R.color.grey0))
                .adapter(adapter, null)
                .itemsCallback((dialog1, view, which, text) -> {
                    String replacedStr = text.toString().replaceAll("\"", "");
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText(context.getString(R.string.copy_song_infos), replacedStr);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, Html.fromHtml(context.getString(R.string.copy_to_clipboard, replacedStr)), Toast.LENGTH_SHORT).show();
                })
                .dividerColor(context.getResources().getColor(R.color.gray1))
                .theme(Theme.DARK)
                .show();
        adapter.setCallback(itemIndex -> showToastCopy(context, copylist.get(itemIndex).getText(), dialog));

    }

    private static void showToastCopy(Context context, String message, MaterialDialog dialog) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(context.getString(R.string.copy_song_infos), message);
        clipboard.setPrimaryClip(clip);
        toast = Toast.makeText(context, Html.fromHtml(context.getString(R.string.copy_to_clipboard, message)), Toast.LENGTH_SHORT);
        toast.show();
        dialog.dismiss();
    }

    private static void showToastShare(Context context, String message, MaterialDialog dialog, Song song, int itemIndex) {
        if (itemIndex == 0) {
            Share.shareSong(context, Share.TYPE_SONG, song.getSongId());
        } else {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, message);
            context.startActivity(Intent.createChooser(sharingIntent, context.getString(R.string.share_name_ofsong)));
        }
        dialog.dismiss();
    }

    private static void showToastLike(Context context, MaterialDialog dialog, int itemIndex) {
        if (itemIndex == 0) {
            Utils.rateWolcano(context);
        } else {
            Utils.shareWolcano(context);
        }
        dialog.dismiss();
    }

    public static void shareDialog(Context context, Song song, Boolean isModel1) {
        CharSequence secondItem;
        if (song.getArtist().equals("<unknown>")) {
            secondItem = Html.fromHtml(context.getString(R.string.share_song_info_plain, song.getTitle()));
        } else {
            secondItem = Html.fromHtml(context.getString(R.string.share_song_info_wartist, song.getTitle(), song.getArtist()));
        }

        ArrayList<Copy> shareListModel1 = new ArrayList<>();
        shareListModel1.add(new Copy(context.getString(R.string.share_audio_file), 0));
        final ShareItemAdapter adapterModel1 = new ShareItemAdapter(context, shareListModel1);

        ArrayList<Copy> shareListModel0 = new ArrayList<>();
        shareListModel0.add(new Copy(context.getString(R.string.share_audio_file), 0));
        shareListModel0.add(new Copy(secondItem.toString(), 1));
        final ShareItemAdapter adapterModel0 = new ShareItemAdapter(context, shareListModel0);


        if (isModel1) {
            MaterialDialog dialogModel1 = new MaterialDialog.Builder(context)
                    .title(R.string.sharequestion)
                    .adapter(adapterModel1, null)
                    .theme(Theme.DARK)
                    .show();
            adapterModel1.setCallback(itemIndex -> showToastShare(context, shareListModel1.get(itemIndex).getText(), dialogModel1, song, itemIndex));
        } else {
            MaterialDialog dialogLocal = new MaterialDialog.Builder(context)
                    .title(R.string.sharequestion)
                    .adapter(adapterModel0, null)
                    .theme(Theme.DARK)
                    .show();
            adapterModel0.setCallback(itemIndex -> showToastShare(context, shareListModel0.get(itemIndex).getText(), dialogLocal, song, itemIndex));
        }

    }

    public static void addPlaylistDialog(final Context context, final Song song, List<Playlist> playlistList) {
        final CharSequence[] chars = new CharSequence[playlistList.size() + 1];
        chars[0] = context.getResources().getString(R.string.create_new_playlist);
        for (int i = 0; i < playlistList.size(); i++) {
            chars[i + 1] = playlistList.get(i).getName();
        }
        CharSequence title, artist;
        title = song.getTitle();
        artist = song.getArtist();
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
                        .items(chars)
                        .icon(albumart)
                        .limitIconToDefaultSize()
                        .itemsCallback((dialog, itemView, which, text) -> {
                            if (which == 0) {
                                createPlaylistDialog(context, song.getSongId(), song.getTitle());
                                return;
                            }
                            SongUtils.addToPlaylist(context, song.getSongId(), playlistList.get(which - 1).getId(), song.getTitle());
                            dialog.dismiss();

                        }).build().show();

            }
        });

    }

    private static void createPlaylistDialog(Context context, long songID, String musicTitle) {
        new MaterialDialog.Builder(context)
                .title(R.string.create_new_playlist)
                .positiveText(R.string.create)
                .negativeText(R.string.cancel)
                .positiveColor(Utils.getAccentColor(context))
                .negativeColor(Utils.getAccentColor(context))
                .input(context.getString(R.string.playlist_name), "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        long playistId = SongUtils.createPlaylist(context, input.toString());

                        if (playistId != -1) {
                            SongUtils.addToPlaylist(context, songID, playistId, musicTitle);
                        } else {
                            Toast.makeText(context, R.string.create_playlist_fail, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).build().show();
    }

    public static void likeDialog(Context context) {

        ArrayList<Copy> likeList = new ArrayList<>();
        likeList.add(new Copy(context.getString(R.string.pref_title_rate), 0));
        likeList.add(new Copy(context.getString(R.string.share_the_app), 1));
        final LikeItemAdapter adapterLike = new LikeItemAdapter(context, likeList);


        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.like_dialog_title)
                .adapter(adapterLike, null)
                .theme(Theme.DARK)
                .show();
        adapterLike.setCallback(itemIndex -> showToastLike(context, dialog, itemIndex));

    }
}
