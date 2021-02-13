package com.wolcano.musicplayer.music.ui.fragment.library.song

import android.Manifest
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.databinding.FragmentSongsBinding
import com.wolcano.musicplayer.music.model.Song
import com.wolcano.musicplayer.music.mvp.listener.FilterListener
import com.wolcano.musicplayer.music.mvp.listener.PlaylistListener
import com.wolcano.musicplayer.music.ui.activity.main.MainActivity
import com.wolcano.musicplayer.music.ui.adapter.SongAdapter
import com.wolcano.musicplayer.music.ui.dialog.Dialogs.addPlaylistDialog
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragment
import com.wolcano.musicplayer.music.ui.fragment.library.LibraryFragment
import com.wolcano.musicplayer.music.ui.helper.ToolbarContentTintHelper
import com.wolcano.musicplayer.music.utils.ColorUtils
import com.wolcano.musicplayer.music.utils.PermissionUtils
import com.wolcano.musicplayer.music.utils.ToastUtils
import com.wolcano.musicplayer.music.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SongFragment : BaseFragment(), FilterListener, OnOffsetChangedListener,
    PlaylistListener {

    private var adapter: SongAdapter? = null

    private lateinit var binding: FragmentSongsBinding
    private var text: String? = null

    val viewModel: SongViewModel by viewModels()

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
        handlePermissionsAndRetrieveSongs()
        handleViewModel()

        return binding.root
    }

    private fun handlePermissionsAndRetrieveSongs() {
        PermissionUtils.with(requireActivity())
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).result(object : PermissionUtils.PermInterface {
                override fun onPermGranted() {
                    viewModel.retrieveSongs()
                }

                override fun onPermUnapproved() {
                    ToastUtils.show(requireContext(), R.string.no_perm_storage)
                }
            }).requestPermissions()
    }

    private fun handleViewModel() {
        viewModel.songsLiveData.observe(viewLifecycleOwner, {
            if (it != null)
                setSongList(it as ArrayList<Song>)
        })
    }

    private fun getLibraryFragment(): LibraryFragment? {
        return parentFragment as LibraryFragment?
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getLibraryFragment()?.addOnAppBarOffsetChangedListener(this)
    }

    private fun setSongList(songList: ArrayList<Song>?) {
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
        val toolbar: Toolbar = requireActivity().findViewById(R.id.toolbar)
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
        getLibraryFragment()?.removeOnAppBarOffsetChangedListener(this)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        view?.setPadding(
            requireView().paddingLeft,
            requireView().paddingTop,
            requireView().paddingRight,
            getLibraryFragment()?.getTotalAppBarScrollingRange()!! + verticalOffset
        )
    }

    override fun handlePlaylistDialog(song: Song?) {
        viewModel.retrievePlaylists()
        viewModel.playlistsLiveData.observe(viewLifecycleOwner, {
            if (it != null) {
                addPlaylistDialog(
                    requireContext(),
                    song,
                    it
                )
            }
        })
    }

}