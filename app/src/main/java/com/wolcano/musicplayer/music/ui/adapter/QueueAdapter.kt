package com.wolcano.musicplayer.music.ui.adapter

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.databinding.QueueAdapterItemBinding
import com.wolcano.musicplayer.music.model.Song
import com.wolcano.musicplayer.music.mvp.listener.PlaylistListener
import com.wolcano.musicplayer.music.provider.RemotePlay.deleteFromRemotePlay
import com.wolcano.musicplayer.music.provider.RemotePlay.getRemotePlayPos
import com.wolcano.musicplayer.music.provider.RemotePlay.playSong
import com.wolcano.musicplayer.music.ui.dialog.Dialogs
import com.wolcano.musicplayer.music.utils.PermissionUtils
import com.wolcano.musicplayer.music.utils.PermissionUtils.PermInterface
import com.wolcano.musicplayer.music.utils.SongUtils
import com.wolcano.musicplayer.music.utils.ToastUtils
import com.wolcano.musicplayer.music.utils.Utils
import java.util.*

class QueueAdapter(
    private val activity: AppCompatActivity,
    private val songList: MutableList<Song?>?,
    private val playlistListener: PlaylistListener?
) : RecyclerView.Adapter<QueueAdapter.ViewHolder>() {

    private var isPlaying = false
    private var downloadCount = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueAdapter.ViewHolder {
        val binding: QueueAdapterItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.queue_adapter_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QueueAdapter.ViewHolder, position: Int) {
        holder.binding.song = songList!![position]
        holder.binding.executePendingBindings()

        val song: Song? = holder.binding.song

        var duration = ""
        try {
            duration = Utils.getDuration(song!!.duration / 1000)
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        holder.binding.indicator.visibility = if (isPlaying && position == getRemotePlayPos(
                activity
            )
        ) View.VISIBLE else View.INVISIBLE
        holder.binding.indicator.setBackgroundColor(Utils.getAccentColor(activity.applicationContext))
        holder.binding.line1.text = song?.title
        holder.binding.line2.text = (if (duration.isEmpty()) "" else "$duration | ") + songList.get(
            position
        )?.artist
        val albumUri = "content://media/external/audio/media/" + song?.songId + "/albumart"
        Picasso.get()
            .load(albumUri)
            .placeholder(R.drawable.album_art)
            .into(holder.binding.albumArt)

        if (song?.type == Song.Tip.MODEL0) setOnSongPopupMenuListener(
            holder,
            position
        ) else setOnOnlinePopupMenuListener(holder, position)
    }

    fun setIsPlaylist(isPlaying: Boolean) {
        this.isPlaying = isPlaying
    }

    private fun setOnSongPopupMenuListener(holder: QueueAdapter.ViewHolder, position: Int) {
        holder.binding.more.setOnClickListener { v ->
            try {
                val contextThemeWrapper =
                    ContextThemeWrapper(v.context, R.style.PopupMenuToolbar)
                val popup =
                    PopupMenu(contextThemeWrapper, v)
                popup.menuInflater.inflate(R.menu.popup_menu_option_queue, popup.menu)
                popup.show()
                popup.setOnMenuItemClickListener { item: MenuItem ->
                    when (item.itemId) {
                        R.id.action_remove_from_queue -> {
                            deleteFromRemotePlay(
                                activity,
                                songList!!.size,
                                position,
                                songList[position]!!
                            )
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, itemCount - position)
                        }
                        R.id.copy_to_clipboard -> Dialogs.copyDialog(
                            activity,
                            songList!![position]!!
                        )
                        R.id.delete -> {
                            val song: Song = songList!![position]!!
                            val title: CharSequence
                            val artist: CharSequence
                            title = song.title
                            artist = song.artist
                            val content: Int = R.string.delete_song_content
                            val URI =
                                Uri.parse("content://media/external/audio/media/" + song.songId + "/albumart")
                            Picasso.get().load(URI).into(object : Target {
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
                                            activity.resources,
                                            R.drawable.album_art
                                        )
                                    }
                                    val albumart: Drawable =
                                        BitmapDrawable(activity.resources, bitmap)
                                    val wholeStr = """
                                    $title
                                    $artist
                                    """.trimIndent()
                                    val spanTitle = SpannableString(wholeStr)
                                    spanTitle.setSpan(
                                        ForegroundColorSpan(
                                            ContextCompat.getColor(activity, R.color.grey0)
                                        ), """$title
""".length, wholeStr.length, 0
                                    )
                                    MaterialDialog(activity).show {
                                        title(text = spanTitle.toString())
                                        message(content)
                                        negativeButton(R.string.no)
                                        positiveButton(R.string.yes) {
                                            deleteFromRemotePlay(
                                                activity,
                                                songList.size,
                                                position,
                                                song
                                            )
                                            val alist: MutableList<Song> =
                                                ArrayList()
                                            alist.add(song)
                                            Utils.deleteTracks(
                                                activity,
                                                alist
                                            )
                                            songList.removeAt(position)
                                            notifyItemRemoved(position)
                                            notifyItemRangeChanged(position, itemCount - position)

                                        }
                                        icon(drawable = albumart)
                                    }
                                }
                            })
                        }
                        R.id.set_as_ringtone -> Utils.setRingtone(
                            activity,
                            songList!![position]!!.songId
                        )
                        R.id.add_to_playlist -> playlistListener?.handlePlaylistDialog(
                            songList!![position]!!
                        )
                        R.id.share -> Dialogs.shareDialog(activity, songList!![position], false)
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


    private fun setOnOnlinePopupMenuListener(viewHolder: QueueAdapter.ViewHolder, position: Int) {
        viewHolder.binding.more.setOnClickListener { v ->
            try {
                val contextThemeWrapper =
                    ContextThemeWrapper(v.context, R.style.PopupMenuToolbar)
                val popup =
                    PopupMenu(contextThemeWrapper, v)
                popup.menuInflater.inflate(R.menu.popup_menu_online_queue, popup.menu)
                popup.show()
                popup.setOnMenuItemClickListener { item: MenuItem ->
                    when (item.itemId) {
                        R.id.action_remove_from_queue -> {
                            deleteFromRemotePlay(
                                activity,
                                songList!!.size,
                                position,
                                songList[position]!!
                            )
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, itemCount - position)
                        }
                        R.id.popup_song_copyto_clipboard -> Dialogs.copyDialog(
                            activity,
                            songList?.get(position)
                        )
                        R.id.action_down -> PermissionUtils.with(
                            activity
                        )
                            .permissions(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                            .result(object : PermInterface {
                                override fun onPermGranted() {
                                    downloadCount++
                                    SongUtils.downPerform(activity, songList?.get(position))
                                }

                                override fun onPermUnapproved() {
                                    ToastUtils.show(
                                        activity.applicationContext,
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
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getItemCount(): Int {
        return songList?.size ?: 0
    }


    inner class ViewHolder(val binding: QueueAdapterItemBinding) : RecyclerView.ViewHolder(
        binding.root
    ),
        View.OnClickListener {
        override fun onClick(v: View) {
            playSong(activity, adapterPosition)
        }

        init {
            binding.root.setOnClickListener(::onClick)
        }
    }

}