package com.wolcano.musicplayer.music.ui.fragment.online

import android.Manifest
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.databinding.FragmentOnlineBinding
import com.wolcano.musicplayer.music.listener.SetSearchQuery
import com.wolcano.musicplayer.music.data.model.Song
import com.wolcano.musicplayer.music.data.model.SongOnline
import com.wolcano.musicplayer.music.provider.RemotePlay
import com.wolcano.musicplayer.music.ui.adapter.OnlineAdapter
import com.wolcano.musicplayer.music.ui.adapter.OnlineAdapterClickListener
import com.wolcano.musicplayer.music.ui.dialog.Dialogs
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragment
import com.wolcano.musicplayer.music.ui.helper.TintHelper
import com.wolcano.musicplayer.music.ui.helper.ToolbarContentTintHelper
import com.wolcano.musicplayer.music.utils.*
import com.wolcano.musicplayer.music.utils.Utils.getAccentColor
import com.wolcano.musicplayer.music.utils.Utils.getAutoSearch
import com.wolcano.musicplayer.music.utils.Utils.getLastSearch
import com.wolcano.musicplayer.music.utils.Utils.getPrimaryColor
import com.wolcano.musicplayer.music.utils.Utils.isColorLight
import com.wolcano.musicplayer.music.utils.Utils.setLastSearch
import com.wolcano.musicplayer.music.utils.Utils.setUpFastScrollRecyclerViewColor
import com.wolcano.musicplayer.music.widgets.MaterialSearchView
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class OnlineFragment : BaseFragment(), SetSearchQuery, OnlineAdapterClickListener {

    private var color = 0
    private var colorTint = Color.WHITE
    private lateinit var binding: FragmentOnlineBinding

    val viewModel: OnlineViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnlineBinding.inflate(inflater, container, false)
        setBindings()
        setHasOptionsMenu(true)

        color = getPrimaryColor(requireContext())
        binding.materialSearchView.bringToFront()

        setStatusbarColor(color, binding.statusBarCustom)
        setUpFastScrollRecyclerViewColor(
            binding.recyclerView, getAccentColor(
                requireContext()
            )
        )
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        (requireActivity() as AppCompatActivity?)?.setSupportActionBar(binding.toolbar)
        binding.toolbar.setBackgroundColor(color)
        if (isColorLight(color)) {
            binding.toolbar.setTitleTextColor(Color.BLACK)
        } else {
            binding.toolbar.setTitleTextColor(Color.WHITE)
        }
        val ab = (requireActivity() as AppCompatActivity?)?.supportActionBar
        ab?.setTitle(R.string.onlineplayer)

        binding.toolbar.navigationIcon = TintHelper.createTintedDrawable(
            binding.toolbar.navigationIcon, ToolbarContentTintHelper.toolbarContentColor(
                requireContext(), color
            )
        )

        setSearchView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.lastSearches.observe(viewLifecycleOwner) {
            it?.let {
                val list = ArrayList<String>()
                for (curr in it) {
                    list.add(curr.searchText)
                }
                binding.materialSearchView.setSuggestions(
                    list,
                    true,
                    this@OnlineFragment,
                    colorTint
                )

                handleAutoSearch()
            }
        }
    }

    private fun handleAutoSearch() {
        if (getAutoSearch(requireContext())) {
            getLastSearch(requireContext())?.let { it ->
                if (it.isNotEmpty())
                    submitSearch(it, true)
            }
        }
    }

    private fun setBindings() {
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
            adapter = OnlineAdapter(this@OnlineFragment)
        }
    }

    private fun setSearchView() {
        binding.materialSearchView.setSubmitOnClick(true)
        binding.materialSearchView.setVoiceSearch(true)
        binding.progressBar.indeterminateTintList = ColorStateList.valueOf(
            getAccentColor(
                requireContext()
            )
        )
        binding.materialSearchView.setHint(getString(R.string.searchhint))

        binding.materialSearchView.setSuggestionIcon(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_history_white_24dp
            )
        )
        binding.materialSearchView.setSuggestionRemove(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.baseline_close_black_36
            )
        )
        binding.materialSearchView.setSuggestionSend(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_call_made_white_24dp
            )
        )
        val colorPrimary = getPrimaryColor(requireContext())
        if (isColorLight(colorPrimary)) {
            colorTint = Color.BLACK
            binding.materialSearchView.setSuggestionIconTint(colorTint)
            binding.materialSearchView.setSuggestionRemoveTint(colorTint)
            binding.materialSearchView.setSuggestionSendTint(colorTint)
            binding.materialSearchView.setCloseIconTint(colorTint)
            binding.materialSearchView.setBackIconTint(colorTint)
            binding.materialSearchView.setTextColor(colorTint)
            binding.materialSearchView.setHintTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black_u6
                )
            )
        } else {
            colorTint = Color.WHITE
            binding.materialSearchView.setSuggestionIconTint(colorTint)
            binding.materialSearchView.setSuggestionRemoveTint(colorTint)
            binding.materialSearchView.setSuggestionSendTint(colorTint)
            binding.materialSearchView.setCloseIconTint(colorTint)
            binding.materialSearchView.setBackIconTint(colorTint)
            binding.materialSearchView.setTextColor(colorTint)
            binding.materialSearchView.setHintTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.grey0
                )
            )
        }
        binding.materialSearchView.setBackgroundColor(colorPrimary)
        val colorDrawable = ColorDrawable(colorPrimary)
        binding.materialSearchView.setSuggestionBackground(colorDrawable)

        binding.materialSearchView.setOnQueryTextListener(object :
            MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                submitSearch(query, false)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    private fun submitSearch(query: String, isAutoSearch: Boolean) {
        viewModel.setQuery(query, isAutoSearch)
        binding.toolbar.title = query
        if (!isAutoSearch) {
            binding.materialSearchView.addSuggestion(query)
            setLastSearch(requireContext(), query)
        }
    }

    private fun removeSuggestion(text: String) {
        viewModel.removeSearchHistory(text)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_online, menu)
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(
            requireActivity(), binding.toolbar, menu, ColorUtils.getToolbarBackgroundColor(
                binding.toolbar
            )
        )
        if (isColorLight(color)) {
            binding.materialSearchView.setBackgroundColor(ColorUtils.shiftColor(color, 0.7f))
            binding.materialSearchView.setSuggestionBackgroundColor(
                ColorUtils.shiftColor(
                    color,
                    0.7f
                )
            )
        } else {
            binding.materialSearchView.setBackgroundColor(color)
            binding.materialSearchView.setSuggestionBackgroundColor(color)
        }
        val searchItem = menu.findItem(R.id.action_searchM)
        binding.materialSearchView.setMenuItem(searchItem)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onSearchQuery(position: Int, isFirst: Boolean) {
        binding.materialSearchView.setQuery(
            binding.materialSearchView.getListPosSend(position),
            isFirst
        )
    }

    override fun onRemoveSuggestion(position: Int, whichList: Int) {
        removeSuggestion(binding.materialSearchView.getListPosSend(position))
    }

    fun closeSearch() {
        binding.materialSearchView.closeSearch()
    }

    fun isSearchOpen(): Boolean {
        return binding.materialSearchView.isSearchOpen
    }

    override fun onDestroyView() {
        if (binding.materialSearchView.isSearchOpen) {
            binding.materialSearchView.closeSearch()
        }
        super.onDestroyView()
    }

    override fun performDownload(song: SongOnline) {
        PermissionUtils.with(
            requireActivity()
        )
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .result(object : PermissionUtils.PermInterface {
                override fun onPermGranted() {
                    SongUtils.downPerform(requireContext(), song)
                }

                override fun onPermUnapproved() {
                    ToastUtils.show(
                        requireContext(),
                        R.string.no_perm_save_file
                    )
                }
            })
            .requestPermissions()
    }

    override fun copySongInfo(song: SongOnline) {
        Dialogs.copyDialog(
            requireActivity(),
            song
        )
    }

    override fun playSongOnline(items: List<SongOnline>, position: Int) {
        Utils.hideKeyboard(requireActivity())
        val handler = Handler()
        handler.postDelayed({
            object : PlayModelLocal(requireActivity(), items) {
                override fun onPrepare() {}
                override fun onTaskDone(songList: List<Song>?) {
                    RemotePlay.playAdd(
                        requireContext(),
                        songList!!,
                        songList[position]
                    )
                }

                override fun onTaskFail(e: Exception?) {
                    ToastUtils.show(requireContext(), R.string.cannot_play)
                }
            }.onTask()
        }, 100)
    }

}