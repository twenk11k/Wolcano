package com.wolcano.musicplayer.music.ui.fragment.recentlyadded

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.data.model.Song
import com.wolcano.musicplayer.music.databinding.FragmentBaseSongBinding
import com.wolcano.musicplayer.music.listener.PlaylistListener
import com.wolcano.musicplayer.music.ui.adapter.RecentlyAddedAdapter
import com.wolcano.musicplayer.music.ui.dialog.Dialogs
import com.wolcano.musicplayer.music.ui.dialog.SleepTimerDialog
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragment
import com.wolcano.musicplayer.music.ui.helper.TintHelper
import com.wolcano.musicplayer.music.ui.helper.ToolbarContentTintHelper
import com.wolcano.musicplayer.music.utils.ColorUtils
import com.wolcano.musicplayer.music.utils.PermissionUtils
import com.wolcano.musicplayer.music.utils.ToastUtils
import com.wolcano.musicplayer.music.utils.Utils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecentlyFragment : BaseFragment(), PlaylistListener {

    private var adapter: RecentlyAddedAdapter? = null
    private lateinit var binding: FragmentBaseSongBinding

    val viewModel: RecentlyViewModel by viewModels()

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
        binding.recyclerview.layoutManager = LinearLayoutManager(requireActivity())
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
            SleepTimerDialog().show(childFragmentManager, "SET_SLEEP_TIMER")
        }
        return super.onOptionsItemSelected(item)
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

    private fun setStatusBarAndToolbar() {
        val color = Utils.getPrimaryColor(requireContext())

        setStatusBarColor(color, binding.statusBarCustom)
        (requireActivity() as AppCompatActivity?)?.setSupportActionBar(binding.toolbar)
        binding.toolbar.setBackgroundColor(color)
        if (Utils.isColorLight(color)) {
            binding.toolbar.setTitleTextColor(Color.BLACK)
        } else {
            binding.toolbar.setTitleTextColor(Color.WHITE)
        }
        val ab = (requireActivity() as AppCompatActivity?)?.supportActionBar
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
        val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
        recyclerView.layoutAnimation = controller
        recyclerView.adapter?.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }

    fun controlIfEmpty() {
        binding.empty.setText(R.string.no_song)
        binding.empty.visibility = if (adapter == null || adapter?.itemCount == 0) View.VISIBLE else View.GONE
    }

    override fun handlePlaylistDialog(song: Song?) {
        viewModel.retrievePlaylists()
        viewModel.playlistsLiveData.observe(viewLifecycleOwner, {
            if (it != null) {
                Dialogs.addPlaylistDialog(
                        requireContext(),
                        song,
                        it
                )
            }
        })
    }

    private fun setSongList(songList: ArrayList<Song>?) {
        if (songList!!.size >= 60) {
            songList.subList(60, songList.size).clear()
        }
        if (songList.size <= 30) {
            binding.recyclerview.setThumbEnabled(false)
        } else {
            binding.recyclerview.setThumbEnabled(true)
        }
        adapter = RecentlyAddedAdapter(requireActivity(), songList, this@RecentlyFragment)
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

}