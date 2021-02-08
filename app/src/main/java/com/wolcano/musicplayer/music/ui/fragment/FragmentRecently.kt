package com.wolcano.musicplayer.music.ui.fragment

import android.graphics.Color
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
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.databinding.FragmentBaseSongBinding
import com.wolcano.musicplayer.music.di.component.ApplicationComponent
import com.wolcano.musicplayer.music.di.component.DaggerSongComponent
import com.wolcano.musicplayer.music.di.module.SongModule
import com.wolcano.musicplayer.music.mvp.DisposableManager
import com.wolcano.musicplayer.music.mvp.interactor.SongInteractorImpl
import com.wolcano.musicplayer.music.mvp.listener.PlaylistListener
import com.wolcano.musicplayer.music.mvp.models.Playlist
import com.wolcano.musicplayer.music.mvp.models.Song
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.SongPresenter
import com.wolcano.musicplayer.music.mvp.view.SongView
import com.wolcano.musicplayer.music.ui.adapter.RecentlyAddedAdapter
import com.wolcano.musicplayer.music.ui.dialog.Dialogs.addPlaylistDialog
import com.wolcano.musicplayer.music.ui.dialog.SleepTimerDialog
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragmentInject
import com.wolcano.musicplayer.music.ui.helper.TintHelper
import com.wolcano.musicplayer.music.ui.helper.ToolbarContentTintHelper
import com.wolcano.musicplayer.music.utils.ColorUtils
import com.wolcano.musicplayer.music.utils.SongUtils
import com.wolcano.musicplayer.music.utils.Utils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class FragmentRecently : BaseFragmentInject(), SongView, PlaylistListener {

    private var adapter: RecentlyAddedAdapter? = null
    private var disposable: Disposable? = null
    private lateinit var binding: FragmentBaseSongBinding

    var songPresenter: SongPresenter? = null @Inject set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_base_song, container, false)
        setHasOptionsMenu(true)
        setStatusBarAndToolbar()
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
        songPresenter?.songs

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


    private fun setStatusBarAndToolbar() {
        val color = Utils.getPrimaryColor(requireContext())

        setStatusbarColor(color, binding.statusBarCustom)

        (activity as AppCompatActivity?)?.setSupportActionBar(binding.toolbar)
        binding.toolbar.setBackgroundColor(color)
        if (Utils.isColorLight(color)) {
            binding.toolbar.setTitleTextColor(Color.BLACK)
        } else {
            binding.toolbar.setTitleTextColor(Color.WHITE)
        }
        val ab = (activity as AppCompatActivity?)?.supportActionBar
        ab?.setTitle(R.string.recentlyadded)

        if (binding.toolbar.navigationIcon != null) {
            binding.toolbar.navigationIcon = TintHelper.createTintedDrawable(
                binding.toolbar.navigationIcon, ToolbarContentTintHelper.toolbarContentColor(
                    requireContext(), color
                )
            )
        }
    }

    private fun runLayoutAnimation(recyclerView: RecyclerView) {
        val context = recyclerView.context
        val controller =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
        recyclerView.layoutAnimation = controller
        recyclerView.adapter!!.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }

    fun controlIfEmpty() {
        binding.empty.setText(R.string.no_song)
        binding.empty.visibility =
            if (adapter == null || adapter?.itemCount == 0) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        if (disposable != null && !disposable!!.isDisposed) {
            disposable!!.dispose()
        }

        DisposableManager.dispose()
        super.onDestroyView()
    }

    override fun handlePlaylistDialog(song: Song?) {
        disposable = Observable.fromCallable {
            SongUtils.scanPlaylist(
                activity
            )
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { playlists: List<Playlist>? ->
                addPlaylistDialog(
                    activity!!, song, playlists!!
                )
            }
    }

    override fun setSongList(songList: ArrayList<Song>?) {
        if (songList!!.size >= 60) {
            songList.subList(60, songList.size).clear()
        }
        if (songList.size <= 30) {
            binding.recyclerview.setThumbEnabled(false)
        } else {
            binding.recyclerview.setThumbEnabled(true)
        }
        adapter = RecentlyAddedAdapter(activity!!, songList, this@FragmentRecently)
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

    override fun setupComponent(applicationComponent: ApplicationComponent?) {
        val sort = MediaStore.Audio.Media.DATE_ADDED + " DESC"

        val songComponent = DaggerSongComponent.builder()
            .applicationComponent(applicationComponent)
            .songModule(SongModule(this, this, requireActivity(), sort, SongInteractorImpl()))
            .build()

        songComponent.inject(this)
    }

}