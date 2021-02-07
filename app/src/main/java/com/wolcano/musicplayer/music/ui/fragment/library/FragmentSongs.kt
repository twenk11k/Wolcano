package com.wolcano.musicplayer.music.ui.fragment.library

import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.databinding.FragmentSongsBinding
import com.wolcano.musicplayer.music.di.component.ApplicationComponent
import com.wolcano.musicplayer.music.di.component.DaggerSongComponent
import com.wolcano.musicplayer.music.di.module.SongModule
import com.wolcano.musicplayer.music.mvp.DisposableManager
import com.wolcano.musicplayer.music.mvp.interactor.SongInteractorImpl
import com.wolcano.musicplayer.music.mvp.listener.FilterListener
import com.wolcano.musicplayer.music.mvp.listener.PlaylistListener
import com.wolcano.musicplayer.music.mvp.models.Playlist
import com.wolcano.musicplayer.music.mvp.models.Song
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.SongPresenter
import com.wolcano.musicplayer.music.mvp.view.SongView
import com.wolcano.musicplayer.music.ui.activity.MainActivity
import com.wolcano.musicplayer.music.ui.adapter.SongAdapter
import com.wolcano.musicplayer.music.ui.dialog.Dialogs.addPlaylistDialog
import com.wolcano.musicplayer.music.ui.fragment.FragmentLibrary
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragmentInject
import com.wolcano.musicplayer.music.ui.helper.ToolbarContentTintHelper
import com.wolcano.musicplayer.music.utils.ColorUtils
import com.wolcano.musicplayer.music.utils.SongUtils
import com.wolcano.musicplayer.music.utils.Utils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class FragmentSongs : BaseFragmentInject(), SongView, FilterListener, OnOffsetChangedListener,
    PlaylistListener {

    private var adapter: SongAdapter? = null
    private var searchCount = 0
    private var disposable: Disposable? = null

    private lateinit var binding: FragmentSongsBinding
    private var text: String? = null

    var songPresenter: SongPresenter? = null @Inject set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_songs, container, false)
        setHasOptionsMenu(true)

        Utils.setUpFastScrollRecyclerViewColor(
            binding.recyclerview, Utils.getAccentColor(
                requireContext()
            )
        )
        binding.recyclerview.layoutManager = LinearLayoutManager(activity)
        binding.recyclerview.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        songPresenter?.songs

        return binding.root
    }

    private fun getLibraryFragment(): FragmentLibrary? {
        return parentFragment as FragmentLibrary?
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getLibraryFragment()?.addOnAppBarOffsetChangedListener(this)
    }

    override fun setSongList(songList: ArrayList<Song>?) {
        adapter = SongAdapter(
            (activity as MainActivity?)!!,
            songList!!,
            this, this
        )
        binding.recyclerview.adapter = adapter
        runLayoutAnimation(binding.recyclerview)
    }

    private fun runLayoutAnimation(recyclerView: RecyclerView) {
        val context = recyclerView.context
        val controller =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
        recyclerView.layoutAnimation = controller
        recyclerView.adapter?.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }

    fun controlIfEmpty() {
        binding.empty.setText(R.string.no_song)
        binding.empty.visibility =
            if (adapter == null || adapter!!.itemCount == 0) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_library_songs, menu)
        val sItem = menu.findItem(R.id.search)
        val toolbar: Toolbar = activity!!.findViewById(R.id.toolbar)
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(
            requireActivity(),
            toolbar,
            menu,
            ColorUtils.getToolbarBackgroundColor(toolbar)
        )
        val searchView = sItem.actionView as SearchView
        searchView.queryHint = getString(R.string.queryhintyerel)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(_text: String): Boolean {
                text = _text
                if (adapter != null) {
                    if (adapter!!.filter != null) {
                        adapter!!.filter!!.filter(_text)
                    }
                }
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                text = query
                searchCount = 1
                searchView.clearFocus()
                return false
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    fun handleOptionsMenu() {
        val toolbar = (activity as MainActivity?)!!.getToolbar()
        (activity as MainActivity?)?.setSupportActionBar(toolbar)
        setHasOptionsMenu(true)
    }

    override fun setFastScrollIndexer(isShown: Boolean) {
        if (isShown) {
            binding.recyclerview.setThumbEnabled(true)
        } else {
            binding.recyclerview.setThumbEnabled(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        getLibraryFragment()!!.removeOnAppBarOffsetChangedListener(this)
        if (disposable != null && !disposable!!.isDisposed) {
            disposable!!.dispose()
        }

        DisposableManager.dispose()
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        view?.setPadding(
            view!!.paddingLeft,
            view!!.paddingTop,
            view!!.paddingRight,
            getLibraryFragment()?.getTotalAppBarScrollingRange()!! + verticalOffset
        )
    }

    override fun handlePlaylistDialog(song: Song?) {
        disposable =
            Observable.fromCallable<List<Playlist>> { SongUtils.scanPlaylist(context) }
                .subscribeOn(
                    Schedulers.io()
                ).observeOn(AndroidSchedulers.mainThread()).subscribe { playlists ->
                    addPlaylistDialog(
                        context!!,
                        song,
                        playlists
                    )
                }
    }

    override fun setupComponent(applicationComponent: ApplicationComponent?) {
        val sort = MediaStore.Audio.Media.DEFAULT_SORT_ORDER

        val songsComponent = DaggerSongComponent.builder()
            .applicationComponent(applicationComponent)
            .songModule(SongModule(this, this, requireActivity(), sort, SongInteractorImpl()))
            .build()
        songsComponent.inject(this)
    }

}