package com.wolcano.musicplayer.music.ui.fragment.library.detail;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kabouzeid.appthemehelper.common.ATHToolbarActivity;
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper;
import com.wolcano.musicplayer.music.App;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.constants.Constants;
import com.wolcano.musicplayer.music.databinding.FragmentBaseSongBinding;
import com.wolcano.musicplayer.music.di.component.ApplicationComponent;
import com.wolcano.musicplayer.music.di.component.ArtistSongComponent;
import com.wolcano.musicplayer.music.di.component.DaggerArtistSongComponent;
import com.wolcano.musicplayer.music.di.module.ArtistSongModule;
import com.wolcano.musicplayer.music.mvp.DisposableManager;
import com.wolcano.musicplayer.music.mvp.interactor.SongInteractorImpl;
import com.wolcano.musicplayer.music.mvp.listener.PlaylistListener;
import com.wolcano.musicplayer.music.mvp.models.Playlist;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.SongPresenter;
import com.wolcano.musicplayer.music.mvp.view.SongView;
import com.wolcano.musicplayer.music.ui.adapter.detail.AlbumSongAdapter;
import com.wolcano.musicplayer.music.ui.dialog.Dialogs;
import com.wolcano.musicplayer.music.ui.dialog.SleepTimerDialog;
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragment;
import com.wolcano.musicplayer.music.utils.SongUtils;
import com.wolcano.musicplayer.music.utils.Utils;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FragmentArtistDetail extends BaseFragment implements SongView, PlaylistListener {


    private Context context;
    private AlbumSongAdapter mAdapter;
    private long artistId = -1;
    private String artistName;
    private int primaryColor = -1;
    private Activity activity;
    private Disposable disposable;
    private FragmentBaseSongBinding binding;

    @Inject
    SongPresenter songPresenter;

    public static FragmentArtistDetail newInstance(long id, String name) {
        FragmentArtistDetail fragment = new FragmentArtistDetail();
        Bundle args = new Bundle();
        args.putLong(Constants.ARTIST_ID, id);
        args.putString(Constants.ARTIST_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        activity = getActivity();
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            artistId = getArguments().getLong(Constants.ARTIST_ID);
            artistName = getArguments().getString(Constants.ARTIST_NAME);
        }
        setupComponent(((App) getActivity().getApplication()).getApplicationComponent());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_base_song, container, false);

        primaryColor = Utils.getPrimaryColor(context);
        setStatusbarColorAuto(binding.statusBarCustom, primaryColor);

        if (Build.VERSION.SDK_INT < 21 && binding.statusBarCustom != null) {
            binding.statusBarCustom.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= 19) {
                int statusBarHeight = Utils.getStatHeight(context);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.toolbar.getLayoutParams();
                layoutParams.setMargins(0, statusBarHeight, 0, 0);
                binding.toolbar.setLayoutParams(layoutParams);
            }
        }

        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);

        binding.toolbar.setBackgroundColor(primaryColor);
        binding.toolbar.setTitle(artistName);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_sleeptimer, menu);
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(getActivity(), binding.toolbar, menu, ATHToolbarActivity.getToolbarBackgroundColor(binding.toolbar));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menu_sleeptimer){
            new SleepTimerDialog().show(getFragmentManager(), "SET_SLEEP_TIMER");
        }
        return super.onOptionsItemSelected(item);
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerview.setAdapter(mAdapter);
        Utils.setUpFastScrollRecyclerViewColor(binding.recyclerview, Utils.getAccentColor(getContext()));
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerview.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

       songPresenter.getArtistSongs();
        setupToolbar();
    }


    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }

        DisposableManager.dispose();

    }

    public void controlIfEmpty() {
        if (binding.empty != null) {
            binding.empty.setText(R.string.no_song);
            binding.empty.setVisibility(mAdapter == null || mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }


    private void setupToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);
        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }



    @Override
    public void handlePlaylistDialog(Song song) {
        disposable = Observable.fromCallable(new Callable<List<Playlist>>() {
            @Override
            public List<Playlist> call() throws Exception {
                return SongUtils.scanPlaylist(activity);
            }
        }).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(new Consumer<List<Playlist>>() {
                    @Override
                    public void accept(List<Playlist> playlists) throws Exception {

                        Dialogs.addPlaylistDialog(activity, song, playlists);
                    }
                });
    }

    @Override
    public void setSongList(List<Song> songList) {
        if (songList.size() <= 30) {
            binding.recyclerview.setThumbEnabled(false);
        } else {
            binding.recyclerview.setThumbEnabled(true);
        }
        mAdapter = new AlbumSongAdapter(getActivity(), songList, FragmentArtistDetail.this);
        controlIfEmpty();
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                controlIfEmpty();
            }
        });

        binding.recyclerview.setAdapter(mAdapter);
        runLayoutAnimation( binding.recyclerview);
    }

    public void setupComponent(ApplicationComponent applicationComponent) {
        String sort = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
        ArtistSongComponent artistSongComponent = DaggerArtistSongComponent.builder()
                .applicationComponent(applicationComponent)
                .artistSongModule(new ArtistSongModule(this,this,activity,sort,artistId,new SongInteractorImpl()))
                .build();
        artistSongComponent.inject(this);
    }
}
