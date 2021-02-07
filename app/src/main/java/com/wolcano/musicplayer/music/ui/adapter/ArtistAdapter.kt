package com.wolcano.musicplayer.music.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.databinding.ItemArtistBinding
import com.wolcano.musicplayer.music.mvp.models.Artist
import com.wolcano.musicplayer.music.utils.Utils.createStr
import com.wolcano.musicplayer.music.utils.Utils.navigateToArtist

class ArtistAdapter(private val context: Activity, private val artistList: List<Artist>) :
    RecyclerView.Adapter<ArtistAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemArtistBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_artist,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.artist = artistList[position]
        holder.binding.executePendingBindings()

        val artist: Artist? = holder.binding.artist

        holder.binding.line1.text = artist?.name
        holder.binding.line2.text = createStr(context, R.plurals.Nsongs, artist!!.songCount)
    }

    override fun getItemCount(): Int {
        return artistList.size
    }

    inner class ViewHolder(val binding: ItemArtistBinding) : RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        override fun onClick(v: View) {
            val artist: Artist = artistList[adapterPosition]
            val artistId = artist.id
            val artistName = artist.name
            navigateToArtist(context, artistId, artistName)
        }

        init {
            this.binding.root.setOnClickListener(::onClick)
        }
    }

}