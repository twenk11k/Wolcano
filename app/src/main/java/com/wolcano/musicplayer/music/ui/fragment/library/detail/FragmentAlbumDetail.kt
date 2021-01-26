package com.wolcano.musicplayer.music.ui.fragment.library.detail

import android.app.Activity
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
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kabouzeid.appthemehelper.ATH
import com.kabouzeid.appthemehelper.util.ColorUtil
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper
import com.squareup.picasso.Picasso
import com.wolcano.musicplayer.music.App
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.constants.Constants
import com.wolcano.musicplayer.music.databinding.FragmentAlbumDetailBinding
import com.wolcano.musicplayer.music.databinding.FragmentAlbumDetailOldBinding
import com.wolcano.musicplayer.music.di.component.AlbumSongComponent
import com.wolcano.musicplayer.music.di.component.ApplicationComponent
import com.wolcano.musicplayer.music.di.component.DaggerAlbumSongComponent
import com.wolcano.musicplayer.music.di.module.AlbumSongModule
import com.wolcano.musicplayer.music.mvp.DisposableManager
import com.wolcano.musicplayer.music.mvp.interactor.SongInteractorImpl
import com.wolcano.musicplayer.music.mvp.listener.PlaylistListener
import com.wolcano.musicplayer.music.mvp.models.Playlist
import com.wolcano.musicplayer.music.mvp.models.Song
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.SongPresenter
import com.wolcano.musicplayer.music.mvp.view.SongView
import com.wolcano.musicplayer.music.provider.RemotePlay
import com.wolcano.musicplayer.music.ui.adapter.detail.AlbumSongAdapter
import com.wolcano.musicplayer.music.ui.dialog.Dialogs
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragment
import com.wolcano.musicplayer.music.utils.SongUtils
import com.wolcano.musicplayer.music.utils.Utils
import com.wolcano.musicplayer.music.widgets.RotateFabBehavior
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import javax.inject.Inject

class FragmentAlbumDetail : BaseFragment(), SongView, PlaylistListener, View.OnClickListener {

    private var mAdapter: AlbumSongAdapter? = null
    private var albumID: Long = -1
    private var albumName: String? = null
    private var primaryColor = -1
    private var accentColor: Int = -1
    private var activity: Activity? = null
    private lateinit var menu: Menu
    private var disposable: Disposable? = null
    private var toolbar: Toolbar? = null
    private var collapsingToolbarLayout: CollapsingToolbarLayout? = null
    private var appBarLayout: AppBarLayout? = null
    private var fabPlay: FloatingActionButton? = null
    private var albumArt: ImageView? = null
    private var recyclerView: RecyclerView? = null
    private var gradient: View? = null
    private val FAB_TIME = 150
    private var handlerFab: Handler? = null
    private var runnableFab: Runnable? = null

    var songPresenter: SongPresenter? = null @Inject set

    companion object {
        fun newInstance(id: Long, name: String?): FragmentAlbumDetail {
            val fragment = FragmentAlbumDetail()
            val args = Bundle()
            args.putLong(Constants.ALBUM_ID, id)
            args.putString(Constants.ALBUM_NAME, name)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = getActivity()
        setHasOptionsMenu(true)
        if (arguments != null) {
            albumID = arguments!!.getLong(Constants.ALBUM_ID)
            albumName = arguments!!.getString(Constants.ALBUM_NAME)
        }
        setupComponent((getActivity()!!.application as App).getApplicationComponent())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        this.menu = menu
    }

    fun show() {
        if (Build.VERSION.SDK_INT >= 21) {
            fabPlay!!.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(DecelerateInterpolator())
                .start()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root: View

        val binding: FragmentAlbumDetailBinding
        val bindingOld: FragmentAlbumDetailOldBinding

        if (Build.VERSION.SDK_INT >= 21) {
            binding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_album_detail, container, false)
            setUpViews(binding)
            root = binding.root
        } else {
            bindingOld = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_album_detail_old,
                container,
                false
            )
            setUpViewsOld(bindingOld)
            root = bindingOld.root
        }
        if (Build.VERSION.SDK_INT < 21) {
            appBarLayout?.fitsSystemWindows = false
            albumArt?.fitsSystemWindows = false
            gradient?.fitsSystemWindows = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = getActivity()!!.window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = resources.getColor(R.color.darkbg1)
            }
        } else {
            val layoutParams = toolbar!!.layoutParams as CollapsingToolbarLayout.LayoutParams
            layoutParams.height += Utils.getStatHeight(requireContext())
            toolbar?.layoutParams = layoutParams
            toolbar?.setPadding(0, Utils.getStatHeight(requireContext()), 0, 0)
        }

        primaryColor = Utils.getPrimaryColor(requireContext())
        accentColor = Utils.getAccentColor(requireContext())

        recyclerView?.adapter = mAdapter
        recyclerView?.layoutManager = LinearLayoutManager(context)
        recyclerView?.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )


        songPresenter?.getAlbumSongs()

        showAlbumArt()
        setupToolbar()
        return root

    }

    private fun setUpViewsOld(binding: FragmentAlbumDetailOldBinding) {
        toolbar = binding.toolbar
        appBarLayout = binding.appbar
        albumArt = binding.albumArt
        gradient = binding.gradient
        recyclerView = binding.recyclerview
    }


    private fun setUpViews(binding: FragmentAlbumDetailBinding) {
        toolbar = binding.toolbar
        collapsingToolbarLayout = binding.collapsingtoolbar
        appBarLayout = binding.appbar
        fabPlay = binding.fabPlay
        albumArt = binding.albumArt
        recyclerView = binding.recyclerview
        gradient = binding.gradient
        fabPlay?.setOnClickListener(this)
    }

    private fun showAlbumArt() {
        val uri = ContentUris.withAppendedId(
            Uri.parse("content://media/external/audio/albumart"),
            albumID
        )
        Picasso.get()
            .load(uri)
            .placeholder(R.color.darkbg1)
            .error(R.drawable.album_art)
            .into(albumArt)
        var bitmap: Bitmap? = null
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, uri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (bitmap != null) {
            Palette.Builder(bitmap).generate { palette ->
                val swatch = Utils.getMostPopulousSwatch(palette)
                if (swatch != null) {
                    var color = swatch.rgb
                    color = ColorUtil.shiftColor(color, 0.7f)
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
                    ToolbarContentTintHelper.handleOnCreateOptionsMenu(
                        activity,
                        toolbar,
                        menu,
                        color
                    )
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
            ToolbarContentTintHelper.handleOnCreateOptionsMenu(activity, toolbar, menu, Color.WHITE)
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

        if (disposable != null && !disposable!!.isDisposed) {
            disposable!!.dispose()
        }

        DisposableManager.dispose()

        super.onDestroyView()
    }


    private fun setStatusBarColor(color: Int) {
        if (Utils.isColorLight(color)) {
            ATH.setLightStatusbar(activity, true)
        } else {
            ATH.setLightStatusbar(activity, false)
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= 21) {
            RotateFabBehavior.show(fabPlay, accentColor, true)
        }

        toolbar?.setBackgroundColor(Color.TRANSPARENT)
        if (getActivity() != null) {
            collapsingToolbarLayout?.setContentScrimColor(primaryColor)
            collapsingToolbarLayout?.setStatusBarScrimColor(Utils.getStatusBarColor(primaryColor))
            ToolbarContentTintHelper.handleOnCreateOptionsMenu(
                getActivity(),
                toolbar,
                menu,
                primaryColor
            )
            if (Utils.isColorLight(primaryColor)) {
                collapsingToolbarLayout?.setCollapsedTitleTextColor(Color.BLACK)
            } else {
                collapsingToolbarLayout?.setCollapsedTitleTextColor(Color.WHITE)
            }
            setStatusBarColor(primaryColor)
        }
    }

    private fun setupToolbar() {
        (getActivity() as AppCompatActivity?)!!.setSupportActionBar(toolbar)
        val ab = (getActivity() as AppCompatActivity?)!!.supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
        toolbar?.setNavigationOnClickListener { getActivity()!!.supportFragmentManager.popBackStack() }
        collapsingToolbarLayout?.title = albumName
    }

    override fun handlePlaylistDialog(song: Song?) {
        disposable = Observable.fromCallable<List<Playlist>> { SongUtils.scanPlaylist(activity) }
            .subscribeOn(
                Schedulers.io()
            ).observeOn(
                AndroidSchedulers.mainThread()
            ).subscribe { playlists ->
            Dialogs.addPlaylistDialog(
                activity,
                song,
                playlists
            )
        }
    }

    override fun setSongList(songList: MutableList<Song>) {
        mAdapter = AlbumSongAdapter(context!!, songList, this@FragmentAlbumDetail)
        recyclerView?.adapter = mAdapter
        runLayoutAnimation(recyclerView!!)
    }

    fun setupComponent(applicationComponent: ApplicationComponent?) {
        val sort = MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        val albumSongComponent: AlbumSongComponent = DaggerAlbumSongComponent.builder()
            .applicationComponent(applicationComponent)
            .albumSongModule(
                AlbumSongModule(
                    this,
                    this,
                    activity,
                    sort,
                    albumID,
                    SongInteractorImpl()
                )
            )
            .build()
        albumSongComponent.inject(this)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.fabPlay) {
            handlerFab = Handler()
            runnableFab = Runnable {
                if (mAdapter != null) {
                    if (mAdapter!!.songList.size != 0) {
                        val song = mAdapter!!.songList[0]
                        RemotePlay.playAdd(context!!, mAdapter!!.songList, song)
                    }
                }
            }
            handlerFab?.postDelayed(runnableFab, FAB_TIME.toLong())
        }
    }

}