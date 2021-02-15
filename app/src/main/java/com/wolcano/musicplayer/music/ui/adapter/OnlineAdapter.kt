package com.wolcano.musicplayer.music.ui.adapter

import android.Manifest
import android.content.res.ColorStateList
import android.os.Build
import android.os.Handler
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.constants.Type
import com.wolcano.musicplayer.music.databinding.FooterViewBinding
import com.wolcano.musicplayer.music.databinding.ItemSongOnlineBinding
import com.wolcano.musicplayer.music.model.Song
import com.wolcano.musicplayer.music.model.SongOnline
import com.wolcano.musicplayer.music.provider.RemotePlay.playAdd
import com.wolcano.musicplayer.music.ui.dialog.Dialogs
import com.wolcano.musicplayer.music.ui.fragment.online.PlayModelLocal
import com.wolcano.musicplayer.music.utils.PermissionUtils
import com.wolcano.musicplayer.music.utils.PermissionUtils.PermInterface
import com.wolcano.musicplayer.music.utils.SongUtils
import com.wolcano.musicplayer.music.utils.ToastUtils
import com.wolcano.musicplayer.music.utils.Utils
import java.util.*

class OnlineAdapter(
    private val activity: AppCompatActivity,
    songOnlineList: ArrayList<SongOnline>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var songOnlineList: ArrayList<SongOnline>? = ArrayList<SongOnline>()
    private var showLoader = false
    private var downloadCount = 0

    init {
        this.songOnlineList = songOnlineList
    }

    private fun setOnPopupMenuListener(viewHolder: OnlineAdapter.ViewHolder, pos: Int) {
        viewHolder.binding.more.setOnClickListener { v ->
            try {
                val contextThemeWrapper =
                    ContextThemeWrapper(v.context, R.style.PopupMenuToolbar)
                val popup = PopupMenu(contextThemeWrapper, v)
                popup.menuInflater.inflate(R.menu.popup_menu_main, popup.menu)
                popup.show()
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.popup_song_copyto_clipboard -> Dialogs.copyDialog(
                            activity,
                            songOnlineList!![pos]
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
                                    SongUtils.downPerform(activity, songOnlineList!![pos])
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
                    return@setOnMenuItemClickListener true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clear() {
        val size = songOnlineList!!.size
        songOnlineList?.clear()
        notifyItemRangeRemoved(0, size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        when (viewType) {
            Type.TYPE_SONG -> {
                val binding: ItemSongOnlineBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_song_online,
                    parent,
                    false
                )
                viewHolder = ViewHolder(binding)
            }
            Type.TYPE_FOOTER -> {
                val binding: FooterViewBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.footer_view,
                    parent,
                    false
                )
                viewHolder = LoaderViewHolder(binding)
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == Type.TYPE_FOOTER) {
            if (holder is LoaderViewHolder) {
                val loaderViewHolder: LoaderViewHolder = holder
                if (showLoader) {
                    loaderViewHolder.binding.progressBar.visibility = View.VISIBLE
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) loaderViewHolder.binding.progressBar.indeterminateTintList =
                        ColorStateList.valueOf(
                            Utils.getAccentColor(activity)
                        )
                } else {
                    loaderViewHolder.binding.progressBar.visibility = View.GONE
                }
            }
        } else {
            val viewHolder: OnlineAdapter.ViewHolder = holder as OnlineAdapter.ViewHolder
            viewHolder.binding.songOnline = songOnlineList!![position]
            viewHolder.binding.executePendingBindings()
            val songOnline: SongOnline? = viewHolder.binding.songOnline
            viewHolder.binding.line1.text = songOnline!!.title
            var duration = ""
            try {
                duration = Utils.getDuration(songOnline.duration)
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            viewHolder.binding.line2.text = (if (songOnlineList!![position]!!.duration.toString()
                    .isEmpty()
            ) "" else duration) + "  |  " + songOnline.artistName
            setOnPopupMenuListener(viewHolder, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (songOnlineList!!.size == position + 1 && songOnlineList!!.size >= 49) {
            Type.TYPE_FOOTER
        } else {
            Type.TYPE_SONG
        }
    }

    override fun getItemCount(): Int {
        return if (null != songOnlineList) songOnlineList!!.size else 0
    }

    fun showLoading(status: Boolean) {
        showLoader = status
    }

    inner class ViewHolder(val binding: ItemSongOnlineBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {

        override fun onClick(v: View) {
            Utils.hideKeyboard(activity)
            val handler = Handler()
            handler.postDelayed({
                object : PlayModelLocal(activity, songOnlineList) {
                    override fun onPrepare() {}
                    override fun onTaskDone(songList: List<Song>?) {
                        if (adapterPosition != -1) playAdd(
                            activity,
                            songList!!,
                            songList[adapterPosition]
                        )
                    }

                    override fun onTaskFail(e: Exception?) {
                        ToastUtils.show(activity.applicationContext, R.string.cannot_play)
                    }
                }.onTask()
            }, 100)
        }

        init {
            this.binding.root.setOnClickListener(::onClick)
        }
    }

    inner class LoaderViewHolder(val binding: FooterViewBinding) : RecyclerView.ViewHolder(
        binding.root
    )

}