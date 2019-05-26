package com.wolcano.musicplayer.music.ui.fragment.library;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.google.android.material.appbar.AppBarLayout;
import com.kabouzeid.appthemehelper.common.ATHToolbarActivity;
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.DisposableManager;
import com.wolcano.musicplayer.music.mvp.interactor.AlbumInteractorImpl;
import com.wolcano.musicplayer.music.mvp.models.Album;
import com.wolcano.musicplayer.music.mvp.presenter.AlbumPresenterImpl;
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.AlbumPresenter;
import com.wolcano.musicplayer.music.mvp.view.AlbumView;
import com.wolcano.musicplayer.music.ui.dialog.SleepTimerDialog;
import com.wolcano.musicplayer.music.ui.fragment.FragmentLibrary;
import com.wolcano.musicplayer.music.ui.activity.MainActivity;
import com.wolcano.musicplayer.music.ui.adapter.AlbumAdapter;
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragment;
import com.wolcano.musicplayer.music.utils.ToastUtils;
import com.wolcano.musicplayer.music.utils.Utils;
import java.util.List;
import io.reactivex.disposables.Disposable;
import pl.droidsonroids.gif.GifImageView;

public class FragmentAlbums extends BaseFragment implements AlbumView,AppBarLayout.OnOffsetChangedListener {

    private com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView recyclerView;
    private AlbumAdapter adapter;
    private Activity activity;
    private TextView empty;
    private Disposable disposable;
    private View v;
    private AlbumPresenter albumPresenter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menu_sleeptimer){
               new SleepTimerDialog().show(getFragmentManager(), "SET_SLEEP_TIMER");
        }
        return super.onOptionsItemSelected(item);
    }

    public void handleOptionsMenu() {
        Toolbar toolbar = ((MainActivity) getActivity()).getToolbar();
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sleeptimer, menu);
        Toolbar toolbar =  getActivity().findViewById(R.id.toolbar);
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(getActivity(), toolbar, menu, ATHToolbarActivity.getToolbarBackgroundColor(toolbar));

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         v = inflater.inflate(R.layout.fragment_inner_album, container, false);
        setHasOptionsMenu(true);
       setupView(v);
        return v;
    }

    private void setupView(View v) {
        recyclerView = v.findViewById(R.id.recycler);
        empty = v.findViewById(android.R.id.empty);

        Utils.setUpFastScrollRecyclerViewColor(recyclerView, Utils.getAccentColor(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        String sort = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
        albumPresenter = new AlbumPresenterImpl(this,activity,disposable,sort,new AlbumInteractorImpl());
        albumPresenter.getAlbums();

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
            empty.setText(R.string.no_album);
            empty.setVisibility(adapter == null || adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLibraryFragment().addOnAppBarOffsetChangedListener(this);

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
    private FragmentLibrary getLibraryFragment() {
        return (FragmentLibrary) getParentFragment();
    }
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), getLibraryFragment().getTotalAppBarScrollingRange() + verticalOffset);
    }

    @Override
    public void setAlbumList(List<Album> albumList) {
        if (albumList.size() <= 30) {
            recyclerView.setThumbEnabled(false);
        } else {
            recyclerView.setThumbEnabled(true);
        }
        adapter = new AlbumAdapter((MainActivity) getActivity(), albumList);
        controlIfEmpty();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                controlIfEmpty();
            }
        });

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        runLayoutAnimation(recyclerView);
    }
}
