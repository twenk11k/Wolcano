package com.wolcano.musicplayer.music.ui.fragment.library;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
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
import com.google.android.material.appbar.AppBarLayout;
import com.kabouzeid.appthemehelper.common.ATHToolbarActivity;
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.databinding.FragmentInnerAlbumBinding;
import com.wolcano.musicplayer.music.mvp.DisposableManager;
import com.wolcano.musicplayer.music.mvp.interactor.GenreInteractorImpl;
import com.wolcano.musicplayer.music.mvp.models.Genre;
import com.wolcano.musicplayer.music.mvp.presenter.GenrePresenterImpl;
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.GenrePresenter;
import com.wolcano.musicplayer.music.mvp.view.GenreView;
import com.wolcano.musicplayer.music.ui.dialog.SleepTimerDialog;
import com.wolcano.musicplayer.music.ui.fragment.FragmentLibrary;
import com.wolcano.musicplayer.music.ui.activity.MainActivity;
import com.wolcano.musicplayer.music.ui.adapter.GenreAdapter;
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragment;
import com.wolcano.musicplayer.music.utils.Utils;
import java.util.List;
import io.reactivex.disposables.Disposable;

public class FragmentGenres extends BaseFragment implements GenreView,AppBarLayout.OnOffsetChangedListener {

    private GenreAdapter adapter;
    private Activity activity;
    private Disposable disposable;
    private GenrePresenter genrePresenter;
    private FragmentInnerAlbumBinding binding;
    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_inner_album, container, false);
        view = binding.getRoot();
        setHasOptionsMenu(true);
        setupViews();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sleeptimer, menu);
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(getActivity(), toolbar, menu, ATHToolbarActivity.getToolbarBackgroundColor(toolbar));

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void handleOptionsMenu() {
        Toolbar toolbar = ((MainActivity) getActivity()).getToolbar();
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menu_sleeptimer){
            new SleepTimerDialog().show(getFragmentManager(), "SET_SLEEP_TIMER");
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViews() {
        Utils.setUpFastScrollRecyclerViewColor(binding.recyclerview, Utils.getAccentColor(getContext()));
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerview.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        String sort = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
        genrePresenter = new GenrePresenterImpl(this,activity,sort,new GenreInteractorImpl());
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
        if (binding.empty != null) {
            binding.empty.setText(R.string.no_genre);
            binding.empty.setVisibility(adapter == null || adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
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
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), getLibraryFragment().getTotalAppBarScrollingRange() + verticalOffset);

    }

    @Override
    public void setGenreList(List<Genre> genreList) {
        if (genreList.size() <= 30) {
            binding.recyclerview.setThumbEnabled(false);
        } else {
            binding.recyclerview.setThumbEnabled(true);
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

        binding.recyclerview.setAdapter(adapter);
        runLayoutAnimation(binding.recyclerview);
    }
}
