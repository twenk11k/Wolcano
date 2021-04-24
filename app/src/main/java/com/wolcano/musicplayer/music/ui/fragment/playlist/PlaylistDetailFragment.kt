package com.wolcano.musicplayer.music.ui.fragment.playlist

import android.Manifest
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
import com.wolcano.musicplayer.music.ui.activity.main.MainActivity
import com.wolcano.musicplayer.music.ui.adapter.detail.PlaylistSongAdapter
import com.wolcano.musicplayer.music.ui.dialog.Dialogs
import com.wolcano.musicplayer.music.ui.dialog.SleepTimerDialog
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragment
import com.wolcano.musicplayer.music.ui.helper.ToolbarContentTintHelper
import com.wolcano.musicplayer.music.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistDetailFragment : BaseFragment(), PlaylistListener {

    private var adapter: PlaylistSongAdapter? = null
    private var playlistId: Long = -1
    private var playlistName: String? = null
    private var primaryColor = -1
    private var accentColor = -1

    private lateinit var binding: FragmentBaseSongBinding

    val viewModel: PlaylistDetailViewModel by viewModels()

    companion object {
        fun newInstance(id: Long, name: String?): PlaylistDetailFragment {
            val fragment = PlaylistDetailFragment()
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
            playlistId = requireArguments().getLong(Constants.PLAYLIST_ID)
            playlistName = requireArguments().getString(Constants.PLAYLIST_NAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_base_song, container, false)
        primaryColor = Utils.getPrimaryColor(requireContext())
        accentColor = Utils.getAccentColor(requireContext(  ))
        setStatusBarColor(primaryColor, binding.statusBarCustom)

        (activity as AppCompatActivity?)?.setSupportActionBar(binding.toolbar)

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
            SleepTimerDialog().show(childFragmentManager, "SET_SLEEP_TIMER")
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
        binding.recyclerview.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerview.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )

        binding.recyclerview.adapter = adapter

        handlePermissionsAndRetrievePlaylistSongs()
        handleViewModel()
        setupToolbar()
    }


    private fun handlePermissionsAndRetrievePlaylistSongs() {
        PermissionUtils.with(requireActivity())
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).result(object : PermissionUtils.PermInterface {
                override fun onPermGranted() {
                    viewModel.retrievePlaylistSongs(playlistId)
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

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        val ab = (requireActivity() as AppCompatActivity?)!!.supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { requireActivity().supportFragmentManager.popBackStack() }
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
        if (songList!!.size <= 30) {
            binding.recyclerview.setThumbEnabled(false)
        } else {
            binding.recyclerview.setThumbEnabled(true)
        }
        adapter = PlaylistSongAdapter(
            (requireActivity() as MainActivity?)!!,
            songList, playlistId, this@PlaylistDetailFragment
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
    
}
