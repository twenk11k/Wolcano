package com.wolcano.musicplayer.music.ui.fragment.playlist

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
import com.wolcano.musicplayer.music.data.model.Playlist
import com.wolcano.musicplayer.music.databinding.FragmentBaseSongBinding
import com.wolcano.musicplayer.music.ui.adapter.PlaylistAdapter
import com.wolcano.musicplayer.music.ui.dialog.SleepTimerDialog
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragment
import com.wolcano.musicplayer.music.ui.helper.TintHelper
import com.wolcano.musicplayer.music.ui.helper.ToolbarContentTintHelper
import com.wolcano.musicplayer.music.utils.ColorUtils
import com.wolcano.musicplayer.music.utils.PermissionUtils
import com.wolcano.musicplayer.music.utils.ToastUtils
import com.wolcano.musicplayer.music.utils.Utils
import com.wolcano.musicplayer.music.utils.Utils.getAccentColor
import com.wolcano.musicplayer.music.utils.Utils.getPrimaryColor
import com.wolcano.musicplayer.music.utils.Utils.setUpFastScrollRecyclerViewColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistFragment : BaseFragment() {

    private var adapter: PlaylistAdapter? = null
    private lateinit var binding: FragmentBaseSongBinding

    val viewModel: PlaylistViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_base_song, container, false)
        setHasOptionsMenu(true)
        setStatusBarAndToolbar()
        setUpFastScrollRecyclerViewColor(
            binding.recyclerview, getAccentColor(
                requireContext()
            )
        )
        binding.recyclerview.layoutManager = LinearLayoutManager(activity)
        binding.recyclerview.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        handlePermissionsAndRetrievePlaylists()
        handleViewModel()
        return binding.root
    }

    private fun handlePermissionsAndRetrievePlaylists() {
        PermissionUtils.with(requireActivity())
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).result(object : PermissionUtils.PermInterface {
                override fun onPermGranted() {
                    viewModel.retrievePlaylists()
                }

                override fun onPermUnapproved() {
                    ToastUtils.show(requireContext(), R.string.no_perm_storage)
                }
            }).requestPermissions()
    }

    private fun handleViewModel() {
        viewModel.playlistsLiveData.observe(viewLifecycleOwner, {
            if (it != null)
                setPlaylistList(it as ArrayList<Playlist>)
        })
    }

    private fun setStatusBarAndToolbar() {
        val color = getPrimaryColor(requireContext())

        setStatusBarColor(color, binding.statusBarCustom)

        (requireActivity() as AppCompatActivity?)?.setSupportActionBar(binding.toolbar)
        binding.toolbar.setBackgroundColor(color)
        if (Utils.isColorLight(color)) {
            binding.toolbar.setTitleTextColor(Color.BLACK)
        } else {
            binding.toolbar.setTitleTextColor(Color.WHITE)
        }
        (requireActivity() as AppCompatActivity?)?.supportActionBar?.setTitle(R.string.playlists)

        if (binding.toolbar.navigationIcon != null) {
            binding.toolbar.navigationIcon = TintHelper.createTintedDrawable(
                binding.toolbar.navigationIcon, ToolbarContentTintHelper.toolbarContentColor(
                    requireContext(), color
                )
            )
        }
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

    private fun runLayoutAnimation(recyclerView: RecyclerView) {
        val context = recyclerView.context
        val controller =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
        recyclerView.layoutAnimation = controller
        recyclerView.adapter?.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }

    fun controlIfEmpty() {
        binding.empty.setText(R.string.no_playlist)
        binding.empty.visibility =
            if (adapter == null || adapter?.itemCount == 0) View.VISIBLE else View.GONE
    }

    private fun setPlaylistList(playlistList: MutableList<Playlist>?) {
        if (playlistList!!.size <= 30) {
            binding.recyclerview.setThumbEnabled(false)
        } else {
            binding.recyclerview.setThumbEnabled(true)
        }
        adapter = PlaylistAdapter(requireActivity(), playlistList)
        binding.recyclerview.adapter = adapter
        runLayoutAnimation(binding.recyclerview)
        controlIfEmpty()
        adapter?.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                controlIfEmpty()
            }
        })
    }

}