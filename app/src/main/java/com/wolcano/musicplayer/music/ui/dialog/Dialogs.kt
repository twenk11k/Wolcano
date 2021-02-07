package com.wolcano.musicplayer.music.ui.dialog

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.Html
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.customListAdapter
import com.afollestad.materialdialogs.list.listItems
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.content.Share
import com.wolcano.musicplayer.music.content.Share.shareSong
import com.wolcano.musicplayer.music.mvp.listener.ItemCallback
import com.wolcano.musicplayer.music.mvp.models.Copy
import com.wolcano.musicplayer.music.mvp.models.Playlist
import com.wolcano.musicplayer.music.mvp.models.Song
import com.wolcano.musicplayer.music.mvp.models.SongOnline
import com.wolcano.musicplayer.music.ui.adapter.customdialog.CopyItemAdapter
import com.wolcano.musicplayer.music.ui.adapter.customdialog.ShareItemAdapter
import com.wolcano.musicplayer.music.utils.SongUtils
import com.wolcano.musicplayer.music.utils.Utils
import java.util.*

object Dialogs {

    private var toast: Toast? = null

    fun copyDialog(context: Context, song: Song?) {
        val copylist = ArrayList<Copy>()
        if (song?.title != "<unknown>" && song?.title != "") copylist.add(Copy(song!!.title, 0))
        if (song.artist != "<unknown>" && song.artist != "") copylist.add(Copy(song.artist, 1))
        if (song.album != null) {
            if (song.album != "<unknown>" && song.album != "") copylist.add(Copy(song!!.album, 2))
        }
        val adapter = CopyItemAdapter(context, copylist)

        val dialog = MaterialDialog(context).show {
            title(R.string.copy_question)
            // itemsColor(ContextCompat.getColor(context, R.color.grey0))
            customListAdapter(adapter, null)
        }
        adapter.setCallback(object : ItemCallback {
            override fun onItemClicked(itemIndex: Int) {
                showToastCopy(
                    context,
                    copylist[itemIndex].text,
                    dialog
                )
            }
        })


    }

    fun copyDialog(context: Context, album: String?, artist: String) {
        val copylist = ArrayList<Copy>()
        if (artist != "<unknown>" && artist != "") copylist.add(Copy(artist, 1))
        if (album != null) {
            if (album != "<unknown>" && album != "") copylist.add(Copy(album, 2))
        }
        val adapter = CopyItemAdapter(context, copylist)
        val dialog = MaterialDialog(context).show {
            title(R.string.copy_question)
            // itemsColor(ContextCompat.getColor(context, R.color.grey0))
            customListAdapter(adapter, null)
        }
        adapter.setCallback(object : ItemCallback {
            override fun onItemClicked(itemIndex: Int) {
                showToastCopy(
                    context,
                    copylist[itemIndex].text,
                    dialog
                )
            }
        })
    }

    fun copyDialog(context: Context, song: SongOnline?) {
        val copylist = ArrayList<Copy>()
        if (song?.title != "<unknown>" && song?.title != "") copylist.add(Copy(song!!.title, 0))
        if (song.artistName != "<unknown>" && song?.artistName != "") copylist.add(
            Copy(
                song.artistName,
                1
            )
        )
        val adapter = CopyItemAdapter(context, copylist)
        val dialog = MaterialDialog(context).show {
            title(R.string.copy_question)
            // itemsColor(ContextCompat.getColor(context, R.color.grey0))
            customListAdapter(adapter, null)
        }
        adapter.setCallback(object : ItemCallback {
            override fun onItemClicked(itemIndex: Int) {
                showToastCopy(
                    context,
                    copylist[itemIndex].text,
                    dialog
                )
            }
        })
    }

    private fun showToastCopy(context: Context, message: String, dialog: MaterialDialog) {
        if (toast != null) {
            toast!!.cancel()
            toast = null
        }
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(context.getString(R.string.copy_song_infos), message)
        clipboard.primaryClip = clip
        toast = Toast.makeText(
            context,
            Html.fromHtml(context.getString(R.string.copy_to_clipboard, message)),
            Toast.LENGTH_SHORT
        )
        toast?.show()
        dialog.dismiss()
    }


    private fun showToastShare(
        context: Context,
        message: String,
        dialog: MaterialDialog,
        song: Song,
        itemIndex: Int
    ) {
        if (itemIndex == 0) {
            shareSong(context, Share.TYPE_SONG, song.songId)
        } else {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_TEXT, message)
            context.startActivity(
                Intent.createChooser(
                    sharingIntent,
                    context.getString(R.string.share_name_ofsong)
                )
            )
        }
        dialog.dismiss()
    }

    private fun showToastLike(context: Context, dialog: MaterialDialog, itemIndex: Int) {
        if (itemIndex == 0) {
            Utils.rateWolcano(context)
        } else {
            Utils.shareWolcano(context)
        }
        dialog.dismiss()
    }

    fun shareDialog(context: Context, song: Song?, isModel1: Boolean) {
        val secondItem: CharSequence = if (song?.artist == "<unknown>") {
            Html.fromHtml(context.getString(R.string.share_song_info_plain, song.title))
        } else {
            Html.fromHtml(
                context.getString(
                    R.string.share_song_info_wartist,
                    song?.title,
                    song?.artist
                )
            )
        }
        val shareListModel1 = ArrayList<Copy>()
        shareListModel1.add(Copy(context.getString(R.string.share_audio_file), 0))
        val adapterModel1 = ShareItemAdapter(context, shareListModel1)
        val shareListModel0 = ArrayList<Copy>()
        shareListModel0.add(Copy(context.getString(R.string.share_audio_file), 0))
        shareListModel0.add(Copy(secondItem.toString(), 1))
        val adapterModel0 = ShareItemAdapter(context, shareListModel0)
        if (isModel1) {
            val dialogModel1 = MaterialDialog(context).show {
                title(R.string.sharequestion)
                customListAdapter(adapterModel1, null)
            }

            adapterModel1.setCallback(object : ItemCallback {
                override fun onItemClicked(itemIndex: Int) {
                    showToastShare(
                        context,
                        shareListModel1[itemIndex].text,
                        dialogModel1,
                        song!!,
                        itemIndex
                    )
                }
            })
        } else {
            val dialogLocal = MaterialDialog(context).show {
                title(R.string.sharequestion)
                customListAdapter(adapterModel0, null)
            }
            adapterModel0.setCallback(object : ItemCallback {
                override fun onItemClicked(itemIndex: Int) {
                    showToastShare(
                        context,
                        shareListModel0[itemIndex].text,
                        dialogLocal,
                        song!!,
                        itemIndex
                    )
                }
            })
        }
    }


    fun addPlaylistDialog(context: Context, song: Song?, playlistList: List<Playlist>) {
        val chars = mutableListOf<CharSequence>()
        chars.add(context.resources.getString(R.string.create_new_playlist))
        for (i in playlistList.indices) {
            chars.add(playlistList[i].name)
        }
        val title: CharSequence
        val artist: CharSequence
        title = song!!.title
        artist = song.artist
        val albumUri =
            Uri.parse("content://media/external/audio/media/" + song.songId + "/albumart")
        Picasso.get().load(albumUri).into(object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
                update(bitmap)
            }

            override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {
                update(null)
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable) {}
            private fun update(bitmap: Bitmap?) {
                var bitmap = bitmap
                if (bitmap == null) {
                    bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.album_art)
                }
                val albumart: Drawable = BitmapDrawable(context.resources, bitmap)
                val wholeStr = """
                $title
                $artist
                """.trimIndent()
                val spanTitle = SpannableString(wholeStr)
                spanTitle.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, R.color.grey0)), """$title
""".length, wholeStr.length, 0
                )
                MaterialDialog(context).show {
                    title(text = spanTitle.toString())
                    listItems(items = chars) { dialog, index, _ ->
                        if (index == 0) {
                            createPlaylistDialog(
                                context,
                                song.songId,
                                song.title
                            )
                            return@listItems
                        }
                        SongUtils.addToPlaylist(
                            context,
                            song.songId,
                            playlistList[index - 1].id,
                            song.title
                        )
                        dialog.dismiss()

                    }
                    icon(drawable = albumart)
                }

            }

        })
    }

    private fun createPlaylistDialog(context: Context, songID: Long, musicTitle: String) {
        MaterialDialog(context).show {
            title(R.string.create_new_playlist)
            positiveButton(R.string.create)
            negativeButton(R.string.cancel)
            // positiveColor(Utils.getAccentColor(context))
            // negativeColor(Utils.getAccentColor(context))
            input(context.getString(R.string.playlist_name), prefill = "") { _, input ->
                val playlistId = SongUtils.createPlaylist(context, input.toString())
                if (playlistId != -1L) {
                    SongUtils.addToPlaylist(context, songID, playlistId, musicTitle)
                } else {
                    Toast.makeText(context, R.string.create_playlist_fail, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    }

}