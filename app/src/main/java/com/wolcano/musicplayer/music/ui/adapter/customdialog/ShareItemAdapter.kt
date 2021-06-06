package com.wolcano.musicplayer.music.ui.adapter.customdialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.data.model.Copy
import com.wolcano.musicplayer.music.databinding.ItemCopyBinding
import com.wolcano.musicplayer.music.listener.ItemCallback
import java.util.*

class ShareItemAdapter(private val context: Context, private val shareList: ArrayList<Copy>) :
    RecyclerView.Adapter<ShareItemAdapter.ViewHolder>() {

    private var itemCallback: ItemCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareItemAdapter.ViewHolder {
        val binding: ItemCopyBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_copy,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShareItemAdapter.ViewHolder, position: Int) {
        holder.binding.copy = shareList[position]
        holder.binding.executePendingBindings()
        val share: Copy? = holder.binding.copy
        holder.binding.txtName.text = share?.text
        if (share?.icon == 0) holder.binding.imgIcon.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.baseline_insert_drive_file_white_24
            )
        )
        if (share?.icon == 1) holder.binding.imgIcon.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.baseline_text_format_white_24
            )
        )
    }

    override fun getItemCount(): Int {
        return shareList.size
    }

    fun setCallback(callback: ItemCallback?) {
        itemCallback = callback
    }

    inner class ViewHolder(val binding: ItemCopyBinding) :
        RecyclerView.ViewHolder(
            binding.root
        ),
        View.OnClickListener {
        override fun onClick(v: View) {
            itemCallback?.onItemClicked(absoluteAdapterPosition)
        }

        init {
            binding.root.setOnClickListener(::onClick)
        }
    }

}