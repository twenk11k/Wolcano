package com.wolcano.musicplayer.music.ui.helper

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.listener.PlaylistListener
import com.wolcano.musicplayer.music.model.Song
import com.wolcano.musicplayer.music.provider.RemotePlay.deleteFromRemotePlay
import com.wolcano.musicplayer.music.provider.RemotePlay.getRemotePlayPos
import com.wolcano.musicplayer.music.ui.dialog.Dialogs
import com.wolcano.musicplayer.music.utils.PermissionUtils
import com.wolcano.musicplayer.music.utils.PermissionUtils.PermInterface
import com.wolcano.musicplayer.music.utils.SongUtils
import com.wolcano.musicplayer.music.utils.ToastUtils
import com.wolcano.musicplayer.music.utils.Utils
import java.util.*

class SongHelperMenu {

    companion object {
        private var dCount = 0

        fun handleMenuLocal(
            context: Context,
            v: View?,
            song: Song,
            playlistListener: PlaylistListener
        ) {
            try {
                val contextThemeWrapper =
                    ContextThemeWrapper(context, R.style.PopupMenuToolbar)
                val popup = PopupMenu(
                    contextThemeWrapper,
                    v!!
                )
                popup.menuInflater.inflate(R.menu.popup_menu_song, popup.menu)
                popup.show()
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.copy_to_clipboard -> Dialogs.copyDialog(context, song)
                        R.id.delete -> {
                            val title: CharSequence
                            val artist: CharSequence
                            title = song.title
                            artist = song.artist
                            val content: Int = R.string.delete_song_content
                            val albumUri =
                                Uri.parse("content://media/external/audio/media/" + song.songId + "/albumart")
                            Picasso.get().load(albumUri).into(object : Target {
                                override fun onBitmapLoaded(
                                    bitmap: Bitmap,
                                    from: LoadedFrom
                                ) {
                                    update(bitmap)
                                }

                                override fun onBitmapFailed(
                                    e: Exception,
                                    errorDrawable: Drawable
                                ) {
                                    update(null)
                                }

                                override fun onPrepareLoad(placeHolderDrawable: Drawable) {}
                                private fun update(bitmap: Bitmap?) {
                                    var bitmap = bitmap
                                    if (bitmap == null) {
                                        bitmap = BitmapFactory.decodeResource(
                                            context.resources,
                                            R.drawable.album_art
                                        )
                                    }
                                    val albumart: Drawable =
                                        BitmapDrawable(context.resources, bitmap)
                                    val wholeStr = """
                                $title
                                $artist
                                """.trimIndent()
                                    val spanTitle = SpannableString(wholeStr)
                                    spanTitle.setSpan(
                                        ForegroundColorSpan(
                                            ContextCompat.getColor(
                                                context,
                                                R.color.grey0
                                            )
                                        ),
                                        """$title
""".length,
                                        wholeStr.length,
                                        0
                                    )

                                    MaterialDialog(context).show {
                                        title(text = spanTitle.toString())
                                        message(content)
                                        positiveButton(R.string.yes) {
                                            if (context == null) return@positiveButton
                                            deleteFromRemotePlay(
                                                context,
                                                1,
                                                getRemotePlayPos(context),
                                                song
                                            )
                                            val alist: MutableList<Song> =
                                                ArrayList()
                                            alist.add(song)
                                            Utils.deleteTracks(
                                                context,
                                                alist
                                            )
                                        }

                                        negativeButton(R.string.no)
                                        icon(drawable = albumart)
                                    }
                                }
                            })
                        }
                        R.id.set_as_ringtone -> Utils.setRingtone(
                            context,
                            song.songId
                        )
                        R.id.add_to_playlist -> playlistListener.handlePlaylistDialog(song)
                        R.id.share -> Dialogs.shareDialog(context, song, false)
                        else -> {
                        }
                    }
                    true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun handleMenuOnline(context: AppCompatActivity, v: View, song: Song?) {
            try {
                val contextThemeWrapper =
                    ContextThemeWrapper(v.context, R.style.PopupMenuToolbar)
                val popup = android.widget.PopupMenu(contextThemeWrapper, v)
                popup.menuInflater.inflate(R.menu.popup_menu_main, popup.menu)
                popup.show()
                popup.setOnMenuItemClickListener { item: MenuItem ->
                    when (item.itemId) {
                        R.id.popup_song_copyto_clipboard -> Dialogs.copyDialog(context, song)
                        R.id.action_down -> PermissionUtils.with(context)
                            .permissions(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                            .result(object : PermInterface {
                                override fun onPermGranted() {
                                    dCount++
                                    SongUtils.downPerform(context, song)
                                }

                                override fun onPermUnapproved() {
                                    ToastUtils.show(
                                        context.applicationContext,
                                        R.string.no_perm_save_file
                                    )
                                }
                            })
                            .requestPermissions()
                        else -> {
                        }
                    }
                    true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}