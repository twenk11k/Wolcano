
package com.wolcano.musicplayer.music.ui.fragments.innerfragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.DisposableManager;
import com.wolcano.musicplayer.music.mvp.listener.FilterListener;
import com.wolcano.musicplayer.music.mvp.models.Playlist;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.provider.RemotePlay;
import com.wolcano.musicplayer.music.ui.fragments.FragmentLibrary;
import com.wolcano.musicplayer.music.utils.Perms;
import com.wolcano.musicplayer.music.ui.activities.MainActivity;
import com.wolcano.musicplayer.music.ui.dialog.Dialogs;
import com.wolcano.musicplayer.music.ui.fragments.BaseFragment;
import com.wolcano.musicplayer.music.ui.filter.SongFilter;
import com.wolcano.musicplayer.music.utils.SongUtils;
import com.wolcano.musicplayer.music.utils.ToastMsgUtils;
import com.wolcano.musicplayer.music.utils.Utils;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import static com.wolcano.musicplayer.music.Constants.SONG_LIBRARY;

public class FragmentSongs extends BaseFragment implements FilterListener,AppBarLayout.OnOffsetChangedListener {

    private SongsAdapter mAdapter;
    private FastScrollRecyclerView recyclerView;
    private Context context;
    private MoPubRecyclerAdapter myMoPubAdapter;
    private Activity activity;
    private int searchC = 0;
    TextView empty;
    private Disposable disposable;
    private Handler handlerInit;
    private Runnable runnableInit;
    private String text;
    private View v;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

         v = inflater.inflate(R.layout.songs_fragment, container, false);
       // container = v.findViewById(R.id.relative_container);

        context = getContext();
        activity = getActivity();

        setHasOptionsMenu(true);
        empty = v.findViewById(android.R.id.empty);
        recyclerView = v.findViewById(R.id.recyclerview);

        Utils.setUpFastScrollRecyclerViewColor(recyclerView, Utils.getAccentColor(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        String sort = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
        setRecyclerView(sort);
        return v;
    }
    private FragmentLibrary getLibraryFragment() {
        return (FragmentLibrary) getParentFragment();
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

                        disposable = booksObservable.
                                subscribeOn(Schedulers.io()).
                                observeOn(AndroidSchedulers.mainThread()).
                                subscribe(alist -> displayArrayList(alist));

                    }

                    @Override
                    public void onPermUnapproved() {
                        controlIfEmpty();
                        ToastMsgUtils.show(context.getApplicationContext(), R.string.no_perm_storage);
                    }
                })
                .reqPerm();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLibraryFragment().addOnAppBarOffsetChangedListener(this);

    }

    private void setMopubAdapter(List<Song> alist){
        mAdapter = new FragmentSongs.SongsAdapter((MainActivity) getActivity(), alist, FragmentSongs.this::setFastScrollIndexer);
        myMoPubAdapter = new MoPubRecyclerAdapter(activity, mAdapter);
        controlIfEmpty();
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                controlIfEmpty();
            }
        });
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
        MoPubStaticNativeAdRenderer mopubRenderer = new MoPubStaticNativeAdRenderer(viewBinder);
        myMoPubAdapter.registerAdRenderer(facebookAdRenderer);
        myMoPubAdapter.registerAdRenderer(flurryNativeAdRenderer);
        myMoPubAdapter.registerAdRenderer(mopubRenderer);
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
        if(Utils.getIsMopubInitDone(context)){
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

    public void controlIfEmpty() {
        if (empty != null) {
            empty.setText(R.string.no_song);
            empty.setVisibility(mAdapter == null || mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment, menu);
        MenuItem sItem = menu.findItem(R.id.search);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(getActivity(), toolbar, menu, ATHToolbarActivity.getToolbarBackgroundColor(toolbar));
        final SearchView searchView = (SearchView) sItem.getActionView();
        searchView.setQueryHint(getString(R.string.queryhintyerel));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String _text) {
                text = _text;
                if (mAdapter != null) {
                    if (mAdapter.getFilter() != null) {
                        mAdapter.getFilter().filter(_text);
                    }
                }
                return false;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                text = query;
                searchC = 1;
                searchView.clearFocus();
                return false;
            }

        });

        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    public void handleOptionsMenu() {
        Toolbar toolbar = ((MainActivity) getActivity()).getToolbar();
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (myMoPubAdapter != null) {
            myMoPubAdapter.destroy();
        }
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        if(handlerInit!=null && runnableInit!=null){
            handlerInit.removeCallbacks(runnableInit);
        }
        DisposableManager.dispose();
        getLibraryFragment().removeOnAppBarOffsetChangedListener(this);

    }

    @Override
    public void setFastScrollIndexer(boolean isShown) {
        if (isShown) {
            recyclerView.setThumbEnabled(true);
        } else {
            recyclerView.setThumbEnabled(false);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), getLibraryFragment().getTotalAppBarScrollingRange() + i);
    }


    public class SongsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
        public List<Song> arrayList;
        AppCompatActivity context;
        SongFilter filter;
        List<Song> fList;
        FilterListener fListener;

        private SongsAdapter(AppCompatActivity context, List<Song> arrayList, FilterListener fListener) {
            this.context = context;
            this.arrayList = arrayList;
            this.fList = arrayList;
            this.fListener = fListener;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder = null;
            View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
            viewHolder = new SongsAdapter.ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            SongsAdapter.ViewHolder viewHolder = (SongsAdapter.ViewHolder) holder;
            Song song = arrayList.get(position);
            String dura = "";
            try {
                dura = Utils.getDura(song.getDura() / 1000);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            viewHolder.line2.setText((dura.isEmpty() ? "" : dura + " | ") + arrayList.get(position).getArtist());
            viewHolder.line1.setText(song.getTitle());
            String contentURI = "content://media/external/audio/media/" + song.getSongId() + "/albumart";
            Picasso.get()
                    .load(contentURI)
                    .placeholder(R.drawable.album_art)
                    .into(viewHolder.albumArt);
            setOnPopupMenuListener(viewHolder, position);
        }

        private void setOnPopupMenuListener(SongsAdapter.ViewHolder holder, final int position) {
            holder.more.setOnClickListener(v -> {
                try {
                    ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(v.getContext(), R.style.PopupMenuToolbar);

                    PopupMenu popup = new PopupMenu(contextThemeWrapper, v);
                    popup.getMenuInflater().inflate(R.menu.menu_song, popup.getMenu());
                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {
                                case R.id.add_to_playlist:
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
                                                    Dialogs.addPlaylistDialog(activity, arrayList.get(position), playlists);
                                                }
                                            });
                                    break;
                                case R.id.copy_to_clipboard:
                                    Dialogs.copyDialog(context, arrayList.get(position));
                                    break;
                                case R.id.set_as_ringtone:
                                    Utils.setRingtone(context, arrayList.get(position).getSongId());
                                    break;
                                case R.id.delete:

                                    Song song = arrayList.get(position);
                                    CharSequence title, artist;
                                    int content;
                                    title = song.getTitle();
                                    artist = song.getArtist();
                                    content = R.string.delete_song_content;
                                    Uri contentURI = Uri.parse("content://media/external/audio/media/" + song.getSongId() + "/albumart");
                                    Picasso.get().load(contentURI).into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                            update(bitmap);
                                        }

                                        @Override
                                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                            update(null);
                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                                        }

                                        private void update(Bitmap bitmap) {
                                            if (bitmap == null) {
                                                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.album_art);
                                            }
                                            Drawable albumart = new BitmapDrawable(getResources(), bitmap);
                                            String wholeStr = title + "\n" + artist;
                                            SpannableString spanTitle = new SpannableString(wholeStr);
                                            spanTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.grey0)), (title + "\n").length(), wholeStr.length(), 0);

                                            new MaterialDialog.Builder(context)
                                                    .title(spanTitle)
                                                    .content(content)
                                                    .positiveText(R.string.yes)
                                                    .negativeText(R.string.no)
                                                    .positiveColor(Utils.getAccentColor(context))
                                                    .negativeColor(Utils.getAccentColor(context))
                                                    .onPositive((dialog, which) -> {
                                                        if (context == null)
                                                            return;
                                                        RemotePlay.get().deleteFromRemotePlay(context, arrayList.size(), position, song);
                                                        List<Song> alist = new ArrayList<>();
                                                        alist.add(song);
                                                        Utils.deleteTracks(context, alist);
                                                        arrayList.remove(position);
                                                        notifyItemRemoved(position);
                                                        notifyItemRangeChanged(position, getItemCount());
                                                    })
                                                    .icon(albumart)
                                                    .limitIconToDefaultSize()
                                                    .show();
                                        }
                                    });

                                    break;
                                case R.id.share:
                                    Dialogs.shareDialog(context, arrayList.get(position), false);
                                    break;
                                default:
                                    break;
                            }

                            return true;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        @Override
        public int getItemCount() {
            if (arrayList.size() <= 30) {
                fListener.setFastScrollIndexer(false);
            } else {
                fListener.setFastScrollIndexer(true);
            }
            return (null != arrayList ? arrayList.size() : 0);
        }

        @Override
        public Filter getFilter() {
            if (filter == null) {
                filter = new SongFilter(fList, this);
            }
            return filter;
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView line1;
            TextView line2;
            ImageView albumArt, more;
            public ViewHolder(View view) {
                super(view);
                this.line1 = view.findViewById(R.id.line1);
                this.line2 = view.findViewById(R.id.line2);
                this.albumArt = view.findViewById(R.id.albumArt);
                this.more = view.findViewById(R.id.more);
                view.setOnClickListener(this);
            }
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(context);
                Song song = arrayList.get(myMoPubAdapter.getOriginalPosition(getAdapterPosition()));
                RemotePlay.get().playAdd(context, arrayList, song);
            }
        }


    }

}
