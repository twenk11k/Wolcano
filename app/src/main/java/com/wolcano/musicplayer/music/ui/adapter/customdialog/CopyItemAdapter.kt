package com.wolcano.musicplayer.music.ui.adapter.customdialog

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.data.model.Copy
import com.wolcano.musicplayer.music.databinding.ItemCopyBinding
import com.wolcano.musicplayer.music.listener.ItemCallback
import java.util.*

class CopyItemAdapter(private val context: Context, private val copyList: ArrayList<Copy>): RecyclerView.Adapter<CopyItemAdapter.ViewHolder>() {

    private var itemCallback: ItemCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CopyItemAdapter.ViewHolder {
        val binding: ItemCopyBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_copy,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CopyItemAdapter.ViewHolder, position: Int) {
        holder.binding.copy = copyList[position]
        holder.binding.executePendingBindings()
        val copy: Copy? = holder.binding.copy
        holder.binding.txtName.text = copy?.text

        if (copy?.icon == 0) holder.binding.imgIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_music_note_white_24))
        if (copy?.icon == 1) holder.binding.imgIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.account_circle_white))
        if (copy?.icon == 2) holder.binding.imgIcon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.baseline_album_white_36))
    }

    override fun getItemCount(): Int {
        return copyList.size
    }

    fun setCallback(callback: ItemCallback?) {
        itemCallback = callback
    }

    inner class ViewHolder(val binding: ItemCopyBinding) : RecyclerView.ViewHolder(
        binding.root
    ),
        View.OnClickListener {
        override fun onClick(v: View) {
            itemCallback?.onItemClicked(absoluteAdapterPosition)
            val replacedStr = copyList[absoluteAdapterPosition].text.replace("\"".toRegex(), "")
            val clipboard =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip =
                ClipData.newPlainText(context.getString(R.string.copy_song_infos), replacedStr)
            clipboard.primaryClip = clip
            Toast.makeText(
                context,
                HtmlCompat.fromHtml(context.getString(R.string.copy_to_clipboard, replacedStr), HtmlCompat.FROM_HTML_MODE_LEGACY),
                Toast.LENGTH_SHORT
            ).show()
        }

        init {
            binding.root.setOnClickListener(::onClick)
        }
    }

}