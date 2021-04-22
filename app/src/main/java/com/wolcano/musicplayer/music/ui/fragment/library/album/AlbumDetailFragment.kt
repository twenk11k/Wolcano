package com.wolcano.musicplayer.music.ui.fragment.library.album

import android.Manifest
import android.content.ContentUris
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.constants.Constants
import com.wolcano.musicplayer.music.databinding.FragmentAlbumDetailBinding
import com.wolcano.musicplayer.music.listener.PlaylistListener
import com.wolcano.musicplayer.music.data.model.Song
import com.wolcano.musicplayer.music.provider.RemotePlay
import com.wolcano.musicplayer.music.ui.adapter.detail.AlbumSongAdapter
import com.wolcano.musicplayer.music.ui.dialog.Dialogs
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragment
import com.wolcano.musicplayer.music.ui.helper.ToolbarContentTintHelper
import com.wolcano.musicplayer.music.utils.*
import com.wolcano.musicplayer.music.widgets.RotateFabBehavior
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@AndroidEntryPoint
class AlbumDetailFragment : BaseFragment(), PlaylistListener, View.OnClickListener {

    private var adapter: AlbumSongAdapter? = null
    private var albumId: Long = -1
    private var albumName: String? = null
    private var primaryColor = -1
    private var accentColor: Int = -1
    private var toolbar: Toolbar? = null
    private var collapsingToolbarLayout: CollapsingToolbarLayout? = null
    private var appBarLayout: AppBarLayout? = null
    private lateinit var fabPlay: FloatingActionButton
    private var albumArt: ImageView? = null
    private var recyclerView: RecyclerView? = null
    private var gradient: View? = null
    private val fabTime = 150
    private var handlerFab: Handler? = null
    private var runnableFab: Runnable? = null

    val viewModel: AlbumDetailViewModel by viewModels()

    companion object {
        fun newInstance(id: Long, name: String?): AlbumDetailFragment {
            val fragment = AlbumDetailFragment()
            val args = Bundle()
            args.putLong(Constants.ALBUM_ID, id)
            args.putString(Constants.ALBUM_NAME, name)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (arguments != null) {
            albumId = requireArguments().getLong(Constants.ALBUM_ID)
            albumName = requireArguments().getString(Constants.ALBUM_NAME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root: View

        val binding: FragmentAlbumDetailBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_album_detail, container, false)
        setUpViews(binding)
        root = binding.root

        val layoutParams = toolbar!!.layoutParams as CollapsingToolbarLayout.LayoutParams
        layoutParams.height += Utils.getStatHeight(requireContext())
        toolbar?.layoutParams = layoutParams
        toolbar?.setPadding(0, Utils.getStatHeight(requireContext()), 0, 0)

        primaryColor = Utils.getPrimaryColor(requireContext())
        accentColor = Utils.getAccentColor(requireContext())

        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        recyclerView?.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        handlePermissionsAndRetrieveSongsForArtist()
        handleViewModel()
        showAlbumArt()
        setupToolbar()
        return root
    }

    private fun handlePermissionsAndRetrieveSongsForArtist() {
        PermissionUtils.with(requireActivity())
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ).result(object : PermissionUtils.PermInterface {
                override fun onPermGranted() {
                    viewModel.retrieveAlbumSongs(albumId)
                }

                override fun onPermUnapproved() {
                    ToastUtils.show(requireContext(), R.string.no_perm_storage)
                }
            }).requestPermissions()
    }

    private fun handleViewModel() {
        viewModel.songsLiveData.observe(viewLifecycleOwner, {
            if (it != null) {
                setSongList(it as ArrayList<Song>)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(
            requireActivity(),
            toolbar,
            menu,
            ColorUtils.getToolbarBackgroundColor(toolbar)
        )
    }

    fun show() {
        fabPlay.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    private fun setUpViews(binding: FragmentAlbumDetailBinding) {
        toolbar = binding.toolbar
        collapsingToolbarLayout = binding.collapsingtoolbar
        appBarLayout = binding.appbar
        fabPlay = binding.fabPlay
        albumArt = binding.albumArt
        recyclerView = binding.recyclerview
        gradient = binding.gradient
        fabPlay.setOnClickListener(this)
    }

    private fun showAlbumArt() {
        val uri = ContentUris.withAppendedId(
            Uri.parse("content://media/external/audio/albumart"),
            albumId
        )
        Picasso.get()
            .load(uri)
            .placeholder(R.color.darkbg1)
            .error(R.drawable.album_art)
            .into(albumArt)
        var bitmap: Bitmap? = null
        try {
            bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (bitmap != null) {
            Palette.Builder(bitmap).generate { palette ->
                val swatch = Utils.getMostPopulousSwatch(palette)
                if (swatch != null) {
                    var color = swatch.rgb
                    color = ColorUtils.shiftColor(color, 0.7f)
                    collapsingToolbarLayout?.setContentScrimColor(color)
                    collapsingToolbarLayout?.setStatusBarScrimColor(
                        Utils.getStatusBarColor(
                            color
                        )
                    )
                    if (Utils.isColorLight(color)) {
                        collapsingToolbarLayout?.setCollapsedTitleTextColor(Color.BLACK)
                        collapsingToolbarLayout?.setExpandedTitleColor(Color.BLACK)
                    } else {
                        collapsingToolbarLayout?.setCollapsedTitleTextColor(Color.WHITE)
                        collapsingToolbarLayout?.setExpandedTitleColor(Color.WHITE)
                    }
                    setStatusBarColor(color)
                }
            }
        } else {
            collapsingToolbarLayout?.setContentScrimColor(primaryColor)
            collapsingToolbarLayout?.setStatusBarScrimColor(Utils.getStatusBarColor(primaryColor))
            if (Utils.isColorLight(primaryColor)) {
                collapsingToolbarLayout?.setCollapsedTitleTextColor(Color.BLACK)
            } else {
                collapsingToolbarLayout?.setCollapsedTitleTextColor(Color.WHITE)
            }
            setStatusBarColor(Color.BLACK)
        }
    }

    private fun runLayoutAnimation(recyclerView: RecyclerView) {
        val context = recyclerView.context
        val controller =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
        recyclerView.layoutAnimation = controller
        recyclerView.adapter?.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }

    override fun onDestroyView() {
        setStatusBarColor(primaryColor)
        if (handlerFab != null && runnableFab != null) {
            handlerFab!!.removeCallbacks(runnableFab)
        }
        super.onDestroyView()
    }

    private fun setStatusBarColor(color: Int) {
        if (Utils.isColorLight(color)) {
            ATH.setLightStatusbar(requireActivity(), true)
        } else {
            ATH.setLightStatusbar(requireActivity(), false)
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= 21) {
            RotateFabBehavior.show(fabPlay, accentColor, true)
        }

        toolbar?.setBackgroundColor(Color.TRANSPARENT)
        collapsingToolbarLayout?.setContentScrimColor(primaryColor)
        collapsingToolbarLayout?.setStatusBarScrimColor(Utils.getStatusBarColor(primaryColor))
        if (Utils.isColorLight(primaryColor)) {
            collapsingToolbarLayout?.setCollapsedTitleTextColor(Color.BLACK)
        } else {
            collapsingToolbarLayout?.setCollapsedTitleTextColor(Color.WHITE)
        }
        setStatusBarColor(primaryColor)
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity?)?.setSupportActionBar(toolbar)
        val ab = (requireActivity() as AppCompatActivity?)?.supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
        toolbar?.setNavigationOnClickListener { requireActivity().supportFragmentManager.popBackStack() }
        collapsingToolbarLayout?.title = albumName
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
        adapter = AlbumSongAdapter(requireContext(), songList, this@AlbumDetailFragment)
        recyclerView?.adapter = adapter
        runLayoutAnimation(recyclerView!!)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.fabPlay) {
            handlerFab = Handler()
            runnableFab = Runnable {
                if (adapter != null) {
                    if (adapter!!.songList?.size != 0) {
                        val song = adapter!!.songList?.get(0)
                        if (song != null) {
                            RemotePlay.playAdd(requireContext(), adapter!!.songList!!, song)
                        }
                    }
                }
            }
            handlerFab?.postDelayed(runnableFab, fabTime.toLong())
        }
    }

}