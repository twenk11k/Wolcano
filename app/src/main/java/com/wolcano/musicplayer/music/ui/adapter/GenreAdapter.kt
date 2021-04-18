package com.wolcano.musicplayer.music.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.databinding.ItemGenreBinding
import com.wolcano.musicplayer.music.data.model.Genre
import com.wolcano.musicplayer.music.utils.Utils.createStr
import com.wolcano.musicplayer.music.utils.Utils.navigateToGenre

class GenreAdapter(private val context: Context, private val genreList: List<Genre>) :
    RecyclerView.Adapter<GenreAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreAdapter.ViewHolder {
        val binding: ItemGenreBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_genre,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenreAdapter.ViewHolder, position: Int) {
        holder.binding.genre = genreList[position]
        holder.binding.executePendingBindings()

        val genre: Genre? = holder.binding.genre

        holder.binding.line1.text = genre?.name
        holder.binding.line2.text = createStr(context, R.plurals.Nsongs, genre!!.songCount)
    }

    override fun getItemCount(): Int {
        return genreList.size
    }

    inner class ViewHolder(val binding: ItemGenreBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        override fun onClick(v: View) {
            val genre: Genre = genreList[absoluteAdapterPosition]
            val genreId = genre.id
            val genreName = genre.name
            navigateToGenre(context, genreId, genreName)
        }

        init {
            this.binding.root.setOnClickListener(::onClick)
        }
    }

}