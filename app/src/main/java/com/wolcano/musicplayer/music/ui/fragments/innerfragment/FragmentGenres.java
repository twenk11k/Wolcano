package com.wolcano.musicplayer.music.ui.fragments.innerfragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.kabouzeid.appthemehelper.common.ATHToolbarActivity;
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper;
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
import com.wolcano.musicplayer.music.mvp.models.Genre;
import com.wolcano.musicplayer.music.ui.fragments.FragmentLibrary;
import com.wolcano.musicplayer.music.utils.Perms;
import com.wolcano.musicplayer.music.ui.activities.MainActivity;
import com.wolcano.musicplayer.music.ui.adapter.GenreAdapter;
import com.wolcano.musicplayer.music.ui.fragments.BaseFragment;
import com.wolcano.musicplayer.music.utils.SongUtils;
import com.wolcano.musicplayer.music.utils.ToastMsgUtils;
import com.wolcano.musicplayer.music.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import pl.droidsonroids.gif.GifImageView;

import static com.wolcano.musicplayer.music.Constants.SONG_LIBRARY;

public class FragmentGenres extends BaseFragment implements AdapterClickListener, AppBarLayout.OnOffsetChangedListener {

    private com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView recyclerView;
    private GenreAdapter adapter;
    private MoPubRecyclerAdapter myMoPubAdapter;
    private Activity activity;
    private TextView empty;
    private Disposable genreSubscription;
    private Handler handlerInit;
    private Runnable runnableInit;
    private View v;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         v = inflater.inflate(R.layout.fragment_inner_album, container, false);
        setHasOptionsMenu(true);
        setupView(v);
        return v;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        int color = Utils.getPrimaryColor(getContext());
        if (Utils.isColorLight(color)) {
            inflater.inflate(R.menu.menu_gif_black, menu);
        } else {
            inflater.inflate(R.menu.menu_gif, menu);
        }
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(getActivity(), toolbar, menu, ATHToolbarActivity.getToolbarBackgroundColor(toolbar));

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void handleOptionsMenu() {
        Toolbar toolbar = ((MainActivity) getActivity()).getToolbar();
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
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

    private void setupView(View v) {
        recyclerView = v.findViewById(R.id.recycler);
        empty = v.findViewById(android.R.id.empty);
        Utils.setUpFastScrollRecyclerViewColor(recyclerView, Utils.getAccentColor(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        String sort = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
        setRecyclerView(sort);
    }
    public void callRecyclerView(){
        String sort = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
        setRecyclerView(sort);
    }
    @Subscribe(tags = {@Tag(SONG_LIBRARY)})
    public void setRecyclerView(String sort) {
        Perms.with(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .result(new Perms.PermInterface() {
                    @Override
                    public void onPermGranted() {
                        Observable<List<Genre>> booksObservable =
                                Observable.fromCallable(() -> SongUtils.scanGenre(getContext())).throttleFirst(500, TimeUnit.MILLISECONDS);
                        genreSubscription = booksObservable.
                                subscribeOn(Schedulers.io()).
                                observeOn(AndroidSchedulers.mainThread()).
                                subscribe(genres -> displayGenres(genres));
                    }

                    @Override
                    public void onPermUnapproved() {
                        controlIfEmpty();
                        ToastMsgUtils.show(activity.getApplicationContext(), R.string.no_perm_storage);
                    }
                })
                .reqPerm();
    }
    private void setMopubAdapter(List<Genre> genreList){
        if (genreList.size() <= 30) {
            recyclerView.setThumbEnabled(false);
        } else {
            recyclerView.setThumbEnabled(true);
        }
        adapter = new GenreAdapter((MainActivity) getActivity(), genreList, FragmentGenres.this);
        controlIfEmpty();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                controlIfEmpty();
            }
        });
        myMoPubAdapter = new MoPubRecyclerAdapter(activity, adapter);
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
        adapter.notifyDataSetChanged();
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
    private void displayGenres(List<Genre> genreList) {

        if(Utils.getIsMopubInitDone(activity.getApplicationContext())){
            setMopubAdapter(genreList);

        } else {
            handlerInit = new Handler();
            runnableInit = new Runnable() {
                @Override
                public void run() {
                    if (Utils.getIsMopubInitDone(activity.getApplicationContext())) {
                        setMopubAdapter(genreList);
                    } else {
                        handlerInit.postDelayed(this::run, 500);
                    }


                }
            };
            handlerInit.postDelayed(runnableInit, 500);
        }

    }

    private void controlIfEmpty() {
        if (empty != null) {
            empty.setText(R.string.no_genre);
            empty.setVisibility(adapter == null || adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (myMoPubAdapter != null) {
            myMoPubAdapter.destroy();
        }
        if (genreSubscription != null && !genreSubscription.isDisposed()) {
            genreSubscription.dispose();
        }
        recyclerView = null;
        adapter = null;
        myMoPubAdapter = null;
        genreSubscription = null;
        empty = null;
        if(handlerInit!=null && runnableInit!=null){
            handlerInit.removeCallbacks(runnableInit);
        }
        DisposableManager.dispose();
        getLibraryFragment().removeOnAppBarOffsetChangedListener(this);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLibraryFragment().addOnAppBarOffsetChangedListener(this);

    }

    @Override
    public int getOriginalPosition(int oldposition) {
        if(myMoPubAdapter!=null){
            return myMoPubAdapter.getOriginalPosition(oldposition);
        } else {
            return oldposition;
        }
    }
    private FragmentLibrary getLibraryFragment() {
        return (FragmentLibrary) getParentFragment();
    }
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), getLibraryFragment().getTotalAppBarScrollingRange() + verticalOffset);

    }
}
