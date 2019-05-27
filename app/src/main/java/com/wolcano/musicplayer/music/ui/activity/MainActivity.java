package com.wolcano.musicplayer.music.ui.activity;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.kabouzeid.appthemehelper.ATH;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.DisposableManager;
import com.wolcano.musicplayer.music.mvp.listener.OnServiceListener;
import com.wolcano.musicplayer.music.mvp.listener.OnSwipeTouchListener;
import com.wolcano.musicplayer.music.mvp.listener.PlaylistListener;
import com.wolcano.musicplayer.music.mvp.models.ModelBitmap;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.provider.RemotePlay;
import com.wolcano.musicplayer.music.content.slidingpanel.SlidingPanel;
import com.wolcano.musicplayer.music.content.PlayerEnum;
import com.wolcano.musicplayer.music.ui.activity.base.BaseActivity;
import com.wolcano.musicplayer.music.ui.dialog.RatingDialogV2;
import com.wolcano.musicplayer.music.utils.ColorUtils;
import com.wolcano.musicplayer.music.utils.NavigationViewUtils;
import com.wolcano.musicplayer.music.widgets.SongCover;
import com.wolcano.musicplayer.music.utils.PermissionUtils;
import com.wolcano.musicplayer.music.widgets.ModelView;
import com.wolcano.musicplayer.music.ui.dialog.Dialogs;
import com.wolcano.musicplayer.music.ui.fragment.FragmentRecently;
import com.wolcano.musicplayer.music.ui.fragment.FragmentLibrary;
import com.wolcano.musicplayer.music.ui.fragment.FragmentOnline;
import com.wolcano.musicplayer.music.ui.fragment.FragmentPlaylist;
import com.wolcano.musicplayer.music.ui.helper.SongHelperMenu;
import com.wolcano.musicplayer.music.utils.SongUtils;
import com.wolcano.musicplayer.music.utils.ToastUtils;
import com.wolcano.musicplayer.music.utils.UriFilesUtils;
import com.wolcano.musicplayer.music.utils.Utils;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener,View.OnClickListener, SeekBar.OnSeekBarChangeListener, OnServiceListener, PlaylistListener {

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
    @BindView(R.id.navView)
    BottomNavigationView navigationView;
    @BindView(R.id.slidinguppanellayout)
    SlidingUpPanelLayout mSlidingUpPanelLayout;
    @BindView(R.id.slidinguppanel_top1)
    FrameLayout frameTopOne;


    private boolean isDragging;
    private SlidingPanel slidingPanel;
    private boolean isExpand = false;
    private Context context;
    private boolean lightStatusbar;
    private boolean mIsResumed = true;
    private Activity activity;
    private Disposable disposable;
    private Drawable placeholder;
    private Handler handlerD, handlerCollapse;
    private Runnable runnableD;
    private int currentFrag;
    private boolean typeReturn = false;
    private BottomSheetBehavior bottomSheetBehavior;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        activity = this;
        ButterKnife.bind(this);
        setDrawerOptions();
        initViews();
        displayDialog();
        handleRateDialog();
    }


    private void setDrawerOptions() {

        handlerCollapse = new Handler();
        bottomSheetBehavior = new BottomSheetBehavior();
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                slideDown(navigationView, slideOffset);
            }
        });
    }

    private void slideDown(BottomNavigationView child, float slideOffset) {

        float height = slideOffset * child.getHeight();

        ViewPropertyAnimator animator = child.animate();

        animator.translationY(height)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(0)
                .start();
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


    public Toolbar getToolbar() {
        return findViewById(R.id.toolbar);
    }

    private void displayDialog() {
        boolean first = Utils.getFirst(context);
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

    }


    private void prev() {
        RemotePlay.get().prev(context);
    }


    private void setLeftButtonMode() {
        PlayerEnum mode = PlayerEnum.valueOf(Utils.getPlaylistId(context));
        switch (mode) {
            case NORMAL:
                mode = PlayerEnum.SHUFFLE;
                break;
            case SHUFFLE:
                mode = PlayerEnum.REPEAT;
                break;
            case REPEAT:
                mode = PlayerEnum.NORMAL;
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
    protected void onServiceConnection() {
        slidingPanel = new SlidingPanel(frameTopOne, this);
        RemotePlay.get().onListener(slidingPanel);
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
        List<Song> intentList = buildQueueFromFileUri(songUri);
        for (int i = 0; i < intentList.size(); i++) {
            if (intentList.get(i).getPath().equals(songPath)) {
                RemotePlay.get().playAdd(context, intentList, intentList.get(i));
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

        setSlidingPanelLayout();
        int openingVal;
        openingVal = Utils.getOpeningVal(MainActivity.this);
        Utils.setFirstFrag(this.getApplicationContext(), true);
        setFragment(openingVal);
        navigationView.getMenu().getItem(openingVal).setChecked(true);
        navigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        initViews2();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == theSeekbar) {
            current.setText(Utils.getDuraStr(progress / 1000, this));
        }
    }

    private void initViews2() {

        NavigationViewUtils.setItemIconColors(navigationView, ColorUtils.getOppositeColor(Utils.getPrimaryColor(this)), Utils.getAccentColor(this));
        NavigationViewUtils.setItemTextColors(navigationView, ColorUtils.getOppositeColor(Utils.getPrimaryColor(this)), Utils.getAccentColor(this));

        navigationView.setBackgroundColor(Utils.getPrimaryColor(context));

        theSeekbar.getProgressDrawable().setColorFilter(Utils.getAccentColor(this), PorterDuff.Mode.SRC_IN);
        theSeekbar.getThumb().setColorFilter(Utils.getAccentColor(this), PorterDuff.Mode.SRC_IN);
        queue.setColorFilter(getResources().getColor(R.color.grey0));

        placeholder = getResources().getDrawable(R.drawable.album_default);
        placeholder.setColorFilter(Utils.getPrimaryColor(this), PorterDuff.Mode.MULTIPLY);

        modelCoverView.setOnTouchListener(new OnSwipeTouchListener(activity) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
            }
        });

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
    }

    private void updateUiSettings() {
        slidingPanel.setViews();
        mIsResumed = true;
        setFragment(currentFrag);
        initViews2();
        onChangeSong(RemotePlay.get().getPlayMusic(this));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsResumed = false;
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
                        if (handlerCollapse != null) {
                            handlerCollapse.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    collapsePanel();
                                }
                            }, 250);
                            break;
                        }

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

        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        if (slidingPanel.getDisposable() != null && !slidingPanel.getDisposable().isDisposed()) {
            slidingPanel.getDisposable().dispose();
        }

        if (handlerD != null) {
            handlerD.removeCallbacks(runnableD);
        }

        DisposableManager.dispose();

        super.onDestroy();
    }



    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (seekBar == theSeekbar) {
            isDragging = true;
        }
    }

    public boolean getIfMainVisible() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (fragment instanceof FragmentOnline) {
            if (((FragmentOnline) fragment).isSearchOpen()) {
                ((FragmentOnline) fragment).closeSearch();
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

            Fragment fragmentMain = new FragmentOnline();
            fragmentTransaction.replace(R.id.fragment, fragmentMain);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (position == 1) {
            if (mIsResumed)
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            Fragment fragmentRecently = new FragmentRecently();
            fragmentTransaction.replace(R.id.fragment, fragmentRecently);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (position == 2) {
            if (mIsResumed)
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            Fragment fragmentLibrary = new FragmentLibrary();
            fragmentTransaction.replace(R.id.fragment, fragmentLibrary);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else if (position == 3) {
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
                    if (RemotePlay.get().getPlayMusic(context).getType() == Song.Tip.MODEL0) {
                        SongHelperMenu.handleMenuLocal(this, v, RemotePlay.get().getPlayMusic(context), this::handlePlaylistDialog);
                    } else {
                        SongHelperMenu.handleMenuOnline(this, v, RemotePlay.get().getPlayMusic(context));
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
                if (!RemotePlay.get().getSongList().isEmpty()) {
                    Intent intent = new Intent(this, QueueActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.empty_queue), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.nav_header_relative:
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

        Observable<List<ModelBitmap>> bitmapObservable =
                Observable.fromCallable(() -> {
                    List<ModelBitmap> bitmapList = new ArrayList<>();
                    bitmapList.add(SongCover.get().loadBlurredModel(context, song));
                    bitmapList.add(SongCover.get().loadOvalModel(context, song));
                    return bitmapList;
                }).throttleFirst(500, TimeUnit.MILLISECONDS);
        disposable = bitmapObservable.
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

            setAlbumCover(song);
            return;
        }
        title.setText(song.getTitle());
        artist.setText(song.getArtist());
        theSeekbar.setProgress((int) RemotePlay.get().getPlayerCurrentPosition());
        theSeekbar.setSecondaryProgress(0);
        theSeekbar.setMax((int) song.getDuration());
        current.setText(R.string.start);
        total.setText(Utils.getDuraStr(song.getDuration() / 1000, this));
        setAlbumCover(song);
        if (RemotePlay.get().isPlaying() || RemotePlay.get().isPreparing()) {
            play.setSelected(true);
            modelCoverView.start();
        } else {
            play.setSelected(false);
            modelCoverView.pause();
        }

    }


    @Override
    public void handlePlaylistDialog(Song song) {
        disposable = Observable.fromCallable(() -> SongUtils.scanPlaylist(activity)).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(playlists -> Dialogs.addPlaylistDialog(activity, song, playlists));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Utils.hideKeyboard(MainActivity.this);
        int id = menuItem.getItemId();
        mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        Utils.setFirstFrag(MainActivity.this, false);
        typeReturn = false;
        switch (id) {
            case R.id.nav_onlineplayer:
                setFragment(0);
                typeReturn = true;
            break;
            case R.id.nav_recentlyadded:
                Utils.setCountDownload(MainActivity.this, 0);
                setFragment(1);
                typeReturn = true;
            break;
            case R.id.nav_library:
                setFragment(2);
                typeReturn = true;
            break;
            case R.id.nav_playlists:
                setFragment(3);
                typeReturn = true;
                break;
            case R.id.nav_settings:
                PermissionUtils.with(MainActivity.this)
                        .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .result(new PermissionUtils.PermInterface() {

                            @Override
                            public void onPermGranted() {
                                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onPermUnapproved() {
                                ToastUtils.show(getApplicationContext(), R.string.no_perm_open_settings);
                            }
                        })
                        .reqPerm();
                break;
        }
        return typeReturn;
    }

}
