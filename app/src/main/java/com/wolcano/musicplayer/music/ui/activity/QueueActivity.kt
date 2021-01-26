package com.wolcano.musicplayer.music.ui.activity

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import butterknife.BindView
import butterknife.ButterKnife
import com.kabouzeid.appthemehelper.common.ATHToolbarActivity
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.mvp.listener.OnServiceListener
import com.wolcano.musicplayer.music.mvp.listener.PlaylistListener
import com.wolcano.musicplayer.music.mvp.models.Playlist
import com.wolcano.musicplayer.music.mvp.models.Song
import com.wolcano.musicplayer.music.provider.RemotePlay
import com.wolcano.musicplayer.music.ui.activity.base.BaseActivity
import com.wolcano.musicplayer.music.ui.adapter.QueueAdapter
import com.wolcano.musicplayer.music.ui.dialog.Dialogs
import com.wolcano.musicplayer.music.ui.dialog.SleepTimerDialog
import com.wolcano.musicplayer.music.utils.SongUtils
import com.wolcano.musicplayer.music.utils.Utils
import com.wolcano.musicplayer.music.widgets.StatusBarView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class QueueActivity : BaseActivity(), OnItemClickListener, OnServiceListener, PlaylistListener {

    @BindView(R.id.toolbar)
    private lateinit var toolbar: Toolbar

    @BindView(R.id.recyclerview)
    private lateinit var recyclerView: FastScrollRecyclerView

    @BindView(R.id.statusBarCustom)
    private lateinit var statusBarView: StatusBarView

    @BindView(R.id.empty)
    private var empty: TextView? = null

    private var adapter: QueueAdapter? = null
    private var primaryColor = -1
    private var queueDisposable: Disposable? = null

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
            toolbar,
            menu,
            ATHToolbarActivity.getToolbarBackgroundColor(toolbar)
        )
        return super.onCreateOptionsMenu(menu)
    }

    override fun onServiceConnection() {
        primaryColor = Utils.getPrimaryColor(this)
        setStatusbarColorAuto(statusBarView, primaryColor)

        if (Build.VERSION.SDK_INT < 21 && findViewById<View?>(R.id.statusBarCustom) != null) {
            findViewById<View>(R.id.statusBarCustom).visibility = View.GONE
            if (Build.VERSION.SDK_INT >= 19) {
                val statusBarHeight = Utils.getStatHeight(this)
                val layoutParams =
                    findViewById<View>(R.id.toolbar).layoutParams as RelativeLayout.LayoutParams
                layoutParams.setMargins(0, statusBarHeight, 0, 0)
                findViewById<View>(R.id.toolbar).layoutParams = layoutParams
            }
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        toolbar.setBackgroundColor(primaryColor)
        toolbar.title = resources.getString(R.string.nowplaying)
        Utils.setUpFastScrollRecyclerViewColor(recyclerView, Utils.getAccentColor(this))
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )


        if (RemotePlay.getSongList()!!.size <= 30) {
            recyclerView.setThumbEnabled(false)
        } else {
            recyclerView.setThumbEnabled(true)
        }
        adapter = QueueAdapter(this, RemotePlay.getSongList(), this)
        controlIfEmpty()
        adapter?.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                controlIfEmpty()
            }
        })

        recyclerView.adapter = adapter
        if(musicService != null) {
            recyclerView.scrollToPosition(RemotePlay.getRemotePlayPos(musicService!!))
        }
        adapter?.setIsPlaylist(true)
        RemotePlay.onListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_sleeptimer) {
            SleepTimerDialog().show(supportFragmentManager, "SET_SLEEP_TIMER")
        }
        return super.onOptionsItemSelected(item)
    }

    private fun controlIfEmpty() {
        if (empty != null) {
            empty!!.setText(R.string.no_queue)
            empty!!.visibility =
                if (adapter == null || adapter!!.itemCount == 0) View.VISIBLE else View.GONE
        }
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

        if (queueDisposable != null && !queueDisposable!!.isDisposed) {
            queueDisposable!!.dispose()
        }

        super.onDestroy()
    }

    override fun handlePlaylistDialog(song: Song?) {
        queueDisposable = Observable.fromCallable {
            SongUtils.scanPlaylist(
                this@QueueActivity
            )
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { playlists: List<Playlist>? ->
                Dialogs.addPlaylistDialog(
                    this@QueueActivity,
                    song,
                    playlists
                )
            }
    }
}