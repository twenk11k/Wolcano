package com.wolcano.musicplayer.music.ui.adapter

import android.app.Activity
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.data.model.Album
import com.wolcano.musicplayer.music.databinding.ItemAlbumBinding
import com.wolcano.musicplayer.music.ui.dialog.Dialogs.copyDialog
import com.wolcano.musicplayer.music.utils.Utils.navigateToAlbum

class AlbumAdapter(private val context: Activity, private val albumList: List<Album>) :
    RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemAlbumBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_album,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.album = albumList[position]
        holder.binding.executePendingBindings()

        val album = holder.binding.album

        holder.binding.line1.text = album?.name
        holder.binding.line2.text = album?.artist
        val albumUri = "content://media/external/audio/albumart/" + album?.id
        Picasso.get().load(albumUri).placeholder(R.drawable.album_art).into(holder.binding.albumArt)
        setOnPopupMenuListener(holder, position)
    }

    override fun getItemCount(): Int {
        return albumList.size
    }

    private fun setOnPopupMenuListener(holder: ViewHolder, position: Int) {
        holder.binding.more.setOnClickListener { v ->
            try {
                val contextThemeWrapper =
                    ContextThemeWrapper(v.context, R.style.PopupMenuToolbar)
                val popup =
                    PopupMenu(contextThemeWrapper, v)
                popup.menuInflater.inflate(R.menu.popup_menu_libary_albums, popup.menu)
                popup.show()
                popup.setOnMenuItemClickListener { item: MenuItem ->
                    if (item.itemId == R.id.copy_to_clipboard) {
                        copyDialog(
                            context,
                            albumList[position].name,
                            albumList[position].artist
                        )
                    }
                    true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    inner class ViewHolder(val binding: ItemAlbumBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        override fun onClick(view: View) {
            try {
                val album: Album = albumList[absoluteAdapterPosition]
                navigateToAlbum(
                    context, album.id,
                    album.name
                )

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        init {
            this.binding.root.setOnClickListener(::onClick)
        }
    }

}