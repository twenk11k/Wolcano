package com.wolcano.musicplayer.music.ui.fragment.library.genre

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
import com.wolcano.musicplayer.music.ui.adapter.RecentlyAddedAdapter
import com.wolcano.musicplayer.music.ui.dialog.Dialogs
import com.wolcano.musicplayer.music.ui.dialog.SleepTimerDialog
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragment
import com.wolcano.musicplayer.music.ui.helper.ToolbarContentTintHelper
import com.wolcano.musicplayer.music.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GenreDetailFragment : BaseFragment(), PlaylistListener {

    private var adapter: RecentlyAddedAdapter? = null
    private var genreId: Long = -1
    private var genreName: String? = null
    private var primaryColor = -1

    private lateinit var binding: FragmentBaseSongBinding

    val viewModel: GenreDetailViewModel by viewModels()

    companion object {
        fun newInstance(id: Long, name: String?): GenreDetailFragment {
            val fragment = GenreDetailFragment()
            val args = Bundle()
            args.putLong(Constants.GENRE_ID, id)
            args.putString(Constants.GENRE_NAME, name)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (arguments != null) {
            genreId = requireArguments().getLong(Constants.GENRE_ID)
            genreName = requireArguments().getString(Constants.GENRE_NAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_base_song, container, false)

        primaryColor = Utils.getPrimaryColor(requireContext())
        setStatusBarColor(primaryColor, binding.statusBarCustom)

        (activity as AppCompatActivity?)?.setSupportActionBar(binding.toolbar)

        binding.toolbar.setBackgroundColor(primaryColor)
        binding.toolbar.title = genreName

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

        binding.recyclerview.adapter = adapter
        Utils.setUpFastScrollRecyclerViewColor(
            binding.recyclerview, Utils.getAccentColor(
                requireContext()
            )
        )
        binding.recyclerview.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerview.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
        setupToolbar()
        handlePermissionsAndRetrieveSongsForGenre()
        handleViewModel()
    }

    private fun handlePermissionsAndRetrieveSongsForGenre() {
        PermissionUtils.with(requireActivity())
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).result(object : PermissionUtils.PermInterface {
                override fun onPermGranted() {
                    viewModel.retrieveGenreSongs(genreId)
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
        recyclerView.adapter?.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }

    fun controlIfEmpty() {
        binding.empty.setText(R.string.no_song)
        binding.empty.visibility =
            if (adapter == null || adapter?.itemCount == 0) View.VISIBLE else View.GONE
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        val ab = (requireActivity() as AppCompatActivity?)?.supportActionBar
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
        adapter = RecentlyAddedAdapter(
            (requireActivity() as MainActivity?)!!,
            songList,
            this@GenreDetailFragment
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