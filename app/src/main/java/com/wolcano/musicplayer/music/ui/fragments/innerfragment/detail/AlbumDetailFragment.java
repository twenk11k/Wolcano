package com.wolcano.musicplayer.music.ui.fragments.innerfragment.detail;


import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.kabouzeid.appthemehelper.ATH;
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper;
import com.mopub.nativeads.FacebookAdRenderer;
import com.mopub.nativeads.FlurryCustomEventNative;
import com.mopub.nativeads.FlurryNativeAdRenderer;
import com.mopub.nativeads.FlurryViewBinder;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.ViewBinder;
import com.squareup.picasso.Picasso;
import com.wolcano.musicplayer.music.Constants;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.DisposableManager;
import com.wolcano.musicplayer.music.mvp.listener.AdapterClickListener;
import com.wolcano.musicplayer.music.mvp.listener.GetDisposable;
import com.wolcano.musicplayer.music.mvp.models.Playlist;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.provider.RemotePlay;
import com.wolcano.musicplayer.music.utils.Perms;
import com.wolcano.musicplayer.music.ui.adapter.detail.AlbumSongAdapter;
import com.wolcano.musicplayer.music.ui.dialog.Dialogs;
import com.wolcano.musicplayer.music.utils.SongUtils;
import com.wolcano.musicplayer.music.utils.ToastMsgUtils;
import com.wolcano.musicplayer.music.utils.Utils;
import com.wolcano.musicplayer.music.widgets.RotateFabBehav;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.wolcano.musicplayer.music.Constants.SONG_LIBRARY;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumDetailFragment extends Fragment implements AdapterClickListener,GetDisposable {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsingtoolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;
    FloatingActionButton fabPlay;
    @BindView(R.id.albumArt)
    ImageView albumArt;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    private Context context;
    private AlbumSongAdapter mAdapter;
    private long albumID = -1;
    private String albumName;
    private int primaryColor = -1, accentColor = -1;

    SharedPreferences settings;
    private MoPubRecyclerAdapter myMoPubAdapter;
    private Activity activity;
    private Menu menu;
    private Disposable albumDetailSubscription;
    private Handler handlerInit;
    private Runnable runnableInit;
    public static AlbumDetailFragment newInstance(long id, String name) {
        AlbumDetailFragment fragment = new AlbumDetailFragment();
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
        if(Build.VERSION.SDK_INT>=21){
             root = inflater.inflate(R.layout.album_detail, container, false);
            fabPlay = root.findViewById(R.id.fabPlay);

        } else {
             root = inflater.inflate(R.layout.album_detail_old, container, false);

        }
        ButterKnife.bind(this, root);

        if (Build.VERSION.SDK_INT < 21) {
            appBarLayout.setFitsSystemWindows(false);
            albumArt.setFitsSystemWindows(false);
            root.findViewById(R.id.gradient).setFitsSystemWindows(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getActivity().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(getResources().getColor(R.color.darkbg1));
            }

        } else {

            Toolbar toolbar =  root.findViewById(R.id.toolbar);
            CollapsingToolbarLayout.LayoutParams layoutParams = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
            layoutParams.height += Utils.getStatHeight(context);
            toolbar.setLayoutParams(layoutParams);
            toolbar.setPadding(0, Utils.getStatHeight(context), 0, 0);

        }

        settings = context.getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        primaryColor = Utils.getPrimaryColor(context);
        accentColor = Utils.getAccentColor(context);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL));
        String sort = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
        setRecyclerView(sort);
        showAlbumArt();
        setupToolbar();
        return root;
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

    @Subscribe(tags = {@Tag(SONG_LIBRARY)})
    public void setRecyclerView(String sort) {
        Perms.with(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .result(new Perms.PermInterface() {
                    @Override
                    public void onPermGranted() {
                        Observable<List<Song>> booksObservable =
                                Observable.fromCallable(() -> SongUtils.scanSongsforAlbum(context, sort, albumID)).throttleFirst(500, TimeUnit.MILLISECONDS);

                        albumDetailSubscription = booksObservable.
                                subscribeOn(Schedulers.io()).
                                observeOn(AndroidSchedulers.mainThread()).
                                subscribe(alist -> displayArrayList(alist));

                    }

                    @Override
                    public void onPermUnapproved() {
                        ToastMsgUtils.show(context.getApplicationContext(),R.string.no_perm_storage);
                    }
                })
                .reqPerm();
    }
    private void setMopubAdapter(List<Song> alist){
        mAdapter = new AlbumSongAdapter(context, alist, AlbumDetailFragment.this,AlbumDetailFragment.this);
        myMoPubAdapter = new MoPubRecyclerAdapter(activity, mAdapter);

        ViewBinder viewBinder = new ViewBinder.Builder(R.layout.native_ad_layout)
                .iconImageId(R.id.native_icon_image)
                .titleId(R.id.native_ad_title)

                .privacyInformationIconImageId(R.id.native_ad_privacy_information_icon_image)
                .callToActionId(R.id.native_cta)
                .build();
        FacebookAdRenderer.FacebookViewBinder facebookViewBinder = new FacebookAdRenderer.FacebookViewBinder.Builder(R.layout.native_ad_layout_fan)
                .adIconViewId(R.id.native_icon_image)
                .titleId(R.id.native_ad_title)
                .adChoicesRelativeLayoutId(R.id.native_ad_privacy_information_icon_image)
                .callToActionId(R.id.native_cta)
                .build();
        FacebookAdRenderer facebookAdRenderer = new FacebookAdRenderer(facebookViewBinder);
        Map<String,Integer> extraToResourceMap=new HashMap<>(1);
        extraToResourceMap.put(FlurryCustomEventNative.EXTRA_SEC_BRANDING_LOGO, R.id.native_ad_privacy_information_icon_image);

        FlurryViewBinder flurryBinder = new FlurryViewBinder.Builder( new ViewBinder.Builder(R.layout.native_ad_layout)
                .iconImageId(R.id.native_icon_image)
                .titleId(R.id.native_ad_title)
                .callToActionId(R.id.native_cta)
                .addExtras(extraToResourceMap)// <-- adding the extras to your Binder
                .build())
                .build();
        FlurryNativeAdRenderer flurryNativeAdRenderer = new FlurryNativeAdRenderer(flurryBinder);

        MoPubStaticNativeAdRenderer myRenderer = new MoPubStaticNativeAdRenderer(viewBinder);
        myMoPubAdapter.registerAdRenderer(facebookAdRenderer);
        myMoPubAdapter.registerAdRenderer(flurryNativeAdRenderer);
        myMoPubAdapter.registerAdRenderer(myRenderer);
        recyclerView.setAdapter(myMoPubAdapter);
        myMoPubAdapter.loadAds("66bc095167b04b84925ae859dacb917b");
        mAdapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);

    }
    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    private void displayArrayList(List<Song> alist) {
        if(Utils.getIsMopubInitDone(context.getApplicationContext())){
            setMopubAdapter(alist);

        } else {
            handlerInit = new Handler();
            runnableInit = new Runnable() {
                @Override
                public void run() {
                    if (Utils.getIsMopubInitDone(context.getApplicationContext())) {
                        setMopubAdapter(alist);
                    } else {
                        handlerInit.postDelayed(this::run, 500);
                    }

                }
            };

            handlerInit.postDelayed(runnableInit, 500);
        }

    }

    @Override
    public void onDestroyView() {
        setStatusBarColor(primaryColor);
        if (myMoPubAdapter != null) {
            myMoPubAdapter.destroy();
        }
        if (albumDetailSubscription != null && !albumDetailSubscription.isDisposed()) {
            albumDetailSubscription.dispose();
        }
        if(handlerInit!=null && runnableInit!=null){
            handlerInit.removeCallbacks(runnableInit);
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
            RotateFabBehav.show(fabPlay, accentColor, true);
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

    @Optional
    @Nullable
    @OnClick(R.id.fabPlay)
    public void onFabPlayClick() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mAdapter!=null){
                    if(mAdapter.getArraylist()!=null){
                        if (mAdapter.getArraylist().size() != 0) {
                            Song song = mAdapter.getArraylist().get(0);
                            RemotePlay.get().playAdd(context,mAdapter.getArraylist(), song);
                        }
                    }
                }
            }
        }, 150);
    }

    @Override
    public int getOriginalPosition(int oldposition) {
        if(myMoPubAdapter!=null){
            return myMoPubAdapter.getOriginalPosition(oldposition);
        } else {
            return oldposition;
        }
    }
    @Override
    public void handlePlaylistDialog(Song song) {
        albumDetailSubscription = Observable.fromCallable(new Callable<List<Playlist>>(){
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
}
