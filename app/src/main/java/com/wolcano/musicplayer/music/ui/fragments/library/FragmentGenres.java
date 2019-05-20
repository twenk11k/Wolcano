package com.wolcano.musicplayer.music.ui.fragments.library;

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
import com.wolcano.musicplayer.music.mvp.interactor.GenreInteractorImpl;
import com.wolcano.musicplayer.music.mvp.models.Genre;
import com.wolcano.musicplayer.music.mvp.presenter.GenrePresenterImpl;
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.GenrePresenter;
import com.wolcano.musicplayer.music.mvp.view.GenreView;
import com.wolcano.musicplayer.music.ui.fragments.FragmentLibrary;
import com.wolcano.musicplayer.music.ui.activities.MainActivity;
import com.wolcano.musicplayer.music.ui.adapter.GenreAdapter;
import com.wolcano.musicplayer.music.ui.fragments.base.BaseFragment;
import com.wolcano.musicplayer.music.utils.ToastUtils;
import com.wolcano.musicplayer.music.utils.Utils;
import java.util.List;
import io.reactivex.disposables.Disposable;
import pl.droidsonroids.gif.GifImageView;

public class FragmentGenres extends BaseFragment implements GenreView,AppBarLayout.OnOffsetChangedListener {

    private com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView recyclerView;
    private GenreAdapter adapter;
    private Activity activity;
    private TextView empty;
    private Disposable disposable;
    private View v;
    private GenrePresenter genrePresenter;

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
                ToastUtils.show(getContext(),"Gif icon is clicked!");
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
        genrePresenter = new GenrePresenterImpl(this,activity,disposable,sort,new GenreInteractorImpl());
        genrePresenter.getGenres();
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
            empty.setText(R.string.no_genre);
            empty.setVisibility(adapter == null || adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLibraryFragment().addOnAppBarOffsetChangedListener(this);

    }

    private FragmentLibrary getLibraryFragment() {
        return (FragmentLibrary) getParentFragment();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), getLibraryFragment().getTotalAppBarScrollingRange() + verticalOffset);

    }

    @Override
    public void setGenreList(List<Genre> genreList) {
        if (genreList.size() <= 30) {
            recyclerView.setThumbEnabled(false);
        } else {
            recyclerView.setThumbEnabled(true);
        }
        adapter = new GenreAdapter((MainActivity) getActivity(), genreList);
        controlIfEmpty();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                controlIfEmpty();
            }
        });

        recyclerView.setAdapter(adapter);
        runLayoutAnimation(recyclerView);
    }
}
