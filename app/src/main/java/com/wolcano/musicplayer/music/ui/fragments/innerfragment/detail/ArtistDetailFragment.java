package com.wolcano.musicplayer.music.ui.fragments.innerfragment.detail;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.kabouzeid.appthemehelper.common.ATHToolbarActivity;
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper;
import com.mopub.mobileads.MoPubView;
import com.mopub.nativeads.FacebookAdRenderer;
import com.mopub.nativeads.FlurryCustomEventNative;
import com.mopub.nativeads.FlurryNativeAdRenderer;
import com.mopub.nativeads.FlurryViewBinder;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.ViewBinder;
import com.wolcano.musicplayer.music.Constants;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.DisposableManager;
import com.wolcano.musicplayer.music.mvp.listener.AdapterClickListener;
import com.wolcano.musicplayer.music.mvp.listener.GetDisposable;
import com.wolcano.musicplayer.music.mvp.models.Playlist;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.widgets.StatusBarView;
import com.wolcano.musicplayer.music.utils.Perms;
import com.wolcano.musicplayer.music.ui.activities.MainActivity;
import com.wolcano.musicplayer.music.ui.adapter.detail.AlbumSongAdapter;
import com.wolcano.musicplayer.music.ui.dialog.Dialogs;
import com.wolcano.musicplayer.music.ui.fragments.BaseFragment;
import com.wolcano.musicplayer.music.utils.SongUtils;
import com.wolcano.musicplayer.music.utils.ToastMsgUtils;
import com.wolcano.musicplayer.music.utils.Utils;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import pl.droidsonroids.gif.GifImageView;

import static com.wolcano.musicplayer.music.Constants.SONG_LIBRARY;

public class ArtistDetailFragment extends BaseFragment implements AdapterClickListener, GetDisposable {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview)
    FastScrollRecyclerView recyclerView;
    @BindView(R.id.statusBarCustom)
    StatusBarView statusBarView;
    @BindView(android.R.id.empty)
    TextView empty;
    private Context context;
    private AlbumSongAdapter mAdapter;
    private long artistId = -1;
    private String artistName;
    private int primaryColor = -1;
    private MoPubRecyclerAdapter myMoPubAdapter;
    private Activity activity;
    private MoPubView moPubView;
    private Disposable artistDetailSubscription;
    private Handler handlerInit;
    private Runnable runnableInit;
    private Handler handlerInit2;
    private Runnable runnableInit2;

    public static ArtistDetailFragment newInstance(long id, String name) {
        ArtistDetailFragment fragment = new ArtistDetailFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_base_song, container, false);
        ButterKnife.bind(this, root);
        if (Utils.getIsMopubInitDone(getContext())) {
            moPubView = (MoPubView) root.findViewById(R.id.adview);
            moPubView.setAdUnitId("f3b55e3961424c73bbf04175a179fe6b"); // Enter your Ad Unit ID from www.mopub.com
            moPubView.loadAd();

        } else {
            handlerInit = new Handler();
            runnableInit = new Runnable() {
                @Override
                public void run() {
                    if (Utils.getIsMopubInitDone(getContext())) {
                        moPubView = (MoPubView) root.findViewById(R.id.adview);
                        moPubView.setAdUnitId("f3b55e3961424c73bbf04175a179fe6b"); // Enter your Ad Unit ID from www.mopub.com
                        moPubView.loadAd();
                    } else {
                        handlerInit.postDelayed(this::run, 500);
                    }


                }
            };
            handlerInit.postDelayed(runnableInit, 500);
        }

        primaryColor = Utils.getPrimaryColor(context);
        setStatusbarColorAuto(statusBarView, primaryColor);

        if (Build.VERSION.SDK_INT < 21 && root.findViewById(R.id.statusBarCustom) != null) {
            root.findViewById(R.id.statusBarCustom).setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= 19) {
                int statusBarHeight = Utils.getStatHeight(context);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) root.findViewById(R.id.toolbar).getLayoutParams();
                layoutParams.setMargins(0, statusBarHeight, 0, 0);
                root.findViewById(R.id.toolbar).setLayoutParams(layoutParams);
            }
        }

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        toolbar.setBackgroundColor(primaryColor);
        toolbar.setTitle(artistName);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        int color = Utils.getPrimaryColor(getContext());
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
                ((MainActivity) getActivity()).showInterstitialGif();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setAdapter(mAdapter);
        Utils.setUpFastScrollRecyclerViewColor(recyclerView, Utils.getAccentColor(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        String sort = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
        setRecyclerView(sort);
        setupToolbar();
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
                                Observable.fromCallable(() -> SongUtils.scanSongsforArtist(context, sort, artistId)).throttleFirst(500, TimeUnit.MILLISECONDS);

                        artistDetailSubscription = booksObservable.
                                subscribeOn(Schedulers.io()).
                                observeOn(AndroidSchedulers.mainThread()).
                                subscribe(alist -> displayArrayList(alist));

                    }

                    @Override
                    public void onPermUnapproved() {
                        ToastMsgUtils.show(context.getApplicationContext(), R.string.no_perm_storage);
                    }
                })
                .reqPerm();
    }
    private void setMopubAdapter(List<Song> alist){
        if (alist.size() <= 30) {
            recyclerView.setThumbEnabled(false);
        } else {
            recyclerView.setThumbEnabled(true);
        }
        mAdapter = new AlbumSongAdapter(getActivity(), alist, ArtistDetailFragment.this, ArtistDetailFragment.this);
        controlIfEmpty();
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                controlIfEmpty();
            }
        });
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

        Map<String, Integer> extraToResourceMap = new HashMap<>(1);
        extraToResourceMap.put(FlurryCustomEventNative.EXTRA_SEC_BRANDING_LOGO, R.id.native_ad_privacy_information_icon_image);

        FlurryViewBinder flurryBinder = new FlurryViewBinder.Builder(new ViewBinder.Builder(R.layout.native_ad_layout)
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
            handlerInit2 = new Handler();
            runnableInit2 = new Runnable() {
                @Override
                public void run() {
                    if (Utils.getIsMopubInitDone(context.getApplicationContext())) {
                        setMopubAdapter(alist);
                    } else {
                        handlerInit2.postDelayed(this::run, 500);
                    }


                }
            };
            handlerInit2.postDelayed(runnableInit2, 500);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (moPubView != null) {
            moPubView.destroy();
        }
        if (myMoPubAdapter != null) {
            myMoPubAdapter.destroy();
        }
        if (artistDetailSubscription != null && !artistDetailSubscription.isDisposed()) {
            artistDetailSubscription.dispose();
        }
        if(handlerInit!=null && runnableInit!=null){
            handlerInit.removeCallbacks(runnableInit);
        }
        if(handlerInit2!=null && runnableInit2!=null){
            handlerInit2.removeCallbacks(runnableInit2);
        }
        DisposableManager.dispose();

    }

    private void controlIfEmpty() {
        if (empty != null) {
            empty.setText(R.string.no_song);
            empty.setVisibility(mAdapter == null || mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
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
        artistDetailSubscription = Observable.fromCallable(new Callable<List<Playlist>>() {
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
