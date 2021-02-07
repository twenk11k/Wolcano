package com.wolcano.musicplayer.music.ui.adapter

import android.content.Context
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.databinding.ItemPlaylistBinding
import com.wolcano.musicplayer.music.mvp.models.Playlist
import com.wolcano.musicplayer.music.utils.SongUtils
import com.wolcano.musicplayer.music.utils.Utils.createStr
import com.wolcano.musicplayer.music.utils.Utils.navigateToPlaylist

class PlaylistAdapter(val context: Context, val playlistList: MutableList<Playlist>) :
    RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemPlaylistBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_playlist,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.playlist = playlistList[position]
        holder.binding.executePendingBindings()

        val playlist: Playlist? = holder.binding.playlist

        holder.binding.line1.text = playlist?.name
        holder.binding.line2.text = createStr(context, R.plurals.Nsongs, playlist!!.songCount)
        holder.binding.playlistImg.setColorFilter(ContextCompat.getColor(context, R.color.grey0))
        setOnPopupMenuListener(holder, position)
    }

    private fun setOnPopupMenuListener(holder: ViewHolder, position: Int) {
        holder.binding.more.setOnClickListener { v ->
            try {
                val contextThemeWrapper =
                    ContextThemeWrapper(v.context, R.style.PopupMenuToolbar)
                val popup =
                    PopupMenu(contextThemeWrapper, v)
                popup.menuInflater.inflate(R.menu.popup_menu_playlist, popup.menu)
                popup.show()
                popup.setOnMenuItemClickListener { item: MenuItem ->
                    when (item.itemId) {
                        R.id.rename ->
                            MaterialDialog(context).show {
                                title(R.string.rename)
                                positiveButton(R.string.change)
                                negativeButton(R.string.cancel)
                                input(playlistList[position].name) { _, input ->
                                    SongUtils.renamePlaylist(
                                        context,
                                        playlistList[position].id,
                                        input.toString()
                                    )
                                    playlistList[position].name = input.toString()
                                    holder.binding.line1.text = input.toString()
                                    Toast.makeText(
                                        context,
                                        R.string.rename_playlist_success,
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                            }
                        R.id.delete ->
                            MaterialDialog(context).show {
                                title(text = playlistList[position].name)
                                message(R.string.delete_playlist)
                                positiveButton(R.string.delete) {
                                    SongUtils.deletePlaylists(
                                        context,
                                        playlistList[position].id
                                    )
                                    playlistList.removeAt(holder.adapterPosition)
                                    notifyItemRemoved(holder.adapterPosition)
                                    notifyItemRangeChanged(
                                        holder.adapterPosition,
                                        itemCount - holder.adapterPosition
                                    )
                                    Toast.makeText(
                                        context,
                                        R.string.delete_playlist_success,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                negativeButton(R.string.cancel) {
                                    dismiss()
                                }
                            }
                    }
                    true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getItemCount(): Int {
        return playlistList.size
    }

    inner class ViewHolder(val binding: ItemPlaylistBinding) : RecyclerView.ViewHolder(
        binding.root
    ),
        View.OnClickListener {
        override fun onClick(v: View) {
            val playlist: Playlist = playlistList[adapterPosition]
            val playlistID = playlist.id
            val playlistName = playlist.name
            navigateToPlaylist(
                context, playlistID,
                playlistName
            )
        }

        init {
            binding.root.setOnClickListener(::onClick)
        }
    }

}