package com.wolcano.musicplayer.music.ui.fragment.library.detail

import android.app.Activity
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.wolcano.musicplayer.music.App
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.constants.Constants
import com.wolcano.musicplayer.music.databinding.FragmentBaseSongBinding
import com.wolcano.musicplayer.music.di.component.ApplicationComponent
import com.wolcano.musicplayer.music.di.component.DaggerPlaylistSongComponent
import com.wolcano.musicplayer.music.di.component.PlaylistSongComponent
import com.wolcano.musicplayer.music.di.module.PlaylistSongModule
import com.wolcano.musicplayer.music.mvp.DisposableManager
import com.wolcano.musicplayer.music.mvp.interactor.SongInteractorImpl
import com.wolcano.musicplayer.music.mvp.listener.PlaylistListener
import com.wolcano.musicplayer.music.mvp.models.Playlist
import com.wolcano.musicplayer.music.mvp.models.Song
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.SongPresenter
import com.wolcano.musicplayer.music.mvp.view.SongView
import com.wolcano.musicplayer.music.ui.activity.MainActivity
import com.wolcano.musicplayer.music.ui.adapter.detail.PlaylistSongAdapter
import com.wolcano.musicplayer.music.ui.dialog.Dialogs.addPlaylistDialog
import com.wolcano.musicplayer.music.ui.dialog.SleepTimerDialog
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragment
import com.wolcano.musicplayer.music.ui.helper.ToolbarContentTintHelper
import com.wolcano.musicplayer.music.utils.ColorUtils
import com.wolcano.musicplayer.music.utils.SongUtils
import com.wolcano.musicplayer.music.utils.Utils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class FragmentPlaylistDetail : BaseFragment(), SongView, PlaylistListener {

    private var adapter: PlaylistSongAdapter? = null
    private var playlistID: Long = -1
    private var playlistName: String? = null
    private var primaryColor = -1
    private var accentColor = -1
    private var activity: Activity? = null
    private var disposable: Disposable? = null
    private lateinit var binding: FragmentBaseSongBinding

    var songPresenter: SongPresenter? = null @Inject set

    companion object {
        fun newInstance(id: Long, name: String?): FragmentPlaylistDetail {
            val fragment = FragmentPlaylistDetail()
            val args = Bundle()
            args.putLong(Constants.PLAYLIST_ID, id)
            args.putString(Constants.PLAYLIST_NAME, name)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (arguments != null) {
            playlistID = arguments!!.getLong(Constants.PLAYLIST_ID)
            playlistName = arguments!!.getString(Constants.PLAYLIST_NAME)
        }
        setupComponent((getActivity()!!.application as App).getApplicationComponent())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_base_song, container, false)
        activity = getActivity()

        primaryColor = Utils.getPrimaryColor(requireContext())
        accentColor = Utils.getAccentColor(requireContext())
        setStatusbarColor(primaryColor, binding.statusBarCustom)

        (getActivity() as AppCompatActivity?)?.setSupportActionBar(binding.toolbar)

        binding.toolbar.setBackgroundColor(primaryColor)
        binding.toolbar.title = playlistName
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_sleeptimer, menu)
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(
            requireActivity(),
            binding.toolbar,
            menu,
            ColorUtils.getToolbarBackgroundColor(binding.toolbar)
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_sleeptimer) {
            SleepTimerDialog().show(fragmentManager!!, "SET_SLEEP_TIMER")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Utils.setUpFastScrollRecyclerViewColor(
            binding.recyclerview, Utils.getAccentColor(
                requireContext()
            )
        )
        binding.recyclerview.layoutManager = LinearLayoutManager(getActivity())
        binding.recyclerview.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        binding.recyclerview.adapter = adapter

        songPresenter!!.playlistSongs
        setupToolbar()
    }


    private fun runLayoutAnimation(recyclerView: RecyclerView) {
        val context = recyclerView.context
        val controller =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
        recyclerView.layoutAnimation = controller
        recyclerView.adapter!!.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (disposable != null && !disposable!!.isDisposed) {
            disposable!!.dispose()
        }

        DisposableManager.dispose()
    }

    fun controlIfEmpty() {
        if (binding.empty != null) {
            binding.empty.setText(R.string.no_song)
            binding.empty.visibility =
                if (adapter == null || adapter?.itemCount == 0) View.VISIBLE else View.GONE
        }
    }

    private fun setupToolbar() {
        (getActivity() as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        val ab = (getActivity() as AppCompatActivity?)!!.supportActionBar
        ab!!.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { getActivity()!!.supportFragmentManager.popBackStack() }
    }

    override fun handlePlaylistDialog(song: Song?) {
        disposable = Observable.fromCallable<List<Playlist>> { SongUtils.scanPlaylist(activity) }
            .subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()
            ).subscribe { playlists: List<Playlist>? ->
            addPlaylistDialog(
                activity!!, song, playlists!!
            )
        }
    }

    override fun setSongList(songList: ArrayList<Song>?) {
        if (songList!!.size <= 30) {
            binding.recyclerview.setThumbEnabled(false)
        } else {
            binding.recyclerview.setThumbEnabled(true)
        }
        adapter = PlaylistSongAdapter(
            (getActivity() as MainActivity?)!!,
            songList, playlistID, this@FragmentPlaylistDetail
        )
        controlIfEmpty()
        adapter?.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                controlIfEmpty()
            }
        })

        binding.recyclerview.adapter = adapter
        runLayoutAnimation(binding.recyclerview)
    }

    fun setupComponent(applicationComponent: ApplicationComponent?) {
        val sort = MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        val playlistSongComponent: PlaylistSongComponent = DaggerPlaylistSongComponent.builder()
            .applicationComponent(applicationComponent)
            .playlistSongModule(
                PlaylistSongModule(
                    this,
                    this,
                    requireActivity(),
                    sort,
                    playlistID,
                    SongInteractorImpl()
                )
            )
            .build()
        playlistSongComponent.inject(this)
    }

}
