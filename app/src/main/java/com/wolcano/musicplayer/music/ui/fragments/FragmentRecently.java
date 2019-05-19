
package com.wolcano.musicplayer.music.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;
import com.mopub.nativeads.FacebookAdRenderer;
import com.mopub.nativeads.FlurryCustomEventNative;
import com.mopub.nativeads.FlurryNativeAdRenderer;
import com.mopub.nativeads.FlurryViewBinder;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.ViewBinder;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.DisposableManager;
import com.wolcano.musicplayer.music.mvp.listener.AdapterClickListener;
import com.wolcano.musicplayer.music.mvp.listener.GetDisposable;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.widgets.StatusBarView;
import com.wolcano.musicplayer.music.utils.Perms;
import com.wolcano.musicplayer.music.ui.activities.MainActivity;
import com.wolcano.musicplayer.music.ui.adapter.RecentlyAddedAdapter;
import com.wolcano.musicplayer.music.ui.dialog.Dialogs;
import com.wolcano.musicplayer.music.utils.SongUtils;
import com.wolcano.musicplayer.music.utils.ToastMsgUtils;
import com.wolcano.musicplayer.music.utils.Utils;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pl.droidsonroids.gif.GifImageView;
import static com.wolcano.musicplayer.music.Constants.SONG_LIBRARY;

public class FragmentRecently extends BaseFragment implements MoPubView.BannerAdListener, AdapterClickListener, GetDisposable {


    private RecentlyAddedAdapter mAdapter;
    @BindView(R.id.recyclerview)
    FastScrollRecyclerView recyclerView;
    @BindView(R.id.statusBarCustom)
    StatusBarView statusBarView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(android.R.id.empty)
    TextView empty;
    private int color;
    private MoPubRecyclerAdapter myMoPubAdapter;
    private Activity activity;
    private MoPubView moPubView;
    private Disposable recentlyDisposable;
    private Handler handlerInit,handlerInit2;
    private Runnable runnableInit,runnableInit2;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        color = Utils.getPrimaryColor(getContext());
        if (statusBarView != null) {
            setStatusbarColorAuto(statusBarView, color);
        }
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
        ab.setTitle(R.string.recentlyadded);
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
        GifImageView imageView = rootView.findViewById(R.id.ad_gif);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, Math.round(getResources().getDimension(R.dimen.gif_size)), 0);
        imageView.setLayoutParams(params);
        imageView.setBackground(getResources().getDrawable(R.drawable.btn_selector_wht));
        imageView.setOnClickListener(v -> ((MainActivity) getActivity()).showInterstitialGif());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_base_song, container, false);
        activity = getActivity();
        ButterKnife.bind(this, rootview);
        setHasOptionsMenu(true);
        if (Utils.getIsMopubInitDone(activity.getApplicationContext())) {
            moPubView = (MoPubView) rootview.findViewById(R.id.adview);
            moPubView.setAdUnitId("f3b55e3961424c73bbf04175a179fe6b"); // Enter your Ad Unit ID from www.mopub.com
            moPubView.loadAd();

        } else {
            handlerInit = new Handler();
            runnableInit = new Runnable() {
                @Override
                public void run() {
                    if (Utils.getIsMopubInitDone(activity.getApplicationContext())) {
                        moPubView = (MoPubView) rootview.findViewById(R.id.adview);
                        moPubView.setAdUnitId("f3b55e3961424c73bbf04175a179fe6b"); // Enter your Ad Unit ID from www.mopub.com
                        moPubView.loadAd();
                    } else {
                        handlerInit.postDelayed(this::run, 500);
                    }


                }
            };
            handlerInit.postDelayed(runnableInit, 500);
        }
        Utils.setUpFastScrollRecyclerViewColor(recyclerView, Utils.getAccentColor(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        String sort = MediaStore.Audio.Media.DATE_ADDED + " DESC";
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
                        Observable<List<Song>> booksObservable =
                                Observable.fromCallable(() -> SongUtils.scanSongs(activity, sort)).throttleFirst(500, TimeUnit.MILLISECONDS);
                        recentlyDisposable = booksObservable.
                                subscribeOn(Schedulers.io()).
                                observeOn(AndroidSchedulers.mainThread()).
                                subscribe(alist -> displayArrayList(alist));
                    }
                    @Override
                    public void onPermUnapproved() {
                        controlIfEmpty();
                        ToastMsgUtils.show(activity.getApplicationContext(), R.string.no_perm_storage);
                    }
                })
                .reqPerm();
    }
    private void setMopubAdapter(List<Song> alist){
        if (alist.size() >= 60) {
            alist.subList(60, alist.size()).clear();
        }
        if (alist.size() <= 30) {
            recyclerView.setThumbEnabled(false);
        } else {
            recyclerView.setThumbEnabled(true);
        }
        mAdapter = new RecentlyAddedAdapter(activity, alist, FragmentRecently.this, FragmentRecently.this);
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
        MoPubStaticNativeAdRenderer myRenderer = new MoPubStaticNativeAdRenderer(viewBinder);
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
        if(Utils.getIsMopubInitDone(activity.getApplicationContext())){
            setMopubAdapter(alist);

        } else {
            handlerInit2 = new Handler();
            runnableInit2 = new Runnable() {
                @Override
                public void run() {
                    if (Utils.getIsMopubInitDone(activity.getApplicationContext())) {
                        setMopubAdapter(alist);
                    } else {
                        handlerInit2.postDelayed(this::run, 500);
                    }
                }
            };
            handlerInit2.postDelayed(runnableInit2, 500);
        }
    }

    private void controlIfEmpty() {
        if (empty != null) {
            empty.setText(R.string.no_song);
            empty.setVisibility(mAdapter == null || mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        if (myMoPubAdapter != null) {
            myMoPubAdapter.destroy();
        }
        if (moPubView != null) {
            moPubView.destroy();
        }
        if (recentlyDisposable != null && !recentlyDisposable.isDisposed()) {
            recentlyDisposable.dispose();
        }
        if(handlerInit!=null && runnableInit!=null){
            handlerInit.removeCallbacks(runnableInit);
        }
        if(handlerInit2!=null && runnableInit2!=null){
            handlerInit2.removeCallbacks(runnableInit2);
        }
        DisposableManager.dispose();
        super.onDestroyView();
    }


    @Override
    public void onBannerLoaded(MoPubView banner) {
    }

    @Override
    public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
    }

    @Override
    public void onBannerClicked(MoPubView banner) {
    }

    @Override
    public void onBannerExpanded(MoPubView banner) {
    }

    @Override
    public void onBannerCollapsed(MoPubView banner) {
    }

    @Override
    public int getOriginalPosition(int oldposition) {
        if (myMoPubAdapter != null) {
            return myMoPubAdapter.getOriginalPosition(oldposition);
        } else {
            return oldposition;
        }
    }


    @Override
    public void handlePlaylistDialog(Song song) {
        recentlyDisposable = Observable.fromCallable(() -> SongUtils.scanPlaylist(activity)).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(playlists -> Dialogs.addPlaylistDialog(activity, song, playlists));
    }
}
