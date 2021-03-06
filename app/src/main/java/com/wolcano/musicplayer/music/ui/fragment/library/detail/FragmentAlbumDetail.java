package com.wolcano.musicplayer.music.ui.fragment.library.detail;


import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kabouzeid.appthemehelper.ATH;
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper;
import com.squareup.picasso.Picasso;
import com.wolcano.musicplayer.music.App;
import com.wolcano.musicplayer.music.constants.Constants;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.databinding.FragmentAlbumDetailBinding;
import com.wolcano.musicplayer.music.databinding.FragmentAlbumDetailOldBinding;
import com.wolcano.musicplayer.music.di.component.AlbumSongComponent;
import com.wolcano.musicplayer.music.di.component.ApplicationComponent;
import com.wolcano.musicplayer.music.di.component.DaggerAlbumSongComponent;
import com.wolcano.musicplayer.music.di.module.AlbumSongModule;
import com.wolcano.musicplayer.music.mvp.DisposableManager;
import com.wolcano.musicplayer.music.mvp.interactor.SongInteractorImpl;
import com.wolcano.musicplayer.music.mvp.listener.PlaylistListener;
import com.wolcano.musicplayer.music.mvp.models.Playlist;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.SongPresenter;
import com.wolcano.musicplayer.music.mvp.view.SongView;
import com.wolcano.musicplayer.music.provider.RemotePlay;
import com.wolcano.musicplayer.music.ui.adapter.detail.AlbumSongAdapter;
import com.wolcano.musicplayer.music.ui.dialog.Dialogs;
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragment;
import com.wolcano.musicplayer.music.utils.SongUtils;
import com.wolcano.musicplayer.music.utils.Utils;
import com.wolcano.musicplayer.music.widgets.RotateFabBehavior;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import javax.inject.Inject;
import butterknife.OnClick;
import butterknife.Optional;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAlbumDetail extends BaseFragment implements SongView, PlaylistListener, View.OnClickListener {


    private Context context;
    private AlbumSongAdapter mAdapter;
    private long albumID = -1;
    private String albumName;
    private int primaryColor = -1, accentColor = -1;
    private Activity activity;
    private Menu menu;
    private Disposable disposable;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private FloatingActionButton fabPlay;
    private ImageView albumArt;
    private RecyclerView recyclerView;
    private View gradient;
    private int FAB_TIME = 150;
    private Handler handlerFab;
    private Runnable runnableFab;
    @Inject
    SongPresenter songPresenter;

    public static FragmentAlbumDetail newInstance(long id, String name) {
        FragmentAlbumDetail fragment = new FragmentAlbumDetail();
        Bundle args = new Bundle();
        args.putLong(Constants.ALBUM_ID, id);
        args.putString(Constants.ALBUM_NAME, name);
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
            albumID = getArguments().getLong(Constants.ALBUM_ID);
            albumName = getArguments().getString(Constants.ALBUM_NAME);
        }
        setupComponent(((App) getActivity().getApplication()).getApplicationComponent());


    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        this.menu = menu;

    }

    public void show() {
        if (Build.VERSION.SDK_INT >= 21) {
            fabPlay.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root;

        FragmentAlbumDetailBinding binding;
        FragmentAlbumDetailOldBinding bindingOld;

        if (Build.VERSION.SDK_INT >= 21) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_album_detail, container, false);
            setUpViews(binding);
            root = binding.getRoot();

        } else {
            bindingOld = DataBindingUtil.inflate(inflater, R.layout.fragment_album_detail_old, container, false);
            setUpViewsOld(bindingOld);
            root = bindingOld.getRoot();
        }
        if (Build.VERSION.SDK_INT < 21) {
            appBarLayout.setFitsSystemWindows(false);
            albumArt.setFitsSystemWindows(false);
            gradient.setFitsSystemWindows(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.darkbg1));
            }

        } else {

            CollapsingToolbarLayout.LayoutParams layoutParams = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
            layoutParams.height += Utils.getStatHeight(context);
            toolbar.setLayoutParams(layoutParams);
            toolbar.setPadding(0, Utils.getStatHeight(context), 0, 0);

        }

        primaryColor = Utils.getPrimaryColor(context);
        accentColor = Utils.getAccentColor(context);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL));


        songPresenter.getAlbumSongs();

        showAlbumArt();
        setupToolbar();
        return root;

    }

    private void setUpViewsOld(FragmentAlbumDetailOldBinding binding) {

        toolbar = binding.toolbar;
        appBarLayout = binding.appbar;
        albumArt = binding.albumArt;
        gradient = binding.gradient;
        recyclerView = binding.recyclerview;
    }

    private void setUpViews(FragmentAlbumDetailBinding binding) {

        toolbar = binding.toolbar;
        collapsingToolbarLayout = binding.collapsingtoolbar;
        appBarLayout = binding.appbar;
        fabPlay = binding.fabPlay;
        albumArt = binding.albumArt;
        recyclerView = binding.recyclerview;
        gradient = binding.gradient;
        fabPlay.setOnClickListener(this);

    }

    private void showAlbumArt() {
        Uri uri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumID);
        Picasso.get()
                .load(uri)
                .placeholder(R.color.darkbg1)
                .error(R.drawable.album_art)
                .into(albumArt);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bitmap != null) {
            new Palette.Builder(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    Palette.Swatch swatch = Utils.getMostPopulousSwatch(palette);
                    if (swatch != null) {
                        int color = swatch.getRgb();
                        color = com.kabouzeid.appthemehelper.util.ColorUtil.shiftColor(color, 0.7F);
                        collapsingToolbarLayout.setContentScrimColor(color);
                        collapsingToolbarLayout.setStatusBarScrimColor(Utils.getStatusBarColor(color));
                        if (Utils.isColorLight(color)) {
                            collapsingToolbarLayout.setCollapsedTitleTextColor(Color.BLACK);
                            collapsingToolbarLayout.setExpandedTitleColor(Color.BLACK);
                        } else {
                            collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
                            collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
                        }
                        setStatusBarColor(color);
                        ToolbarContentTintHelper.handleOnCreateOptionsMenu(activity, toolbar, menu, color);
                    }
                }
            });
        } else {
            collapsingToolbarLayout.setContentScrimColor(primaryColor);
            collapsingToolbarLayout.setStatusBarScrimColor(Utils.getStatusBarColor(primaryColor));

            if (Utils.isColorLight(primaryColor)) {
                collapsingToolbarLayout.setCollapsedTitleTextColor(Color.BLACK);
            } else {
                collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
            }
            setStatusBarColor(Color.BLACK);
            ToolbarContentTintHelper.handleOnCreateOptionsMenu(activity, toolbar, menu, Color.WHITE);


        }


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

        setStatusBarColor(primaryColor);
        if(handlerFab!=null && runnableFab!=null){
            handlerFab.removeCallbacks(runnableFab);
        }

        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }

        DisposableManager.dispose();

        super.onDestroyView();
    }

    private void setStatusBarColor(int color) {
        if (Utils.isColorLight(color)) {
            ATH.setLightStatusbar(activity, true);
        } else {
            ATH.setLightStatusbar(activity, false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 21) {
            RotateFabBehavior.show(fabPlay, accentColor, true);
        }

        toolbar.setBackgroundColor(Color.TRANSPARENT);
        if (getActivity() != null) {
            collapsingToolbarLayout.setContentScrimColor(primaryColor);
            collapsingToolbarLayout.setStatusBarScrimColor(Utils.getStatusBarColor(primaryColor));
            ToolbarContentTintHelper.handleOnCreateOptionsMenu(getActivity(), toolbar, menu, primaryColor);
            if (Utils.isColorLight(primaryColor)) {
                collapsingToolbarLayout.setCollapsedTitleTextColor(Color.BLACK);
            } else {
                collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
            }
            setStatusBarColor(primaryColor);
        }
    }

    private void setupToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        collapsingToolbarLayout.setTitle(albumName);
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
        mAdapter = new AlbumSongAdapter(context, songList, FragmentAlbumDetail.this);
        recyclerView.setAdapter(mAdapter);
        runLayoutAnimation(recyclerView);
    }

    public void setupComponent(ApplicationComponent applicationComponent) {


        String sort = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

        AlbumSongComponent albumSongComponent = DaggerAlbumSongComponent.builder()
                .applicationComponent(applicationComponent)
                .albumSongModule(new AlbumSongModule(this,this,activity,sort,albumID,new SongInteractorImpl()))
                .build();
        albumSongComponent.inject(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.fabPlay){
            handlerFab = new Handler();
            runnableFab = new Runnable() {
                @Override
                public void run() {
                    if (mAdapter != null) {
                        if (mAdapter.getSongList() != null) {
                            if (mAdapter.getSongList().size() != 0) {
                                Song song = mAdapter.getSongList().get(0);
                                RemotePlay.get().playAdd(context, mAdapter.getSongList(), song);
                            }
                        }
                    }
                }
            };
            handlerFab.postDelayed(runnableFab,FAB_TIME);
        }
    }
}
