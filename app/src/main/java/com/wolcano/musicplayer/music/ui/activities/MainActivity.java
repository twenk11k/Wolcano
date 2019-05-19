package com.wolcano.musicplayer.music.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.applovin.sdk.AppLovinSdk;
import com.facebook.ads.AdSettings;
import com.kabouzeid.appthemehelper.ATH;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.util.ATHUtil;
import com.kabouzeid.appthemehelper.util.NavigationViewUtil;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.logging.MoPubLog;
import com.mopub.common.privacy.ConsentDialogListener;
import com.mopub.common.privacy.ConsentStatus;
import com.mopub.common.privacy.ConsentStatusChangeListener;
import com.mopub.common.privacy.PersonalInfoManager;
import com.mopub.mobileads.GooglePlayServicesBanner;
import com.mopub.mobileads.GooglePlayServicesInterstitial;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.squareup.picasso.Picasso;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.DisposableManager;
import com.wolcano.musicplayer.music.mvp.listener.GetDisposable;
import com.wolcano.musicplayer.music.mvp.listener.InterstitialListener;
import com.wolcano.musicplayer.music.mvp.listener.OnServiceListener;
import com.wolcano.musicplayer.music.mvp.models.ModelBitmap;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.provider.RemotePlay;
import com.wolcano.musicplayer.music.content.slidingpanel.SlidingPanel1;
import com.wolcano.musicplayer.music.content.LeftButtonEnumVals;
import com.wolcano.musicplayer.music.ui.dialog.RatingDialogV2;
import com.wolcano.musicplayer.music.utils.SongCover;
import com.wolcano.musicplayer.music.utils.Perms;
import com.wolcano.musicplayer.music.widgets.ModelView;
import com.wolcano.musicplayer.music.ui.dialog.Dialogs;
import com.wolcano.musicplayer.music.ui.dialog.SleepTimerDialog;
import com.wolcano.musicplayer.music.ui.fragments.FragmentRecently;
import com.wolcano.musicplayer.music.ui.fragments.FragmentLibrary;
import com.wolcano.musicplayer.music.ui.fragments.FragmentMain;
import com.wolcano.musicplayer.music.ui.fragments.FragmentPlaylist;
import com.wolcano.musicplayer.music.ui.helper.SongHelperMenu;
import com.wolcano.musicplayer.music.utils.SongUtils;
import com.wolcano.musicplayer.music.utils.ToastMsgUtils;
import com.wolcano.musicplayer.music.utils.UriFilesUtils;
import com.wolcano.musicplayer.music.utils.Utils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener, OnServiceListener, InterstitialListener, GetDisposable {

    @BindView(R.id.child2linear)
    LinearLayout child2linear;
    @BindView(R.id.child2bg)
    ImageView child2bg;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.innerLinearTopOne)
    LinearLayout innerLinearTopOne;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.artist)
    TextView artist;
    @BindView(R.id.modelcover)
    ModelView modelCoverView;
    @BindView(R.id.seekbar)
    SeekBar theSeekbar;
    @BindView(R.id.current)
    TextView current;
    @BindView(R.id.total)
    TextView total;
    @BindView(R.id.leftbutton)
    ImageView leftButton;
    @BindView(R.id.play)
    ImageView play;
    @BindView(R.id.next)
    ImageView next;
    @BindView(R.id.prev)
    ImageView prev;
    @BindView(R.id.queue)
    ImageView queue;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navView)
    NavigationView navigationView;
    @BindView(R.id.slidinguppanellayout)
    SlidingUpPanelLayout mSlidingUpPanelLayout;
    @BindView(R.id.slidinguppanel_top1)
    FrameLayout frameTopOne;


    private boolean isDragging;
    SlidingPanel1 slidingPanel1;

    ImageView imageHeaderBg;
    private TextView navHeaderTitle, navHeaderArtist, navHeaderAppName;
    private ImageView navHeaderIcon;
    private RelativeLayout navHeaderLinear;
    private boolean isExpand = false;
    private int olshared = -1;
    private MenuItem recentlyAddedMenu;
    private Context context;
    private boolean lightStatusbar;
    private boolean mIsResumed = true;
    private MoPubInterstitial mInterstitial;
    private MoPubInterstitial mInterstitialGif;
    private Activity activity;
    private Disposable mainActDisposable;
    private Drawable placeholder;
    private int interFailCGif = 0;
    private boolean isRunnableDoneGif = false;
    private boolean isRunnableDone = false;
    private int interFailC = 0;
    private Handler interFailHandler, interFailHandlerGif, handlerD, handlerCollapse;
    private Runnable interFailRunnable, interFailRunnableGif, runnableD;
    private int currentFrag;
    private boolean isInterstitialCalledGif = false;
    private boolean isInterstitialCalled = false;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AdSettings.addTestDevice("d53e3e13-bdd5-45c5-b84a-1c347bafc009");
        if(!MoPub.isSdkInitialized()){
            Utils.setIsMopubInitDone(this.getApplicationContext(), false);
        } else {
            Utils.setIsMopubInitDone(this.getApplicationContext(), true);
        }
        context = this;
        activity = this;
        ButterKnife.bind(this);
        setDrawerOptions();
        initMopub();
        initViews();
        displayDialog();
        handleRateDialog();
    }

    private SdkInitializationListener initSdkListener() {
        return () -> {
            Utils.setIsMopubInitDone(context, true);

       /* MoPub SDK initialized.
       Check if you should show the consent dialog here, and make your ad requests. */
            PersonalInfoManager mPersonalInfoManager = MoPub.getPersonalInformationManager();
            ConsentDialogListener consentDialogListener = new ConsentDialogListener() {

                @Override
                public void onConsentDialogLoaded() {
                    if (mPersonalInfoManager != null) {
                        mPersonalInfoManager.showConsentDialog();
                    }
                    mPersonalInfoManager.subscribeConsentStatusChangeListener(new ConsentStatusChangeListener() {
                        @Override
                        public void onConsentStateChange(@NonNull ConsentStatus oldConsentStatus, @NonNull ConsentStatus newConsentStatus, boolean canCollectPersonalInformation) {
                            StartAppSDK.setUserConsent(MainActivity.this,
                                    "pas",
                                    System.currentTimeMillis(),
                                    canCollectPersonalInformation);

                        }
                    });
                }

                @Override
                public void onConsentDialogLoadFailed(@NonNull MoPubErrorCode moPubErrorCode) {
                    MoPubLog.i("Consent dialog failed to load.");
                }
            };

            if (mPersonalInfoManager.shouldShowConsentDialog()) {
                mPersonalInfoManager.loadConsentDialog(consentDialogListener);
            } else {
                initStartapp();
                initApplovin();
                initInterstitial();

            }

        };
    }

    private void initMopub() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bundle extras = new Bundle();
                extras.putString("npa", "1");

                List<String> networksToInit = new ArrayList();
                networksToInit.add("com.mopub.mobileads.FacebookBanner");
                networksToInit.add("com.mopub.mobileads.FacebookInterstitial");
                networksToInit.add("com.mopub.mobileads.StartAppCustomEventMedium");
                networksToInit.add("com.mopub.mobileads.StartAppCustomEventBanner");
                networksToInit.add("com.mopub.mobileads.StartAppCustomEventInterstitial");
                networksToInit.add("com.mopub.mobileads.AppLovinBanner");
                networksToInit.add("com.mopub.mobileads.AppLovinInterstitial");
                networksToInit.add("com.mopub.mobileads.IronSourceInterstitial");
                networksToInit.add("com.mopub.mobileads.GooglePlayServicesBanner");
                networksToInit.add("com.mopub.mobileads.GooglePlayServicesInterstitial");
                networksToInit.add("com.mopub.mobileads.FlurryAgentWrapper");
                networksToInit.add("com.mopub.mobileads.FacebookAdvancedBidder");
                SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder("19571a07c85d430299f931d646c19296")
                        .withMediationSettings(new GooglePlayServicesBanner.GooglePlayServicesMediationSettings(extras),
                                new GooglePlayServicesInterstitial.GooglePlayServicesMediationSettings(extras))
                        .withNetworksToInit(networksToInit)
                        .build();

                MoPub.initializeSdk(MainActivity.this, sdkConfiguration, initSdkListener());
            }
        });


    }

    public void initInterstitial() {

        interFailHandler = new Handler();
        interFailHandlerGif = new Handler();
        mInterstitial = new MoPubInterstitial(this, "e1e9074c144044319655625e72525696");
        mInterstitial.setInterstitialAdListener(new MoPubInterstitial.InterstitialAdListener() {
            @Override
            public void onInterstitialLoaded(MoPubInterstitial interstitial) {
                if (isInterstitialCalled && !isRunnableDone) {
                    mInterstitial.show();
                }
                isInterstitialCalled = false;
                isRunnableDone = false;
            }

            @Override
            public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
                if (interFailC >= 0 && interFailC < 2) {
                     mInterstitial.load();
                } else {
                    interFailRunnable = new Runnable() {
                        @Override
                        public void run() {
                            isRunnableDone = true;
                            mInterstitial.load();
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
                mInterstitial.load();

            }
        });
        mInterstitial.load();

        mInterstitialGif = new MoPubInterstitial(this, "4d9cda056ed849e1a6c3cc4b917ac154");
        mInterstitialGif.setInterstitialAdListener(new MoPubInterstitial.InterstitialAdListener() {
            @Override
            public void onInterstitialLoaded(MoPubInterstitial interstitial) {
                if (isInterstitialCalledGif && !isRunnableDoneGif) {
                    mInterstitialGif.show();
                }
                isInterstitialCalledGif = false;
                isRunnableDoneGif = false;
            }

            @Override
            public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
                if (interFailCGif >= 0 && interFailCGif < 2) {
                    mInterstitialGif.load();
                } else {
                    if (isInterstitialCalledGif) {
                        Toast.makeText(MainActivity.this, getString(R.string.errorinter), Toast.LENGTH_LONG).show();
                        isInterstitialCalledGif = false;
                    }
                    interFailRunnableGif = new Runnable() {
                        @Override
                        public void run() {
                            isRunnableDoneGif = true;
                            mInterstitialGif.load();
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
                mInterstitialGif.load();
            }
        });
        mInterstitialGif.load();
    }

    private void handleRateDialog() {
        final RatingDialogV2 ratingDialog = new RatingDialogV2.Builder(this)
                .threshold(4)
                .session(3)
                .title(getString(R.string.rating_title))
                .formCancelText(getString(R.string.rating_cancel))
                .formSubmitText(getString(R.string.rating_submit))
                .formTitle(getString(R.string.form_title))
                .positiveButtonText(getString(R.string.form_later))
                .negativeButtonText(getString(R.string.form_never))
                .formHint(getString(R.string.form_hint))
                .build();

        ratingDialog.show();

    }

    public void initStartapp() {
        StartAppSDK.init(activity, "205611346", false);
        StartAppAd.disableSplash();
    }

    public Toolbar getToolbar() {
        return findViewById(R.id.toolbar);
    }

    private void displayDialog() {
        boolean first = Utils.getFirst(context);
        if (Locale.getDefault().getLanguage().equals("tr") || Locale.getDefault().getLanguage().equals("es") || Locale.getDefault().getLanguage().equals("pt") || Locale.getDefault().getLanguage().equals("th") && Utils.isItTrue(context)) {
            if (first) {
                String str;
                if (Build.VERSION.SDK_INT >= 23) {
                    str = getString(R.string.first_dec);
                } else {
                    str = getString(R.string.first_dec_old);
                }
                new MaterialDialog.Builder(this)
                        .title(R.string.first_title)
                        .content(str)
                        .theme(Theme.DARK)
                        .btnStackedGravity(GravityEnum.END)
                        .positiveText(R.string.close)
                        .canceledOnTouchOutside(false)
                        .show();
                Utils.setFirst(context, false);
            }
        } else {
            Utils.setFirst(context, false);
        }
    }

    private void initApplovin() {
        AppLovinSdk.initializeSdk(this);
        final AppLovinSdk sdk = AppLovinSdk.getInstance(context);
        sdk.getSettings().setTestAdsEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void prev() {
        RemotePlay.get().prev(context);
    }
    private void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }
    private void setDrawerOptions() {
        if (!Locale.getDefault().getLanguage().equals("tr") && !Locale.getDefault().getLanguage().equals("es") && !Locale.getDefault().getLanguage().equals("pt") && !Locale.getDefault().getLanguage().equals("th")) {
          navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_drawer_2);
        }
        recentlyAddedMenu = navigationView.getMenu().findItem(R.id.nav_recentlyadded);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            Utils.setStatusBarTranslucent(getWindow());
            drawerLayout.setFitsSystemWindows(false);
            navigationView.setFitsSystemWindows(false);
            findViewById(R.id.slidinguppanellayout).setFitsSystemWindows(false);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawerLayout.setOnApplyWindowInsetsListener((view, windowInsets) -> {
                navigationView.dispatchApplyWindowInsets(windowInsets);
                return windowInsets.replaceSystemWindowInsets(0, 0, 0, 0);
            });
        }
        disableNavigationViewScrollbars(navigationView);
        handlerD = new Handler();
        handlerCollapse = new Handler();
        runnableD = new Runnable() {
            @Override
            public void run() {
                if (navigationView != null) {
                    int getFromShared = Utils.getCountSave(getApplicationContext());
                    if (getFromShared == 0) {
                        if (olshared != getFromShared) {
                            olshared = getFromShared;
                            recentlyAddedMenu.setTitle(getResources().getString(R.string.recentlyadded));
                        }
                    } else {
                        if (olshared != getFromShared) {
                            olshared = getFromShared;
                            setBadge(getFromShared);
                        }
                    }
                    handlerD.postDelayed(this::run, 500);
                }
            }
        };
        handlerD.post(runnableD);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                Utils.hideKeyboard(MainActivity.this);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

    }

    private void setBadge(int count) {
        recentlyAddedMenu.setTitle(getResources().getString(R.string.recentlyadded));
        String laststr = recentlyAddedMenu.getTitle().toString();
        String counter = Integer.toString(count);
        String s = laststr + "   " + counter + " ";
        SpannableString sColored = new SpannableString(s);
        sColored.setSpan(new RelativeSizeSpan(1.2f), s.length() - 3, s.length(), 0);
        sColored.setSpan(new ForegroundColorSpan(Utils.getAccentColor(this)), s.length() - 3, s.length(), 0);
        recentlyAddedMenu.setTitle(sColored);
    }
    private void setLeftButtonMode() {
        LeftButtonEnumVals mode = LeftButtonEnumVals.valueOf(Utils.getPlaylistId(context));
        switch (mode) {
            case DUZ:
                mode = LeftButtonEnumVals.KARISIK;
                break;
            case KARISIK:
                mode = LeftButtonEnumVals.TEK;
                break;
            case TEK:
                mode = LeftButtonEnumVals.DUZ;
                break;
        }
        Utils.setPlaylistId(context, mode.getVal());
        setLeftButton();
    }

    @Override
    public void onBackPressed() {
        if (isExpand) {
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else if (!isExpand) {

            if (!getIfMainVisible()) {

                int fragments = getSupportFragmentManager().getBackStackEntryCount();
                if (fragments == 1) {
                    moveTaskToBack(true);
                } else {
                    if (getFragmentManager().getBackStackEntryCount() > 1) {
                        getFragmentManager().popBackStack();

                    } else {
                        super.onBackPressed();

                    }
                }
            }
        }
    }


    @Override
    protected void onServiceCon() {
        slidingPanel1 = new SlidingPanel1(frameTopOne, this);
        RemotePlay.get().onListener(slidingPanel1);
        handleIntent();
        showPlayView();

    }

    public void handleIntent() {
        Intent intent = getIntent();
        if (intent.getAction().equals(Intent.ACTION_VIEW) && !Utils.getRecreated(this)) {
            startPlaybackFromUri(intent.getData());
        }
        Utils.setRecreated(this, false);
    }

    private void startPlaybackFromUri(Uri songUri) {
        String songPath = UriFilesUtils.getPathFromUri(this, songUri);
        List<Song> intent_list = buildQueueFromFileUri(songUri);
        for (int i = 0; i < intent_list.size(); i++) {
            if (intent_list.get(i).getPath().equals(songPath)) {
                RemotePlay.get().playAdd(context, intent_list, intent_list.get(i));
                break;
            }
        }

    }

    private List<Song> buildQueueFromFileUri(Uri fileUri) {
        String path = UriFilesUtils.getPathFromUri(this, fileUri);
        if (path == null || path.trim().isEmpty()) {
            return Collections.emptyList();
        }

        File file = new File(path);
        return SongUtils.buildSongListFromFile(this, file);
    }

    public void showPlayView() {

        modelCoverView.initHelper(RemotePlay.get().isPlaying());
        setLeftButton();
        onSongChange(RemotePlay.get().getPlayMusic(context));
        RemotePlay.get().onListener(this);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent();
    }

    public void initViews() {

        setNavigationDrawer();
        setSlidingPanelLayout();
        int openingVal;

        if (Locale.getDefault().getLanguage().equals("tr") || Locale.getDefault().getLanguage().equals("es") || Locale.getDefault().getLanguage().equals("pt") || Locale.getDefault().getLanguage().equals("th")) {
            openingVal = Utils.getOpeningVal(MainActivity.this);
            Utils.setFirstFrag(this.getApplicationContext(),true);
            setFragment(openingVal);
            navigationView.getMenu().getItem(openingVal).setChecked(true);
        } else {
            openingVal = Utils.getOpeningVal2(MainActivity.this);
            setFragment(openingVal);
            navigationView.getMenu().getItem(openingVal - 4).setChecked(true);
        }
        initViews2();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == theSeekbar) {
            current.setText(Utils.getDuraStr(progress / 1000, this));
        }
    }
    private void initViews2() {
        NavigationViewUtil.setItemIconColors(navigationView, ATHUtil.resolveColor(this, R.attr.iconColor, ThemeStore.textColorSecondary(this)), Utils.getAccentColor(this));
        NavigationViewUtil.setItemTextColors(navigationView, ThemeStore.textColorPrimary(this), Utils.getAccentColor(this));

        // header impl
        View header = navigationView.getHeaderView(0);
        navHeaderTitle = header.findViewById(R.id.nav_header_title);
        navHeaderArtist = header.findViewById(R.id.nav_header_artist);
        navHeaderLinear = header.findViewById(R.id.nav_header_relative);
        navHeaderAppName = header.findViewById(R.id.nav_header_app_name);
        navHeaderIcon = header.findViewById(R.id.nav_header_image);
        imageHeaderBg = header.findViewById(R.id.headerback);
        theSeekbar.getProgressDrawable().setColorFilter(Utils.getAccentColor(this), PorterDuff.Mode.SRC_IN);
        theSeekbar.getThumb().setColorFilter(Utils.getAccentColor(this), PorterDuff.Mode.SRC_IN);
        navHeaderLinear.setOnClickListener(this::onClick);
        queue.setColorFilter(getResources().getColor(R.color.grey0));

        placeholder = getResources().getDrawable(R.drawable.album_default);
        placeholder.setColorFilter(Utils.getPrimaryColor(this), PorterDuff.Mode.MULTIPLY);
    }
    @Override
    protected void handleListener() {
        back.setOnClickListener(this);
        menu.setOnClickListener(this);
        innerLinearTopOne.setOnClickListener(this);
        leftButton.setOnClickListener(this);
        play.setOnClickListener(this);
        prev.setOnClickListener(this);
        next.setOnClickListener(this);
        queue.setOnClickListener(this);
        theSeekbar.setOnSeekBarChangeListener(this);
    }
    @Override
    protected void onStop() {
        super.onStop();
        MoPub.onStop(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (frameTopOne.getAlpha() == 1.0 && !isExpand) {
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        if (Utils.getColorSelection(this)) {
            SongCover.get().setCacheDefault(context);
            Utils.setColorSelection(this, false);
            Utils.setRecreated(this, true);
            updateUiSettings();
        }
        mIsResumed = true;
        MoPub.onResume(this);
    }

    private void updateUiSettings() {
        slidingPanel1.setViews();
        mIsResumed = true;
        setFragment(currentFrag);
        initViews2();
        onChangeSong(RemotePlay.get().getPlayMusic(this));
    }
    @Override
    protected void onPause() {
        super.onPause();
        mIsResumed = false;
        MoPub.onPause(this);
    }

    @Override
    public void setLightStatusbar(boolean enabled) {
        lightStatusbar = enabled;
        if (getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            super.setLightStatusbar(enabled);
        }
    }

    public SlidingUpPanelLayout.PanelState getPanelState() {
        return mSlidingUpPanelLayout == null ? null : mSlidingUpPanelLayout.getPanelState();
    }

    public void setSlidingPanelLayout() {
        mSlidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (slideOffset >= 0 && slideOffset < 1) {
                    frameTopOne.setVisibility(View.VISIBLE);
                }
                frameTopOne.setAlpha(1 - slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                switch (newState) {
                    case COLLAPSED:
                        frameTopOne.setVisibility(View.VISIBLE);
                        isExpand = false;
                        ATH.setLightStatusbar(MainActivity.this, lightStatusbar);
                        break;
                    case EXPANDED:
                        frameTopOne.setVisibility(View.GONE);
                        ATH.setLightStatusbar(MainActivity.this, false);
                        isExpand = true;
                        break;
                    case ANCHORED:
                        handlerCollapse.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                collapsePanel();
                            }
                        }, 250);
                        break;
                }
            }
        });
        if (Build.VERSION.SDK_INT >= 19)
            child2linear.setPadding(0, Utils.getStatHeight(this), 0, 0);

    }

    private void next() {
        RemotePlay.get().next(context, false);
    }

    public void expandPanel() {
        mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    public void collapsePanel() {
        if (mSlidingUpPanelLayout != null)
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    @Override
    protected void onDestroy() {

        RemotePlay.get().removeListener(this);

        if (mInterstitial != null) {
            mInterstitial.destroy();
        }
        if (mInterstitialGif != null) {
            mInterstitialGif.destroy();
        }
        if (mainActDisposable != null && !mainActDisposable.isDisposed()) {
            mainActDisposable.dispose();
        }
        if (slidingPanel1.bitmapSubscription != null && !slidingPanel1.bitmapSubscription.isDisposed()) {
            slidingPanel1.bitmapSubscription.dispose();
        }
        if (interFailHandler != null) {
            interFailHandler.removeCallbacks(interFailRunnable);
        }
        if (interFailHandlerGif != null) {
            interFailHandlerGif.removeCallbacks(interFailRunnableGif);
        }
        if (handlerD != null) {
            handlerD.removeCallbacks(runnableD);
        }

        MoPub.onDestroy(this);
        DisposableManager.dispose();

        super.onDestroy();
    }

    public void setNavigationDrawer() {

        NavigationView navigationView = (NavigationView) findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Hide keyboard
        Utils.hideKeyboard(this);
        int id = item.getItemId();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                Utils.setFirstFrag(MainActivity.this,false);

                if (id == R.id.nav_folders) {
                    setFragment(0);
                } else if (id == R.id.nav_recentlyadded) {
                    Utils.setCountSave(MainActivity.this, 0);
                    setFragment(1);
                } else if (id == R.id.nav_library) {
                    setFragment(2);
                } else if (id == R.id.nav_playlists) {
                    setFragment(3);
                } else if (id == R.id.nav_sleep_timer) {
                    new SleepTimerDialog().show(getSupportFragmentManager(), "SET_SLEEP_TIMER");
                }
                if (id == R.id.nav_settings) {
                    Perms.with(MainActivity.this)
                            .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .result(new Perms.PermInterface() {

                                @Override
                                public void onPermGranted() {
                                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void onPermUnapproved() {
                                    ToastMsgUtils.show(getApplicationContext(), R.string.no_perm_open_settings);
                                }
                            })
                            .reqPerm();

                } else if (id == R.id.nav_like) {
                    Dialogs.likeDialog(context);
                }
            }
        }, 350);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (seekBar == theSeekbar) {
            isDragging = true;
        }
    }
    public boolean getIfMainVisible() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (fragment instanceof FragmentMain) {
            if (((FragmentMain) fragment).isSearchOpen()) {
                ((FragmentMain) fragment).closeSearch();
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    public void setFragment(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        currentFrag = position;
        if (position == 0) {
            if (mIsResumed)
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            Fragment fragmentMain = new FragmentMain();
            fragmentTransaction.replace(R.id.fragment, fragmentMain);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (position == 1 || position == 5) {
            if (mIsResumed)
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            Fragment fragmentRecently= new FragmentRecently();
            fragmentTransaction.replace(R.id.fragment, fragmentRecently);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (position == 2 || position == 4) {
            if (mIsResumed)
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            Fragment fragmentLibrary = new FragmentLibrary();
            fragmentTransaction.replace(R.id.fragment, fragmentLibrary);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else if (position == 3 || position == 6) {
            if (mIsResumed)
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            Fragment fragmentPlaylists = new FragmentPlaylist();
            fragmentTransaction.replace(R.id.fragment, fragmentPlaylists);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar == theSeekbar) {
            isDragging = false;
            if (RemotePlay.get().isPlaying() || RemotePlay.get().isPausing()) {
                int progress = seekBar.getProgress();
                RemotePlay.get().seekTo(progress);
            } else {
                seekBar.setProgress(0);
            }
        }
    }


    @Override
    public void onChangeSong(Song song) {
        onSongChange(song);
    }

    @Override
    public void onPlayStart() {
        play.setSelected(true);
        modelCoverView.start();
    }

    @Override
    public void onPlayPause() {
        play.setSelected(false);
        modelCoverView.pause();
    }

    @Override
    public void onProgressChange(int progress) {
        if (!isDragging) {
            theSeekbar.setProgress(progress);
        }
    }

    @Override
    public void onBufferingUpdate(int percent) {
        if (percent != 0)
            theSeekbar.setSecondaryProgress(theSeekbar.getMax() * 100 / percent);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.menu:
                if (RemotePlay.get().getPlayMusic(context) != null) {
                    if (RemotePlay.get().getPlayMusic(context).getTip() == Song.Tip.MODEL0) {
                        SongHelperMenu.handleMenuLocal(this, v, RemotePlay.get().getPlayMusic(context), this::handlePlaylistDialog);
                    } else {
                        SongHelperMenu.handleMenuFolder(this, v, RemotePlay.get().getPlayMusic(context), this);
                    }
                }
                break;
            case R.id.innerLinearTopOne:
                onBackPressed();
                break;
            case R.id.leftbutton:
                setLeftButtonMode();
                break;
            case R.id.play:
                play();
                break;
            case R.id.next:
                next();
                break;
            case R.id.prev:
                prev();
                break;
            case R.id.queue:
                if (!RemotePlay.get().getArrayList().isEmpty()) {
                    Intent intent = new Intent(this, QueueActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.empty_queue), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_header_relative:
                drawerLayout.closeDrawers();
                mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                break;
        }
    }

    private void play() {
        RemotePlay.get().buttonClick(context);
    }

    public void setLeftButton() {
        int mode = Utils.getPlaylistId(context);
        leftButton.setImageLevel(mode);
    }

    private void setAlbumCover(Song song) {
        loadBitmap(song, child2bg, modelCoverView);
    }

    private void setBitmap(List<ModelBitmap> bitmapList, ImageView imageView, ModelView modelCoverView) {
        ModelBitmap blur, round;
        blur = bitmapList.get(0);
        round = bitmapList.get(1);
        if (blur == null) {
            imageView.setImageDrawable(placeholder);
        } else {
            imageView.setImageBitmap(blur.getBitmap());
        }
        if (round == null) {
            modelCoverView.setRoundBitmap(SongCover.get().getMainModel(context, SongCover.Tip.OVAL));
        } else {
            if (round.getId() == 0) {
                modelCoverView.setRoundBitmap(round.getBitmap());
            } else if (round.getId() == 1) {
                modelCoverView.setRoundBitmap(SongCover.get().getMainModel(context, SongCover.Tip.OVAL));
            }
        }


    }

    public void loadBitmap(Song song, ImageView imageView, ModelView modelCoverView) {

        Observable<List<ModelBitmap>> bitmap1Observable =
                Observable.fromCallable(() -> {
                    List<ModelBitmap> bitmapList = new ArrayList<>();
                    bitmapList.add(SongCover.get().loadBlurredModel(context, song));
                    bitmapList.add(SongCover.get().loadOvalModel(context, song));
                    return bitmapList;
                }).throttleFirst(500, TimeUnit.MILLISECONDS);
        mainActDisposable = bitmap1Observable.
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(bitmap -> setBitmap(bitmap, imageView, modelCoverView));
    }

    public void onSongChange(Song song) {
        if (song == null) {

            title.setText("");
            artist.setText("");
            current.setText(R.string.start);
            total.setText(R.string.start);
            theSeekbar.setSecondaryProgress(0);
            theSeekbar.setProgress(0);
            theSeekbar.setMax(0);
            navHeaderTitle.setText("");
            navHeaderArtist.setText("");
            imageHeaderBg.setImageResource(android.R.color.transparent);
            imageHeaderBg.setBackgroundColor(getResources().getColor(R.color.drawerhead));
            navHeaderAppName.setVisibility(View.VISIBLE);
            navHeaderIcon.setVisibility(View.VISIBLE);

            setAlbumCover(song);
            return;
        }
        title.setText(song.getTitle());
        artist.setText(song.getArtist());
        theSeekbar.setProgress((int) RemotePlay.get().getSoundPos());
        theSeekbar.setSecondaryProgress(0);
        theSeekbar.setMax((int) song.getDura());
        current.setText(R.string.start);
        total.setText(Utils.getDuraStr(song.getDura() / 1000, this));
        setAlbumCover(song);
        if (RemotePlay.get().isPlaying() || RemotePlay.get().isPreparing()) {
            play.setSelected(true);
            modelCoverView.start();
        } else {
            play.setSelected(false);
            modelCoverView.pause();
        }
        if (navHeaderTitle != null)
            navHeaderTitle.setText(song.getTitle());
        if (navHeaderArtist != null)
            navHeaderArtist.setText(song.getArtist());
        if (navHeaderAppName != null)
            navHeaderAppName.setVisibility(View.GONE);
        if (navHeaderIcon != null)
            navHeaderIcon.setVisibility(View.GONE);
        Uri contentURI = Uri.parse("content://media/external/audio/media/" + song.getSongId() + "/albumart");
        if (imageHeaderBg != null) {
            Picasso.get()
                    .load(contentURI)

                    .placeholder(R.drawable.album_art)
                    .into(imageHeaderBg);
        }


    }


    public void showInterstitial() {
        if (mInterstitial != null) {
            if (mInterstitial.isReady()) {
                mInterstitial.show();
            } else {
                isInterstitialCalled = true;
                mInterstitial.load();
            }
        }

    }

 public void showInterstitialGif() {
        if (mInterstitialGif != null) {
            if (mInterstitialGif.isReady()) {
                mInterstitialGif.show();
            } else {
                isInterstitialCalledGif = true;
                mInterstitialGif.load();
            }
        }
    }

    @Override
    public void handlePlaylistDialog(Song song) {
        mainActDisposable = Observable.fromCallable(() -> SongUtils.scanPlaylist(activity)).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(playlists -> Dialogs.addPlaylistDialog(activity, song, playlists));
    }

}
