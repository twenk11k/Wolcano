package com.wolcano.musicplayer.music.ui.activity.main

import android.Manifest
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.content.PlayerEnum
import com.wolcano.musicplayer.music.content.SlidingPanel
import com.wolcano.musicplayer.music.content.managers.DisposableManager
import com.wolcano.musicplayer.music.databinding.ActivityMainBinding
import com.wolcano.musicplayer.music.listener.OnServiceListener
import com.wolcano.musicplayer.music.listener.PlaylistListener
import com.wolcano.musicplayer.music.data.model.ModelBitmap
import com.wolcano.musicplayer.music.data.model.Song
import com.wolcano.musicplayer.music.provider.RemotePlay.buttonClick
import com.wolcano.musicplayer.music.provider.RemotePlay.getPlayMusic
import com.wolcano.musicplayer.music.provider.RemotePlay.getPlayerCurrentPosition
import com.wolcano.musicplayer.music.provider.RemotePlay.getSongList
import com.wolcano.musicplayer.music.provider.RemotePlay.isPausing
import com.wolcano.musicplayer.music.provider.RemotePlay.isPlaying
import com.wolcano.musicplayer.music.provider.RemotePlay.isPreparing
import com.wolcano.musicplayer.music.provider.RemotePlay.next
import com.wolcano.musicplayer.music.provider.RemotePlay.onListener
import com.wolcano.musicplayer.music.provider.RemotePlay.playAdd
import com.wolcano.musicplayer.music.provider.RemotePlay.prev
import com.wolcano.musicplayer.music.provider.RemotePlay.removeListener
import com.wolcano.musicplayer.music.provider.RemotePlay.seekTo
import com.wolcano.musicplayer.music.ui.activity.base.BaseActivity
import com.wolcano.musicplayer.music.ui.activity.queue.QueueActivity
import com.wolcano.musicplayer.music.ui.activity.settings.SettingsActivity
import com.wolcano.musicplayer.music.ui.dialog.Dialogs
import com.wolcano.musicplayer.music.ui.fragment.library.LibraryFragment
import com.wolcano.musicplayer.music.ui.fragment.online.OnlineFragment
import com.wolcano.musicplayer.music.ui.fragment.playlist.PlaylistFragment
import com.wolcano.musicplayer.music.ui.fragment.recentlyadded.RecentlyFragment
import com.wolcano.musicplayer.music.ui.helper.SongHelperMenu
import com.wolcano.musicplayer.music.utils.*
import com.wolcano.musicplayer.music.utils.PermissionUtils.PermInterface
import com.wolcano.musicplayer.music.widgets.ModelView
import com.wolcano.musicplayer.music.widgets.SongCover
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.*

@AndroidEntryPoint
class MainActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
    View.OnClickListener,
    OnSeekBarChangeListener, OnServiceListener, PlaylistListener {

    private var isDragging = false
    private var slidingPanel: SlidingPanel? = null
    private var isExpand = false
    private var lightStatusbar = false
    private var mIsResumed = true
    private var placeholder: Drawable? = null
    private var handlerCollapse: Handler? = null
    private var currentFrag = 0
    private var typeReturn = false
    private val binding: ActivityMainBinding by binding(R.layout.activity_main)

    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDrawerOptions()
        initViews()
        displayDialog()
    }

    private fun setDrawerOptions() {
        handlerCollapse = Handler()
    }

    /**
     * Slide down bottomnavigationview using ViewPropertyAnimator
     */
    private fun slideDown(child: BottomNavigationView, slideOffset: Float) {
        val height = slideOffset * child.height
        val animator = child.animate()
        animator.translationY(height)
            .setInterpolator(DecelerateInterpolator())
            .setDuration(0)
            .start()
    }

    fun getToolbar(): Toolbar? {
        return findViewById(R.id.toolbar)
    }

    private fun displayDialog() {
        val first = Utils.getFirst(this)
        if (first) {
            val str = if (Build.VERSION.SDK_INT >= 23) {
                getString(R.string.first_dec)
            } else {
                getString(R.string.first_dec_old)
            }
            MaterialDialog(this).show {
                title(R.string.first_title)
                message(text = str)
                positiveButton(R.string.close)
                cancelOnTouchOutside(false)
            }

            Utils.setFirst(this, false)
        }
    }

    private fun prev() {
        prev(this)
    }

    private fun setLeftButtonMode() {
        var mode = PlayerEnum.valueOf(Utils.getPlaylistId(this))
        mode = when (mode) {
            PlayerEnum.NORMAL -> PlayerEnum.SHUFFLE
            PlayerEnum.SHUFFLE -> PlayerEnum.REPEAT
            PlayerEnum.REPEAT -> PlayerEnum.NORMAL
            else -> PlayerEnum.NORMAL
        }
        Utils.setPlaylistId(this, mode.value)
        setLeftButton()
    }

    override fun onBackPressed() {
        if (isExpand) {
            binding.slidinguppanel.slidinguppanellayout.panelState = PanelState.COLLAPSED
        } else if (!isExpand) {
            if (!getIfMainVisible()) {
                val fragments = supportFragmentManager.backStackEntryCount
                if (fragments == 1) {
                    moveTaskToBack(true)
                } else {
                    if (fragmentManager.backStackEntryCount > 1) {
                        fragmentManager.popBackStack()
                    } else {
                        super.onBackPressed()
                    }
                }
            }
        }
    }

    override fun onServiceConnection() {
        slidingPanel = SlidingPanel(binding.slidinguppanel.slidinguppanelTop1.flPlayBar, this)
        onListener(slidingPanel!!)
        handleIntent()
        showPlayView()
    }

    fun handleIntent() {
        val intent = intent
        if (intent.action == Intent.ACTION_VIEW && !Utils.getRecreated(this)) {
            startPlaybackFromUri(intent.data)
        }
        Utils.setRecreated(this, false)
    }

    private fun startPlaybackFromUri(songUri: Uri) {
        val songPath = UriFilesUtils.getPathFromUri(this, songUri)
        val intentList: List<Song?>? = buildQueueFromFileUri(songUri)
        if (intentList != null) {
            for (i in intentList.indices) {
                if (intentList[i]!!.path == songPath) {
                    playAdd(this, intentList, intentList[i]!!)
                    break
                }
            }
        }
    }

    private fun buildQueueFromFileUri(fileUri: Uri): List<Song?>? {
        val path = UriFilesUtils.getPathFromUri(this, fileUri)
        if (path == null || path.trim { it <= ' ' }.isEmpty()) {
            return emptyList()
        }
        val file = File(path)
        return SongUtils.buildSongListFromFile(this, file)
    }

    fun showPlayView() {
        binding.slidinguppanel.slidinguppanelChild2.modelcover.initHelper(isPlaying())
        setLeftButton()
        onSongChange(getPlayMusic(this))
        onListener(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent()
    }

    fun initViews() {
        setSlidingPanelLayout()
        val openingVal: Int = Utils.getOpeningVal(this@MainActivity)
        Utils.setFirstFrag(this.applicationContext, true)
        setFragment(openingVal)
        binding.navView.menu.getItem(openingVal)?.isChecked = true
        binding.navView.setOnNavigationItemSelectedListener { menuItem: MenuItem ->
            onNavigationItemSelected(
                menuItem
            )
        }
        initViews2()
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (seekBar === binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.seekbar) {
            binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.current.text =
                Utils.getDuraStr((progress / 1000).toLong(), this)
        }
    }

    private fun initViews2() {
        NavigationViewUtils.setItemIconColors(
            binding.navView,
            ColorUtils.getOppositeColor(Utils.getPrimaryColor(this)),
            Utils.getAccentColor(this)
        )
        NavigationViewUtils.setItemTextColors(
            binding.navView,
            ColorUtils.getOppositeColor(Utils.getPrimaryColor(this)),
            Utils.getAccentColor(this)
        )
        binding.navView.setBackgroundColor(Utils.getPrimaryColor(this))
        binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.seekbar.progressDrawable.setColorFilter(
            Utils.getAccentColor(this),
            PorterDuff.Mode.SRC_IN
        )
        binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.seekbar.thumb?.setColorFilter(
            Utils.getAccentColor(
                this
            ), PorterDuff.Mode.SRC_IN
        )
        binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.queue.setColorFilter(
            ContextCompat.getColor(this, R.color.grey0)
        )

        placeholder = ContextCompat.getDrawable(this, R.drawable.album_default)
        placeholder?.setColorFilter(Utils.getPrimaryColor(this), PorterDuff.Mode.MULTIPLY)
    }

    override fun handleListener() {
        binding.slidinguppanel.slidinguppanelChild2.back.setOnClickListener(this)
        binding.slidinguppanel.slidinguppanelChild2.menu.setOnClickListener(this)
        binding.slidinguppanel.slidinguppanelChild2.innerLinearTopOne.setOnClickListener(this)
        binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.leftbutton.setOnClickListener(
            this
        )
        binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.play.setOnClickListener(
            this
        )
        binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.prev.setOnClickListener(
            this
        )
        binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.next.setOnClickListener(
            this
        )
        binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.queue.setOnClickListener(
            this
        )
        binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.seekbar.setOnSeekBarChangeListener(
            this
        )
    }

    override fun onResume() {
        super.onResume()
        if (binding.slidinguppanel.slidinguppanelTop1.flPlayBar.alpha.toDouble() == 1.0 && !isExpand) {
            binding.slidinguppanel.slidinguppanellayout.panelState = PanelState.COLLAPSED
        }
        if (Utils.getColorSelection(this)) {
            SongCover.setCacheDefault(this)
            Utils.setColorSelection(this, false)
            Utils.setRecreated(this, true)
            updateUiSettings()
        }
        mIsResumed = true
    }

    private fun updateUiSettings() {
        slidingPanel?.setViews()
        mIsResumed = true
        setFragment(currentFrag)
        initViews2()
        onChangeSong(getPlayMusic(this))
    }

    override fun onPause() {
        super.onPause()
        mIsResumed = false
    }

    override fun setLightStatusbar(enabled: Boolean) {
        lightStatusbar = enabled
        if (getPanelState() == PanelState.COLLAPSED) {
            super.setLightStatusbar(enabled)
        }
    }

    fun getPanelState(): PanelState? {
        return binding.slidinguppanel.slidinguppanellayout.panelState
    }

    fun setSlidingPanelLayout() {
        binding.slidinguppanel.slidinguppanellayout.addPanelSlideListener(object :
            SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View, slideOffset: Float) {
                if (slideOffset >= 0 && slideOffset < 1) {
                    binding.slidinguppanel.slidinguppanelTop1.flPlayBar.visibility = View.VISIBLE
                }
                binding.slidinguppanel.slidinguppanelTop1.flPlayBar.alpha = 1 - slideOffset
            }

            override fun onPanelStateChanged(
                panel: View,
                previousState: PanelState,
                newState: PanelState
            ) {
                when (newState) {
                    PanelState.COLLAPSED -> {
                        binding.slidinguppanel.slidinguppanelTop1.flPlayBar.visibility =
                            View.VISIBLE
                        isExpand = false
                        ATH.setLightStatusbar(this@MainActivity, lightStatusbar)
                    }
                    PanelState.EXPANDED -> {
                        binding.slidinguppanel.slidinguppanelTop1.flPlayBar.visibility = View.GONE
                        ATH.setLightStatusbar(this@MainActivity, false)
                        isExpand = true
                    }
                    PanelState.ANCHORED -> if (handlerCollapse != null) {
                        handlerCollapse?.postDelayed({ collapsePanel() }, 250)
                    }
                }
            }
        })
        binding.slidinguppanel.slidinguppanelChild2.child2linear.setPadding(
            0,
            Utils.getStatHeight(this),
            0,
            0
        )
    }

    private operator fun next() {
        next(this, false)
    }

    fun expandPanel() {
        binding.slidinguppanel.slidinguppanellayout.panelState = PanelState.EXPANDED
    }

    fun collapsePanel() {
        binding.slidinguppanel.slidinguppanellayout.panelState = PanelState.COLLAPSED
    }

    override fun onDestroy() {
        removeListener(this)
        if (slidingPanel!!.disposable != null && !slidingPanel!!.disposable!!.isDisposed) {
            slidingPanel?.disposable?.dispose()
        }
        DisposableManager.dispose()
        super.onDestroy()
    }


    override fun onStartTrackingTouch(seekBar: SeekBar) {
        if (seekBar === binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.seekbar) {
            isDragging = true
        }
    }

    fun getIfMainVisible(): Boolean {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment)
        return if (fragment is OnlineFragment) {
            if (fragment.isSearchOpen()) {
                fragment.closeSearch()
                true
            } else {
                false
            }
        } else false
    }

    fun setFragment(position: Int) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        currentFrag = position
        if (position == 0) {
            if (mIsResumed) supportFragmentManager.popBackStack(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
            val fragmentMain: Fragment = OnlineFragment()
            fragmentTransaction.replace(R.id.fragment, fragmentMain)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        } else if (position == 1) {
            if (mIsResumed) supportFragmentManager.popBackStack(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
            val fragmentRecently: Fragment = RecentlyFragment()
            fragmentTransaction.replace(R.id.fragment, fragmentRecently)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        } else if (position == 2) {
            if (mIsResumed) supportFragmentManager.popBackStack(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
            val fragmentLibrary: Fragment = LibraryFragment()
            fragmentTransaction.replace(R.id.fragment, fragmentLibrary)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        } else if (position == 3) {
            if (mIsResumed) supportFragmentManager.popBackStack(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
            val fragmentPlaylists: Fragment = PlaylistFragment()
            fragmentTransaction.replace(R.id.fragment, fragmentPlaylists)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        if (seekBar === binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.seekbar) {
            isDragging = false
            if (isPlaying() || isPausing()) {
                val progress = seekBar.progress
                seekTo(progress)
            } else {
                seekBar.progress = 0
            }
        }
    }


    override fun onChangeSong(song: Song?) {
        onSongChange(song)
    }

    override fun onPlayStart() {
        binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.play.isSelected = true
        binding.slidinguppanel.slidinguppanelChild2.modelcover.start()
    }

    override fun onPlayPause() {
        binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.play.isSelected = false
        binding.slidinguppanel.slidinguppanelChild2.modelcover.pause()
    }

    override fun onProgressChange(progress: Int) {
        if (!isDragging) {
            binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.seekbar.progress =
                progress
        }
    }

    override fun onBufferingUpdate(percent: Int) {
        if (percent != 0) binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.seekbar.secondaryProgress =
            binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.seekbar.max * 100 / percent
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> onBackPressed()
            R.id.menu -> if (getPlayMusic(this) != null) {
                if (getPlayMusic(this)!!.type == Song.Tip.MODEL0) {
                    SongHelperMenu.handleMenuLocal(
                        this,
                        v,
                        getPlayMusic(this)!!,
                        this
                    )
                } else {
                    SongHelperMenu.handleMenuOnline(this, v, getPlayMusic(this))
                }
            }
            R.id.innerLinearTopOne -> onBackPressed()
            R.id.leftbutton -> setLeftButtonMode()
            R.id.play -> play()
            R.id.next -> next()
            R.id.prev -> prev()
            R.id.queue -> if (getSongList()!!.isNotEmpty()) {
                val intent = Intent(this, QueueActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, resources.getString(R.string.empty_queue), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun play() {
        buttonClick(this)
    }

    fun setLeftButton() {
        val mode = Utils.getPlaylistId(this)
        binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.leftbutton.setImageLevel(
            mode
        )
    }

    private fun setAlbumCover(song: Song?) {
        loadBitmap(
            song,
            binding.slidinguppanel.slidinguppanelChild2.child2bg,
            binding.slidinguppanel.slidinguppanelChild2.modelcover
        )
    }

    private fun setBitmap(
        bitmapList: List<ModelBitmap?>,
        imageView: ImageView,
        modelCoverView: ModelView
    ) {
        val blur: ModelBitmap? = bitmapList[0]
        val round: ModelBitmap? = bitmapList[1]
        if (blur == null) {
            imageView.setImageDrawable(placeholder)
        } else {
            imageView.setImageBitmap(blur.bitmap)
        }
        if (round == null) {
            modelCoverView.setRoundBitmap(SongCover.getMainModel(this, SongCover.Tip.OVAL))
        } else {
            if (round.id == 0) {
                modelCoverView.setRoundBitmap(round.bitmap)
            } else if (round.id == 1) {
                modelCoverView.setRoundBitmap(
                    SongCover.getMainModel(this, SongCover.Tip.OVAL)
                )
            }
        }
    }

    fun loadBitmap(song: Song?, imageView: ImageView, modelCoverView: ModelView) {
        viewModel.retrieveBitmaps(song)
        viewModel.bitmapsLiveData.observe(this, {
            if (it != null) {
                setBitmap(
                    it,
                    imageView,
                    modelCoverView
                )
            }
        })
    }

    fun onSongChange(song: Song?) {
        if (song == null) {
            binding.slidinguppanel.slidinguppanelChild2.title.text = ""
            binding.slidinguppanel.slidinguppanelChild2.artist.text = ""
            binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.current.setText(R.string.start)
            binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.total.setText(R.string.start)
            binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.seekbar.secondaryProgress =
                0
            binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.seekbar.progress =
                0
            binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.seekbar.max = 0
            setAlbumCover(song)
            return
        }
        binding.slidinguppanel.slidinguppanelChild2.title.text = song.title
        binding.slidinguppanel.slidinguppanelChild2.artist.text = song.artist
        binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.seekbar.progress =
            getPlayerCurrentPosition().toInt()
        binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.seekbar.secondaryProgress =
            0
        binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.seekbar.max =
            song.duration.toInt()
        binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.current.setText(R.string.start)
        binding.slidinguppanel.slidinguppanelChild2.slidinguppanelController.total.text =
            Utils.getDuraStr(song.duration / 1000, this)
        setAlbumCover(song)
        if (isPlaying() || isPreparing()) {
            binding.slidinguppanel.slidinguppanelTop1.play.isSelected = true
            binding.slidinguppanel.slidinguppanelChild2.modelcover.start()
        } else {
            binding.slidinguppanel.slidinguppanelTop1.play.isSelected = false
            binding.slidinguppanel.slidinguppanelChild2.modelcover.pause()
        }
    }


    override fun handlePlaylistDialog(song: Song?) {
        viewModel.retrievePlaylists()
        viewModel.playlistsLiveData.observe(this, {
            if (it != null) {
                Dialogs.addPlaylistDialog(
                    this,
                    song,
                    it
                )
            }
        })
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        Utils.hideKeyboard(this@MainActivity)
        val id = menuItem.itemId
        binding.slidinguppanel.slidinguppanellayout.panelState = PanelState.COLLAPSED
        Utils.setFirstFrag(this@MainActivity, false)
        typeReturn = false
        when (id) {
            R.id.nav_onlineplayer -> {
                setFragment(0)
                typeReturn = true
            }
            R.id.nav_recentlyadded -> {
                Utils.setCountDownload(this@MainActivity, 0)
                setFragment(1)
                typeReturn = true
            }
            R.id.nav_library -> {
                setFragment(2)
                typeReturn = true
            }
            R.id.nav_playlists -> {
                setFragment(3)
                typeReturn = true
            }
            R.id.nav_settings -> PermissionUtils.with(this@MainActivity)
                .permissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .result(object : PermInterface {
                    override fun onPermGranted() {
                        val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                        startActivity(intent)
                    }

                    override fun onPermUnapproved() {
                        ToastUtils.show(applicationContext, R.string.no_perm_open_settings)
                    }
                })
                .requestPermissions()
        }
        return typeReturn
    }

}