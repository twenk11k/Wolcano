package com.wolcano.musicplayer.music.ui.fragment.library

import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.databinding.FragmentInnerAlbumBinding
import com.wolcano.musicplayer.music.di.component.ApplicationComponent
import com.wolcano.musicplayer.music.di.component.DaggerGenreComponent
import com.wolcano.musicplayer.music.di.module.GenreModule
import com.wolcano.musicplayer.music.mvp.DisposableManager
import com.wolcano.musicplayer.music.mvp.interactor.GenreInteractorImpl
import com.wolcano.musicplayer.music.mvp.models.Genre
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.GenrePresenter
import com.wolcano.musicplayer.music.mvp.view.GenreView
import com.wolcano.musicplayer.music.ui.activity.MainActivity
import com.wolcano.musicplayer.music.ui.adapter.GenreAdapter
import com.wolcano.musicplayer.music.ui.dialog.SleepTimerDialog
import com.wolcano.musicplayer.music.ui.fragment.FragmentLibrary
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragmentInject
import com.wolcano.musicplayer.music.ui.helper.ToolbarContentTintHelper
import com.wolcano.musicplayer.music.utils.ColorUtils
import com.wolcano.musicplayer.music.utils.Utils.getAccentColor
import com.wolcano.musicplayer.music.utils.Utils.setUpFastScrollRecyclerViewColor
import javax.inject.Inject

class FragmentGenres : BaseFragmentInject(), GenreView, OnOffsetChangedListener {

    private var adapter: GenreAdapter? = null
    private lateinit var binding: FragmentInnerAlbumBinding

    var genrePresenter: GenrePresenter? = null @Inject set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_inner_album, container, false)
        setHasOptionsMenu(true)
        setupViews()
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sleeptimer, menu)
        val toolbar: Toolbar = activity!!.findViewById(R.id.toolbar)
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(
            requireActivity(),
            toolbar,
            menu,
            ColorUtils.getToolbarBackgroundColor(toolbar)
        )

        super.onCreateOptionsMenu(menu, inflater)
    }

    fun handleOptionsMenu() {
        val toolbar = (activity as MainActivity?)!!.getToolbar()
        (activity as MainActivity?)!!.setSupportActionBar(toolbar)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_sleeptimer) {
            SleepTimerDialog().show(fragmentManager!!, "SET_SLEEP_TIMER")
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
        genrePresenter?.getGenres()
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
        if (binding.empty != null) {
            binding.empty.setText(R.string.no_genre)
            binding.empty.visibility =
                if (adapter == null || adapter?.itemCount == 0) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        getLibraryFragment()?.removeOnAppBarOffsetChangedListener(this)
        DisposableManager.dispose()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getLibraryFragment()?.addOnAppBarOffsetChangedListener(this)
    }

    private fun getLibraryFragment(): FragmentLibrary? {
        return parentFragment as FragmentLibrary?
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        view?.setPadding(
            view!!.paddingLeft,
            view!!.paddingTop,
            view!!.paddingRight,
            getLibraryFragment()!!.getTotalAppBarScrollingRange() + verticalOffset
        )
    }

    override fun setGenreList(genreList: MutableList<Genre>?) {
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

    override fun setupComponent(applicationComponent: ApplicationComponent?) {
        val sort = MediaStore.Audio.Media.DEFAULT_SORT_ORDER

        val genreComponent = DaggerGenreComponent.builder()
            .applicationComponent(applicationComponent)
            .genreModule(GenreModule(this, this, activity, sort, GenreInteractorImpl()))
            .build()

        genreComponent.inject(this)
    }

}