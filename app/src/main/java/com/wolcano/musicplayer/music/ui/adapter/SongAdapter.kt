package com.wolcano.musicplayer.music.ui.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
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
import com.wolcano.musicplayer.music.data.model.Song
import com.wolcano.musicplayer.music.databinding.ItemSongBinding
import com.wolcano.musicplayer.music.listener.FilterListener
import com.wolcano.musicplayer.music.listener.PlaylistListener
import com.wolcano.musicplayer.music.provider.RemotePlay.deleteFromRemotePlay
import com.wolcano.musicplayer.music.provider.RemotePlay.playAdd
import com.wolcano.musicplayer.music.ui.dialog.Dialogs
import com.wolcano.musicplayer.music.ui.filter.SongFilter
import com.wolcano.musicplayer.music.utils.Utils
import java.util.*
import kotlin.collections.ArrayList

class SongAdapter(
    private val context: AppCompatActivity,
    private var songList: MutableList<Song>,
    private val filterListener: FilterListener,
    private val playlistListener: PlaylistListener
) : RecyclerView.Adapter<SongAdapter.ViewHolder>(), Filterable {

    private var filter: SongFilter? = null
    private val filterList: MutableList<Song> = songList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemSongBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_song,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.song = songList[position]
        holder.binding.executePendingBindings()
        val song = holder.binding.song
        var duration = ""
        try {
            duration = Utils.getDuration(song!!.duration / 1000)
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        holder.binding.line2.text =
            (if (duration.isEmpty()) "" else "$duration | ") + songList[position].artist
        holder.binding.line1.text = song?.title
        val contentURI = "content://media/external/audio/media/" + song?.songId + "/albumart"
        Picasso.get()
            .load(contentURI)
            .placeholder(R.drawable.album_art)
            .into(holder.binding.albumArt)
        setOnPopupMenuListener(holder, position)
    }

    fun setFilterList(tempList: ArrayList<Song>) {
        this.songList = tempList
        notifyDataSetChanged()
    }

    private fun setOnPopupMenuListener(holder: ViewHolder, position: Int) {
        holder.binding.more.setOnClickListener { v ->
            try {
                val contextThemeWrapper =
                    ContextThemeWrapper(v.context, R.style.PopupMenuToolbar)
                val popup =
                    PopupMenu(contextThemeWrapper, v)
                popup.menuInflater.inflate(R.menu.popup_menu_song, popup.menu)
                popup.show()
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.add_to_playlist -> playlistListener.handlePlaylistDialog(
                            songList[position]
                        )
                        R.id.copy_to_clipboard -> Dialogs.copyDialog(
                            context,
                            songList[position]
                        )
                        R.id.set_as_ringtone -> Utils.setRingtone(
                            context,
                            songList[position].songId
                        )
                        R.id.delete -> {
                            val song = songList[position]
                            val title: CharSequence
                            val artist: CharSequence
                            title = song.title
                            artist = song.artist
                            val content: Int = R.string.delete_song_content
                            val contentURI =
                                Uri.parse("content://media/external/audio/media/" + song.songId + "/albumart")
                            Picasso.get().load(contentURI)
                                .into(object : Target {
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
                                        val albumArt: Drawable =
                                            BitmapDrawable(context.resources, bitmap)
                                        val wholeStr =
                                            """
                                        $title
                                        $artist
                                        """.trimIndent()
                                        val spanTitle = SpannableString(wholeStr)
                                        spanTitle.setSpan(
                                            ForegroundColorSpan(
                                                ContextCompat.getColor(context, R.color.grey0)
                                            ), """$title
""".length, wholeStr.length, 0
                                        )
                                        MaterialDialog(context).show {
                                            title(text = spanTitle.toString())
                                            message(content)
                                            negativeButton(R.string.no)
                                            positiveButton(R.string.yes) {
                                                if (context == null) return@positiveButton
                                                deleteFromRemotePlay(
                                                    context,
                                                    songList.size,
                                                    position,
                                                    song
                                                )
                                                val songList: MutableList<Song> =
                                                    ArrayList()
                                                songList.add(song)
                                                Utils.deleteTracks(
                                                    context,
                                                    songList
                                                )
                                                songList.removeAt(position)
                                                notifyItemRemoved(position)
                                                notifyItemRangeChanged(position, itemCount)

                                            }
                                            icon(drawable = albumArt)
                                        }

                                    }
                                })
                        }
                        R.id.share -> Dialogs.shareDialog(context, songList[position], false)
                        else -> {
                        }
                    }
                    return@setOnMenuItemClickListener true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getItemCount(): Int {
        if (songList.size <= 30) {
            filterListener.setFastScrollIndexer(false)
        } else {
            filterListener.setFastScrollIndexer(true)
        }
        return songList.size
    }

    override fun getFilter(): Filter? {
        if (filter == null) {
            filter = SongFilter(filterList, this)
        }
        return filter
    }

    inner class ViewHolder(val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        override fun onClick(v: View) {
            Utils.hideKeyboard(context)
            val song: Song = songList[absoluteAdapterPosition]
            playAdd(context, songList, song)
        }

        init {
            this.binding.root.setOnClickListener(::onClick)
        }
    }

}