package com.wolcano.musicplayer.music.ui.activities;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kabouzeid.appthemehelper.common.ATHToolbarActivity;
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper;
import com.mopub.common.MoPub;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubView;
import com.mopub.nativeads.FacebookAdRenderer;
import com.mopub.nativeads.FlurryCustomEventNative;
import com.mopub.nativeads.FlurryNativeAdRenderer;
import com.mopub.nativeads.FlurryViewBinder;
import com.mopub.nativeads.MoPubRecyclerAdapter;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.ViewBinder;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.listener.AdapterClickListener;
import com.wolcano.musicplayer.music.mvp.listener.GetDisposable;
import com.wolcano.musicplayer.music.mvp.listener.InterstitialQueueListener;
import com.wolcano.musicplayer.music.mvp.listener.OnServiceListener;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.provider.RemotePlay;
import com.wolcano.musicplayer.music.widgets.StatusBarView;
import com.wolcano.musicplayer.music.ui.adapter.QueueAdapter;
import com.wolcano.musicplayer.music.ui.dialog.Dialogs;
import com.wolcano.musicplayer.music.utils.SongUtils;
import com.wolcano.musicplayer.music.utils.Utils;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pl.droidsonroids.gif.GifImageView;


public class QueueActivity extends BaseActivity implements AdapterView.OnItemClickListener, OnServiceListener, AdapterClickListener,InterstitialQueueListener,GetDisposable {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerview)
    FastScrollRecyclerView recyclerView;
    @BindView(R.id.statusBarCustom)
    StatusBarView statusBarView;
    @BindView(android.R.id.empty)
    TextView empty;

    private QueueAdapter adapter;
    private int primaryColor = -1;
    private MoPubRecyclerAdapter myMoPubAdapter;
    private MoPubView moPubView;
    private MoPubInterstitial interstitial,interstitialGif;
    private Disposable queueDisposable;
    private int interFailC = 0;
    private int interFailCGif = 0;
    private Handler interFailHandler;
    private Runnable interFailRunnable;
    private Handler interFailHandlerGif;
    private Runnable interFailRunnableGif;
    private boolean isRunnableDoneGif = false;
    private boolean isRunnableDone = false;

    private boolean isInterstitialCalledGif = false;
    private boolean isInterstitialCalled = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        ButterKnife.bind(this);
        interFailHandler = new Handler();
        interFailHandlerGif = new Handler();

        interstitial = new MoPubInterstitial(this, "e1e9074c144044319655625e72525696");
        interstitial.load();
        interstitial.setInterstitialAdListener(new MoPubInterstitial.InterstitialAdListener() {
            @Override
            public void onInterstitialLoaded(MoPubInterstitial interstitial) {
                if (isInterstitialCalled && !isRunnableDone) {
                    interstitial.show();
                }
                isInterstitialCalled = false;
                isRunnableDone = false;
            }
            @Override
            public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
                if (interFailC >= 0 && interFailC < 2) {
                    interstitial.load();
                } else {
                    interFailRunnable = new Runnable() {
                        @Override
                        public void run() {
                            isRunnableDone = true;

                            interstitial.load();
                            interFailC = 1;
                            return;
                        }
                    };
                    interFailHandler.postDelayed(interFailRunnable, 5 * 60 * 1000);
                }
                interFailC++;
            }
            @Override
            public void onInterstitialShown(MoPubInterstitial interstitial) {
            }
            @Override
            public void onInterstitialClicked(MoPubInterstitial interstitial) {
            }
            @Override
            public void onInterstitialDismissed(MoPubInterstitial interstitial) {
                interstitial.load();
            }
        });
        interstitialGif = new MoPubInterstitial(this, "4d9cda056ed849e1a6c3cc4b917ac154");
        interstitialGif.load();
        interstitialGif.setInterstitialAdListener(new MoPubInterstitial.InterstitialAdListener() {
            @Override
            public void onInterstitialLoaded(MoPubInterstitial interstitial) {
                if (isInterstitialCalledGif && !isRunnableDoneGif) {
                    interstitialGif.show();
                }
                isInterstitialCalledGif = false;
                isRunnableDoneGif = false;
            }

            @Override
            public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
                if (interFailCGif >= 0 && interFailCGif < 2) {
                    interstitialGif.load();
                } else {
                    if (isInterstitialCalledGif) {
                        Toast.makeText(QueueActivity.this, getString(R.string.errorinter), Toast.LENGTH_LONG).show();
                        isInterstitialCalledGif = false;
                    }
                    interFailRunnableGif = new Runnable() {
                        @Override
                        public void run() {
                            isRunnableDoneGif = true;
                            interstitialGif.load();
                            interFailCGif = 1;
                            return;
                        }
                    };
                    interFailHandlerGif.postDelayed(interFailRunnableGif, 5 * 60 * 1000);
                }
                interFailCGif++;
            }

            @Override
            public void onInterstitialShown(MoPubInterstitial interstitial) {
            }

            @Override
            public void onInterstitialClicked(MoPubInterstitial interstitial) {

            }

            @Override
            public void onInterstitialDismissed(MoPubInterstitial interstitial) {
                interstitialGif.load();
            }
        });
        moPubView = (MoPubView) findViewById(R.id.adview);
        moPubView.setAdUnitId("f3b55e3961424c73bbf04175a179fe6b"); // Enter your Ad Unit ID from www.mopub.com
        moPubView.loadAd();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.clear();
        primaryColor = Utils.getPrimaryColor(this);
        if(Utils.isColorLight(primaryColor)){
            getMenuInflater().inflate(R.menu.menu_gif_black, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_gif, menu);
        }
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(this, toolbar, menu, ATHToolbarActivity.getToolbarBackgroundColor(toolbar));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
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
                showInterstitialGif();
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onServiceCon() {
        primaryColor = Utils.getPrimaryColor(this);
        setStatusbarColorAuto(statusBarView, primaryColor);

        if (Build.VERSION.SDK_INT < 21 && findViewById(R.id.statusBarCustom) != null) {
            findViewById(R.id.statusBarCustom).setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= 19) {
                int statusBarHeight = Utils.getStatHeight(this);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) findViewById(R.id.toolbar).getLayoutParams();
                layoutParams.setMargins(0, statusBarHeight, 0, 0);
                findViewById(R.id.toolbar).setLayoutParams(layoutParams);
            }
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setBackgroundColor(primaryColor);
        toolbar.setTitle(getResources().getString(R.string.nowplaying));
        Utils.setUpFastScrollRecyclerViewColor(recyclerView, Utils.getAccentColor(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));


        if (RemotePlay.get().getArrayList().size() <= 30) {
            recyclerView.setThumbEnabled(false);
        } else {
            recyclerView.setThumbEnabled(true);
        }
        adapter = new QueueAdapter(this, RemotePlay.get().getArrayList(), this,this,this::handlePlaylistDialog);
        controlIfEmpty();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                controlIfEmpty();
            }
        });
        myMoPubAdapter = new MoPubRecyclerAdapter(this, adapter);
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
        myMoPubAdapter.registerAdRenderer(facebookAdRenderer);
        myMoPubAdapter.registerAdRenderer(flurryNativeAdRenderer);
        myMoPubAdapter.registerAdRenderer(myRenderer);
        recyclerView.setAdapter(myMoPubAdapter);
        myMoPubAdapter.loadAds("66bc095167b04b84925ae859dacb917b");

        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(RemotePlay.get().getRemotePlayPos(musicService));
        adapter.setIsPlaylist(true);
        RemotePlay.get().onListener(this);
    }

    private void controlIfEmpty() {
        if (empty != null) {
            empty.setText(R.string.no_queue);
            empty.setVisibility(adapter == null || adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onChangeSong(Song song) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPlayStart() {
    }

    @Override
    public void onPlayPause() {
    }

    @Override
    public void onProgressChange(int progress) {
    }

    @Override
    public void onBufferingUpdate(int percent) {
    }

    @Override
    protected void onDestroy() {
        RemotePlay.get().removeListener(this);
        if(moPubView!=null){
          moPubView.destroy();
        }
        if(interstitial!=null){
            interstitial.destroy();
        }
        if(interstitialGif!=null){
            interstitialGif.destroy();
        }
        if(myMoPubAdapter!=null){
            myMoPubAdapter.destroy();
        }
        if (queueDisposable != null && !queueDisposable.isDisposed()) {
            queueDisposable.dispose();
        }
        if(interFailHandler!=null){
            interFailHandler.removeCallbacks(interFailRunnable);
        }
        if(interFailHandlerGif!=null){
            interFailHandlerGif.removeCallbacks(interFailRunnableGif);
        }
        MoPub.onDestroy(this);
        super.onDestroy();
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
    public void showInterstitial() {
        if(interstitial!=null){
            if(interstitial.isReady()){
                interstitial.show();
            } else {
                isInterstitialCalled = true;
                interstitial.load();
            }
        }
    }

    public void showInterstitialGif(){
        if(interstitialGif!=null){
            if(interstitialGif.isReady()){
                interstitialGif.show();
            } else {
                isInterstitialCalledGif = true;
                interstitialGif.load();

            }
        }
    }

    @Override
    public void handlePlaylistDialog(Song song) {
        queueDisposable = Observable.fromCallable(() -> SongUtils.scanPlaylist(QueueActivity.this)).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(playlists -> Dialogs.addPlaylistDialog(QueueActivity.this, song, playlists));
    }
}
