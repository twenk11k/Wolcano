package com.wolcano.musicplayer.music.ui.adapter

import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.bindables.binding
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.databinding.ItemSongOnlineBinding
import com.wolcano.musicplayer.music.model.SongOnline

class OnlineAdapter(
    private val clickListener: OnlineAdapterClickListener
) : RecyclerView.Adapter<OnlineAdapter.OnlineViewHolder>() {

    private val items: MutableList<SongOnline> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlineViewHolder {
        val binding = parent.binding<ItemSongOnlineBinding>(R.layout.item_song_online)
        return OnlineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OnlineViewHolder, position: Int) {
        holder.binding.apply {
            data = items[position]
            executePendingBindings()
            setOnPopupMenuListener(this, position)
        }
    }

    private fun setOnPopupMenuListener(binding: ItemSongOnlineBinding, pos: Int) {
        binding.more.setOnClickListener { v ->

            try {
                val contextThemeWrapper =
                    ContextThemeWrapper(v.context, R.style.PopupMenuToolbar)
                val popup = PopupMenu(contextThemeWrapper, v)
                popup.menuInflater.inflate(R.menu.popup_menu_main, popup.menu)
                popup.show()
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.popup_song_copyto_clipboard -> clickListener.copySongInfo(items[pos])
                        R.id.action_down -> clickListener.performDownload(items[pos])
                    }
                    return@setOnMenuItemClickListener true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun clear() {
        val size = items.size
        items.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun setOnlineList(songList: List<SongOnline>) {
        val previousItemSize = items.size
        items.addAll(songList)
        notifyItemRangeChanged(previousItemSize, songList.size)
    }

    fun clearAndSetOnlineList(onlineList: List<SongOnline>) {
        items.clear()
        items.addAll(onlineList)
        this.notifyDataSetChanged()
    }

    override fun getItemCount() = items.size

    inner class OnlineViewHolder(val binding: ItemSongOnlineBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {

        init {
            this.binding.root.setOnClickListener(::onClick)
        }

        override fun onClick(v: View) {
            if (absoluteAdapterPosition != -1) {
                clickListener.playSongOnline(items, absoluteAdapterPosition)
            }
        }
    }

}