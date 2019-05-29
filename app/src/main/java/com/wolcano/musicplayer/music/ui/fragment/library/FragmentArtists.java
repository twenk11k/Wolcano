package com.wolcano.musicplayer.music.ui.fragment.library;

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
import com.wolcano.musicplayer.music.di.component.ApplicationComponent;
import com.wolcano.musicplayer.music.di.component.ArtistComponent;
import com.wolcano.musicplayer.music.di.component.DaggerArtistComponent;
import com.wolcano.musicplayer.music.di.module.ArtistModule;
import com.wolcano.musicplayer.music.mvp.DisposableManager;
import com.wolcano.musicplayer.music.mvp.interactor.ArtistInteractorImpl;
import com.wolcano.musicplayer.music.mvp.models.Artist;
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.ArtistPresenter;
import com.wolcano.musicplayer.music.mvp.view.ArtistView;
import com.wolcano.musicplayer.music.ui.dialog.SleepTimerDialog;
import com.wolcano.musicplayer.music.ui.fragment.FragmentLibrary;
import com.wolcano.musicplayer.music.ui.activity.MainActivity;
import com.wolcano.musicplayer.music.ui.adapter.ArtistAdapter;
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragment;
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragmentInject;
import com.wolcano.musicplayer.music.utils.Utils;
import java.util.List;
import javax.inject.Inject;


public class FragmentArtists extends BaseFragmentInject implements ArtistView,AppBarLayout.OnOffsetChangedListener {

    private ArtistAdapter adapter;
    private FragmentInnerAlbumBinding binding;
    private View view;

    @Inject
    ArtistPresenter artistPresenter;




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLibraryFragment().addOnAppBarOffsetChangedListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_inner_album, container, false);
        view = binding.getRoot();
        setHasOptionsMenu(true);
        setupViews();
        return view;
    }

    private void setupViews() {
        Utils.setUpFastScrollRecyclerViewColor(binding.recyclerview, Utils.getAccentColor(getContext()));
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerview.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
       artistPresenter.getArtists();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sleeptimer, menu);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menu_sleeptimer){
            new SleepTimerDialog().show(getFragmentManager(), "SET_SLEEP_TIMER");
        }
        return super.onOptionsItemSelected(item);
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
            binding.empty.setText(R.string.no_artist);
            binding.empty.setVisibility(adapter == null || adapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getLibraryFragment().removeOnAppBarOffsetChangedListener(this);
        DisposableManager.dispose();

    }

    private FragmentLibrary getLibraryFragment() {
        return (FragmentLibrary) getParentFragment();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), getLibraryFragment().getTotalAppBarScrollingRange() + verticalOffset);

    }

    @Override
    public void setArtistList(List<Artist> artistList) {
        if (artistList.size() <= 30) {
            binding.recyclerview.setThumbEnabled(false);
        } else {
            binding.recyclerview.setThumbEnabled(true);
        }
        adapter = new ArtistAdapter((MainActivity) getActivity(), artistList);
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

    @Override
    public void setupComponent(ApplicationComponent applicationComponent) {
        String sort = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;

        ArtistComponent artistComponent = DaggerArtistComponent.builder()
                .applicationComponent(applicationComponent)
                .artistModule(new ArtistModule(this,this,getActivity(),sort,new ArtistInteractorImpl()))
                .build();

        artistComponent.inject(this);
    }

}
