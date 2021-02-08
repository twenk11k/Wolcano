package com.wolcano.musicplayer.music.ui.fragment.library.detail

import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.wolcano.musicplayer.music.App
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.constants.Constants
import com.wolcano.musicplayer.music.databinding.FragmentBaseSongBinding
import com.wolcano.musicplayer.music.di.component.ApplicationComponent
import com.wolcano.musicplayer.music.di.component.DaggerGenreSongComponent
import com.wolcano.musicplayer.music.di.module.GenreSongModule
import com.wolcano.musicplayer.music.mvp.DisposableManager
import com.wolcano.musicplayer.music.mvp.interactor.SongInteractorImpl
import com.wolcano.musicplayer.music.mvp.listener.PlaylistListener
import com.wolcano.musicplayer.music.mvp.models.Playlist
import com.wolcano.musicplayer.music.mvp.models.Song
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.SongPresenter
import com.wolcano.musicplayer.music.mvp.view.SongView
import com.wolcano.musicplayer.music.ui.activity.MainActivity
import com.wolcano.musicplayer.music.ui.adapter.RecentlyAddedAdapter
import com.wolcano.musicplayer.music.ui.dialog.Dialogs
import com.wolcano.musicplayer.music.ui.dialog.SleepTimerDialog
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragment
import com.wolcano.musicplayer.music.ui.helper.ToolbarContentTintHelper
import com.wolcano.musicplayer.music.utils.ColorUtils
import com.wolcano.musicplayer.music.utils.SongUtils
import com.wolcano.musicplayer.music.utils.Utils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class FragmentGenreDetail : BaseFragment(), SongView, PlaylistListener {

    private var adapter: RecentlyAddedAdapter? = null
    private var genreID: Long = -1
    private var genreName: String? = null
    private var primaryColor = -1
    private var disposable: Disposable? = null
    private lateinit var binding: FragmentBaseSongBinding

    var songPresenter: SongPresenter? = null @Inject set

    companion object {
        fun newInstance(id: Long, name: String?): FragmentGenreDetail {
            val fragment = FragmentGenreDetail()
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
            genreID = arguments!!.getLong(Constants.GENRE_ID)
            genreName = arguments!!.getString(Constants.GENRE_NAME)
        }
        setupComponent((activity!!.application as App).getApplicationComponent())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_base_song, container, false)

        primaryColor = Utils.getPrimaryColor(requireContext())
        setStatusbarColor(primaryColor, binding.statusBarCustom)

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
            SleepTimerDialog().show(fragmentManager!!, "SET_SLEEP_TIMER")
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

        songPresenter?.genreSongs
        setupToolbar()
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

    override fun onDestroyView() {
        super.onDestroyView()

        if (disposable != null && !disposable!!.isDisposed) {
            disposable!!.dispose()
        }

        DisposableManager.dispose()
    }

    private fun setupToolbar() {
        (requireActivity() as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
        val ab = (requireActivity() as AppCompatActivity?)?.supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { requireActivity().supportFragmentManager.popBackStack() }
    }

    override fun handlePlaylistDialog(song: Song?) {
        disposable = Observable.fromCallable<List<Playlist>> { SongUtils.scanPlaylist(activity) }
            .subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()
            ).subscribe { playlists ->
                Dialogs.addPlaylistDialog(
                    requireActivity(),
                    song,
                    playlists
                )
            }
    }

    override fun setSongList(songList: ArrayList<Song>?) {
        if (songList!!.size <= 30) {
            binding.recyclerview.setThumbEnabled(false)
        } else {
            binding.recyclerview.setThumbEnabled(true)
        }
        adapter = RecentlyAddedAdapter(
            (requireActivity() as MainActivity?)!!,
            songList,
            this@FragmentGenreDetail
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


    fun setupComponent(applicationComponent: ApplicationComponent?) {
        val sort = MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        val genreSongComponent = DaggerGenreSongComponent.builder()
            .applicationComponent(applicationComponent)
            .genreSongModule(
                GenreSongModule(
                    this,
                    this,
                    requireActivity(),
                    sort,
                    genreID,
                    SongInteractorImpl()
                )
            )
            .build()
        genreSongComponent.inject(this)
    }

}