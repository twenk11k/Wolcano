package com.wolcano.musicplayer.music.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.kabouzeid.appthemehelper.common.ATHToolbarActivity;
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.DisposableManager;
import com.wolcano.musicplayer.music.mvp.models.Playlist;
import com.wolcano.musicplayer.music.ui.fragments.base.BaseFragment;
import com.wolcano.musicplayer.music.widgets.StatusBarView;
import com.wolcano.musicplayer.music.utils.Perms;
import com.wolcano.musicplayer.music.ui.adapter.PlaylistAdapter;
import com.wolcano.musicplayer.music.utils.SongUtils;
import com.wolcano.musicplayer.music.utils.ToastUtils;
import com.wolcano.musicplayer.music.utils.Utils;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pl.droidsonroids.gif.GifImageView;
import static com.wolcano.musicplayer.music.Constants.SONG_LIBRARY;

public class FragmentPlaylist extends BaseFragment {

    @BindView(R.id.recyclerview)
    FastScrollRecyclerView recyclerView;
    private PlaylistAdapter mAdapter;
    @BindView(R.id.statusBarCustom)
    StatusBarView statusBarView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private int color;
    @BindView(android.R.id.empty)
    TextView empty;
    private Activity activity;
    private Disposable playlistSubscription;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(view);
        color = Utils.getPrimaryColor(getContext());
        setStatusbarColorAuto(statusBarView, color);


        if (Build.VERSION.SDK_INT < 21 && view.findViewById(R.id.statusBarCustom) != null) {
            view.findViewById(R.id.statusBarCustom).setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= 19) {
                int statusBarHeight = Utils.getStatHeight(getContext());
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.findViewById(R.id.toolbar).getLayoutParams();
                layoutParams.setMargins(0, statusBarHeight, 0, 0);
                view.findViewById(R.id.toolbar).setLayoutParams(layoutParams);
            }
        }

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        toolbar.setBackgroundColor(color);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.playlists);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        color = Utils.getPrimaryColor(getContext());
        if (Utils.isColorLight(color)) {
            inflater.inflate(R.menu.menu_gif_black, menu);
        } else {
            inflater.inflate(R.menu.menu_gif, menu);
        }

        ToolbarContentTintHelper.handleOnCreateOptionsMenu(getActivity(), toolbar, menu, ATHToolbarActivity.getToolbarBackgroundColor(toolbar));

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem item = menu.getItem(0);
        FrameLayout rootView = (FrameLayout) item.getActionView();
        GifImageView imageView = (GifImageView) rootView.findViewById(R.id.ad_gif);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, Math.round(getResources().getDimension(R.dimen.gif_size)), 0);
        imageView.setLayoutParams(params);
        imageView.setBackground(getResources().getDrawable(R.drawable.btn_selector_wht));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.show(getContext(),"Gif icon is clicked!");
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_base_song, container, false);
        ButterKnife.bind(this, rootview);
        setHasOptionsMenu(true);


        Utils.setUpFastScrollRecyclerViewColor(recyclerView, Utils.getAccentColor(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        String sort = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
        setRecyclerView(sort);
        return rootview;
    }

    @Subscribe(tags = {@Tag(SONG_LIBRARY)})
    public void setRecyclerView(String sort) {
        Perms.with(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .result(new Perms.PermInterface() {
                    @Override
                    public void onPermGranted() {
                        Observable<List<Playlist>> observable =
                                Observable.fromCallable(() -> SongUtils.scanPlaylist(getContext())).throttleFirst(500, TimeUnit.MILLISECONDS);

                        playlistSubscription = observable.
                                subscribeOn(Schedulers.io()).
                                observeOn(AndroidSchedulers.mainThread()).
                                subscribe(playlists -> displayPlaylists(playlists));
                    }

                    @Override
                    public void onPermUnapproved() {
                        controlIfEmpty();
                        ToastUtils.show(activity.getApplicationContext(), R.string.no_perm_storage);
                    }
                })
                .reqPerm();
    }
    private void applyList(List<Playlist> playlistList) {
        if (playlistList.size() <= 30) {
            recyclerView.setThumbEnabled(false);
        } else {
            recyclerView.setThumbEnabled(true);
        }
        mAdapter = new PlaylistAdapter(getActivity(), playlistList);
        recyclerView.setAdapter(mAdapter);
        controlIfEmpty();
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                controlIfEmpty();
            }
        });
    }
    private void displayPlaylists(List<Playlist> playlistList) {

        applyList(playlistList);

    }

    private void controlIfEmpty() {
        if (empty != null) {
            empty.setText(R.string.no_playlist);
            empty.setVisibility(mAdapter == null || mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (playlistSubscription != null && !playlistSubscription.isDisposed()) {
            playlistSubscription.dispose();
        }

        DisposableManager.dispose();

    }



}
