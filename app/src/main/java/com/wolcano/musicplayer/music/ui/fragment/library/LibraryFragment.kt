package com.wolcano.musicplayer.music.ui.fragment.library

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.content.managers.DisposableManager
import com.wolcano.musicplayer.music.databinding.FragmentLibraryBinding
import com.wolcano.musicplayer.music.ui.adapter.statepager.LibraryFragmentPagerAdapter
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragment
import com.wolcano.musicplayer.music.ui.fragment.library.album.AlbumFragment
import com.wolcano.musicplayer.music.ui.fragment.library.artist.ArtistFragment
import com.wolcano.musicplayer.music.ui.fragment.library.genre.GenreFragment
import com.wolcano.musicplayer.music.ui.fragment.library.song.SongFragment
import com.wolcano.musicplayer.music.ui.helper.TintHelper
import com.wolcano.musicplayer.music.ui.helper.ToolbarContentTintHelper
import com.wolcano.musicplayer.music.utils.ColorUtils
import com.wolcano.musicplayer.music.utils.Utils
import com.wolcano.musicplayer.music.utils.Utils.getPrimaryColor
import com.wolcano.musicplayer.music.utils.Utils.isColorLight
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryFragment : BaseFragment() {

    private var isItHidden = false
    private lateinit var binding: FragmentLibraryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_library, container, false)

        setHasOptionsMenu(true)
        val color = getPrimaryColor(requireContext())
        setStatusbarColor(color, binding.statusBarCustom)

        (activity as AppCompatActivity?)?.setSupportActionBar(binding.toolbar)

        binding.toolbar.setBackgroundColor(color)
        if (isColorLight(color)) {
            binding.toolbar.setTitleTextColor(Color.BLACK)
        } else {
            binding.toolbar.setTitleTextColor(Color.WHITE)
        }

        val ab = (activity as AppCompatActivity?)?.supportActionBar
        ab?.setTitle(R.string.library)
        if (binding.toolbar.navigationIcon != null) {
            binding.toolbar.navigationIcon = TintHelper.createTintedDrawable(
                binding.toolbar.navigationIcon, ToolbarContentTintHelper.toolbarContentColor(
                    requireContext(), color
                )
            )
        }

        val adapter = LibraryFragmentPagerAdapter(
            requireContext(),
            childFragmentManager
        )
        binding.viewpager.adapter = adapter
        binding.viewpager.isSaveFromParentEnabled = false
        binding.viewpager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    val fragment = adapter.instantiateItem(binding.viewpager, 0) as Fragment
                    if (fragment is SongFragment) {
                        if (isItHidden) fragment.handleOptionsMenu()
                    } else if (fragment is ArtistFragment) {
                        if (isItHidden) fragment.handleOptionsMenu()
                    } else if (fragment is AlbumFragment) {
                        if (isItHidden) fragment.handleOptionsMenu()
                    } else if (fragment is GenreFragment) {
                        if (isItHidden) fragment.handleOptionsMenu()
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

        binding.viewpager.removeAllViews()
        binding.tabLayout.setupWithViewPager(binding.viewpager)
        binding.tabLayout.setBackgroundColor(color)
        val normalColor = ToolbarContentTintHelper.toolbarSubtitleColor(requireActivity(), color)
        val selectedColor = ToolbarContentTintHelper.toolbarTitleColor(requireActivity(), color)
        binding.tabLayout.setTabTextColors(normalColor, selectedColor)
        binding.tabLayout.setSelectedTabIndicatorColor(Utils.getAccentColor(requireActivity()))

        return binding.root
    }

    fun addOnAppBarOffsetChangedListener(onOffsetChangedListener: OnOffsetChangedListener?) {
        binding.appbar.addOnOffsetChangedListener(onOffsetChangedListener)
    }

    fun removeOnAppBarOffsetChangedListener(onOffsetChangedListener: OnOffsetChangedListener?) {
        binding.appbar.removeOnOffsetChangedListener(onOffsetChangedListener)
    }

    fun getTotalAppBarScrollingRange(): Int {
        return binding.appbar.totalScrollRange
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.viewpager.removeAllViews()
        binding.viewpager.destroyDrawingCache()
        DisposableManager.dispose()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) {
            isItHidden = true
        }
        super.onHiddenChanged(hidden)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(
            requireActivity(),
            binding.toolbar,
            menu,
            ColorUtils.getToolbarBackgroundColor(binding.toolbar)
        )
    }

}