package com.wolcano.musicplayer.music.ui.fragment.library.genre

import android.Manifest
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.data.model.Genre
import com.wolcano.musicplayer.music.databinding.FragmentInnerAlbumBinding
import com.wolcano.musicplayer.music.ui.activity.main.MainActivity
import com.wolcano.musicplayer.music.ui.adapter.GenreAdapter
import com.wolcano.musicplayer.music.ui.dialog.SleepTimerDialog
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragment
import com.wolcano.musicplayer.music.ui.fragment.library.LibraryFragment
import com.wolcano.musicplayer.music.ui.helper.ToolbarContentTintHelper
import com.wolcano.musicplayer.music.utils.ColorUtils
import com.wolcano.musicplayer.music.utils.PermissionUtils
import com.wolcano.musicplayer.music.utils.ToastUtils
import com.wolcano.musicplayer.music.utils.Utils.getAccentColor
import com.wolcano.musicplayer.music.utils.Utils.setUpFastScrollRecyclerViewColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GenreFragment : BaseFragment(), OnOffsetChangedListener {

    private var adapter: GenreAdapter? = null
    private lateinit var binding: FragmentInnerAlbumBinding

    val viewModel: GenreViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_inner_album, container, false)
        setHasOptionsMenu(true)
        setupViews()
        handlePermissionsAndRetrieveGenres()
        handleViewModel()
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sleeptimer, menu)
        val toolbar: Toolbar = requireActivity().findViewById(R.id.toolbar)
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(
            requireActivity(),
            toolbar,
            menu,
            ColorUtils.getToolbarBackgroundColor(toolbar)
        )

        super.onCreateOptionsMenu(menu, inflater)
    }

    fun handleOptionsMenu() {
        val toolbar = (requireActivity() as MainActivity?)?.getToolbar()
        (requireActivity() as MainActivity?)?.setSupportActionBar(toolbar)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_sleeptimer) {
            SleepTimerDialog().show(childFragmentManager, "SET_SLEEP_TIMER")
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupViews() {
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
    }

    private fun handlePermissionsAndRetrieveGenres() {
        PermissionUtils.with(requireActivity())
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).result(object : PermissionUtils.PermInterface {
                override fun onPermGranted() {
                    viewModel.retrieveGenres()
                }

                override fun onPermUnapproved() {
                    ToastUtils.show(requireContext(), R.string.no_perm_storage)
                }
            }).requestPermissions()
    }

    private fun handleViewModel() {
        viewModel.genresLiveData.observe(viewLifecycleOwner, {
            if (it != null)
                setGenreList(it as ArrayList<Genre>)
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
        binding.txtEmpty.setText(R.string.no_genre)
        binding.txtEmpty.visibility =
            if (adapter == null || adapter?.itemCount == 0) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        getLibraryFragment()?.removeOnAppBarOffsetChangedListener(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getLibraryFragment()?.addOnAppBarOffsetChangedListener(this)
    }

    private fun getLibraryFragment(): LibraryFragment? {
        return parentFragment as LibraryFragment?
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        view?.setPadding(
            requireView().paddingLeft,
            requireView().paddingTop,
            requireView().paddingRight,
            getLibraryFragment()!!.getTotalAppBarScrollingRange() + verticalOffset
        )
    }

    private fun setGenreList(genreList: ArrayList<Genre>?) {
        if (genreList!!.size <= 30) {
            binding.recyclerview.setThumbEnabled(false)
        } else {
            binding.recyclerview.setThumbEnabled(true)
        }
        adapter = GenreAdapter(requireActivity(), genreList)
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