
package com.wolcano.musicplayer.music.ui.fragment.library;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.material.appbar.AppBarLayout;
import com.kabouzeid.appthemehelper.common.ATHToolbarActivity;
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.DisposableManager;
import com.wolcano.musicplayer.music.mvp.interactor.SongInteractorImpl;
import com.wolcano.musicplayer.music.mvp.listener.FilterListener;
import com.wolcano.musicplayer.music.mvp.models.Playlist;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.mvp.presenter.SongPresenterImpl;
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.SongPresenter;
import com.wolcano.musicplayer.music.mvp.view.SongView;
import com.wolcano.musicplayer.music.provider.RemotePlay;
import com.wolcano.musicplayer.music.ui.fragment.FragmentLibrary;
import com.wolcano.musicplayer.music.ui.activity.MainActivity;
import com.wolcano.musicplayer.music.ui.dialog.Dialogs;
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragment;
import com.wolcano.musicplayer.music.ui.filter.SongFilter;
import com.wolcano.musicplayer.music.utils.SongUtils;
import com.wolcano.musicplayer.music.utils.Utils;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FragmentSongs extends BaseFragment implements SongView,FilterListener, AppBarLayout.OnOffsetChangedListener {


    private SongsAdapter mAdapter;
    private FastScrollRecyclerView recyclerView;
    private Context context;
    private Activity activity;
    private int searchC = 0;
    TextView empty;
    private Disposable disposable;
    private String text;
    private View v;
    private SongPresenter songPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

         v = inflater.inflate(R.layout.fragment_songs, container, false);

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
        songPresenter = new SongPresenterImpl(this,activity,disposable,sort,new SongInteractorImpl());
        songPresenter.getSongs();

        return v;
    }
    private FragmentLibrary getLibraryFragment() {
        return (FragmentLibrary) getParentFragment();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLibraryFragment().addOnAppBarOffsetChangedListener(this);

    }

    @Override
    public void setSongList(List<Song> songList) {

        mAdapter = new FragmentSongs.SongsAdapter((MainActivity) getActivity(), songList, FragmentSongs.this::setFastScrollIndexer);
        recyclerView.setAdapter(mAdapter);
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

    public void controlIfEmpty() {
        if (empty != null) {
            empty.setText(R.string.no_song);
            empty.setVisibility(mAdapter == null || mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_library_songs, menu);
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

        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
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
                    popup.getMenuInflater().inflate(R.menu.popup_menu_song, popup.getMenu());
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
                Song song = arrayList.get(getAdapterPosition());
                RemotePlay.get().playAdd(context, arrayList, song);
            }
        }


    }

}
