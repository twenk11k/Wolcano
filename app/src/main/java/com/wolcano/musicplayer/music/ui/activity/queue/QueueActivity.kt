package com.wolcano.musicplayer.music.ui.activity.queue

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import butterknife.ButterKnife
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.databinding.ActivityQueueBinding
import com.wolcano.musicplayer.music.listener.OnServiceListener
import com.wolcano.musicplayer.music.listener.PlaylistListener
import com.wolcano.musicplayer.music.model.Song
import com.wolcano.musicplayer.music.provider.RemotePlay
import com.wolcano.musicplayer.music.ui.activity.base.BaseActivity
import com.wolcano.musicplayer.music.ui.adapter.QueueAdapter
import com.wolcano.musicplayer.music.ui.dialog.Dialogs
import com.wolcano.musicplayer.music.ui.dialog.SleepTimerDialog
import com.wolcano.musicplayer.music.ui.helper.ToolbarContentTintHelper
import com.wolcano.musicplayer.music.utils.ColorUtils
import com.wolcano.musicplayer.music.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QueueActivity : BaseActivity(), OnItemClickListener, OnServiceListener, PlaylistListener {

    private var adapter: QueueAdapter? = null
    private var primaryColor = -1
    private val binding: ActivityQueueBinding by binding(R.layout.activity_queue)

    val viewModel: QueueViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queue)
        ButterKnife.bind(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menuInflater.inflate(R.menu.menu_sleeptimer, menu)
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(
            this,
            binding.toolbar,
            menu,
            ColorUtils.getToolbarBackgroundColor(binding.toolbar)
        )
        return super.onCreateOptionsMenu(menu)
    }

    override fun onServiceConnection() {
        primaryColor = Utils.getPrimaryColor(this)
        setStatusbarColor(primaryColor, binding.statusBarCustom)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.toolbar.setBackgroundColor(primaryColor)
        binding.toolbar.title = resources.getString(R.string.nowplaying)
        Utils.setUpFastScrollRecyclerViewColor(binding.recyclerview, Utils.getAccentColor(this))
        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        binding.recyclerview.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        if (RemotePlay.getSongList()!!.size <= 30) {
            binding.recyclerview.setThumbEnabled(false)
        } else {
            binding.recyclerview.setThumbEnabled(true)
        }
        adapter = QueueAdapter(this, RemotePlay.getSongList(), this)
        controlIfEmpty()
        adapter?.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                controlIfEmpty()
            }
        })

        binding.recyclerview.adapter = adapter
        if (musicService != null) {
            binding.recyclerview.scrollToPosition(RemotePlay.getRemotePlayPos(musicService!!))
        }
        adapter?.setIsPlaylist(true)
        RemotePlay.onListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_sleeptimer) {
            SleepTimerDialog().show(supportFragmentManager, "SET_SLEEP_TIMER")
        } else if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun controlIfEmpty() {
        binding.empty.setText(R.string.no_queue)
        binding.empty.visibility =
            if (adapter == null || adapter!!.itemCount == 0) View.VISIBLE else View.GONE
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
    }

    override fun onChangeSong(song: Song?) {
        adapter?.notifyDataSetChanged()
    }

    override fun onPlayStart() {
    }

    override fun onPlayPause() {
    }

    override fun onProgressChange(progress: Int) {}

    override fun onBufferingUpdate(percent: Int) {}

    override fun onDestroy() {
        RemotePlay.removeListener(this)
        super.onDestroy()
    }

    override fun handlePlaylistDialog(song: Song?) {
        viewModel.retrievePlaylists()
        viewModel.playlistsLiveData.observe(this, {
            if (it != null) {
                Dialogs.addPlaylistDialog(
                    this,
                    song,
                    it
                )
            }
        })
    }

}