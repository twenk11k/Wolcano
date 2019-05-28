package com.wolcano.musicplayer.music.ui.fragment;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.RelativeLayout;
import com.kabouzeid.appthemehelper.common.ATHToolbarActivity;
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.databinding.FragmentBaseSongBinding;
import com.wolcano.musicplayer.music.mvp.DisposableManager;
import com.wolcano.musicplayer.music.mvp.interactor.PlaylistInteractorImpl;
import com.wolcano.musicplayer.music.mvp.models.Playlist;
import com.wolcano.musicplayer.music.mvp.presenter.PlaylistPresenterImpl;
import com.wolcano.musicplayer.music.mvp.presenter.interfaces.PlaylistPresenter;
import com.wolcano.musicplayer.music.mvp.view.PlaylistView;
import com.wolcano.musicplayer.music.ui.dialog.SleepTimerDialog;
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragment;
import com.wolcano.musicplayer.music.ui.adapter.PlaylistAdapter;
import com.wolcano.musicplayer.music.utils.Utils;
import java.util.List;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

public class FragmentPlaylist extends BaseFragment implements PlaylistView {

    private PlaylistAdapter mAdapter;
    private int color;
    private Activity activity;
    private Disposable disposable;
    private PlaylistPresenter playlistPresenter;
    private FragmentBaseSongBinding binding;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        color = Utils.getPrimaryColor(getContext());
        setStatusbarColorAuto(binding.statusBarCustom, color);

        if (Build.VERSION.SDK_INT < 21 && view.findViewById(R.id.statusBarCustom) != null) {
            view.findViewById(R.id.statusBarCustom).setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= 19) {
                int statusBarHeight = Utils.getStatHeight(getContext());
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.findViewById(R.id.toolbar).getLayoutParams();
                layoutParams.setMargins(0, statusBarHeight, 0, 0);
                view.findViewById(R.id.toolbar).setLayoutParams(layoutParams);
            }
        }

        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);

        binding.toolbar.setBackgroundColor(color);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setTitle(R.string.playlists);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_sleeptimer, menu);
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(getActivity(), binding.toolbar, menu, ATHToolbarActivity.getToolbarBackgroundColor(binding.toolbar));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menu_sleeptimer){
            new SleepTimerDialog().show(getFragmentManager(), "SET_SLEEP_TIMER");
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_base_song, container, false);
        setHasOptionsMenu(true);
        Utils.setUpFastScrollRecyclerViewColor(binding.recyclerview, Utils.getAccentColor(getContext()));
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerview.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        String sort = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
        playlistPresenter = new PlaylistPresenterImpl(this,activity,disposable,sort,new PlaylistInteractorImpl());
        playlistPresenter.getPlaylists();
        return binding.getRoot();
    }


    public void controlIfEmpty() {
        if (binding.empty != null) {
            binding.empty.setText(R.string.no_playlist);
            binding.empty.setVisibility(mAdapter == null || mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }

        DisposableManager.dispose();

    }


    @Override
    public void setPlaylistList(List<Playlist> playlistList) {
        if (playlistList.size() <= 30) {
            binding.recyclerview.setThumbEnabled(false);
        } else {
            binding.recyclerview.setThumbEnabled(true);
        }
        mAdapter = new PlaylistAdapter(getActivity(), playlistList);
        binding.recyclerview.setAdapter(mAdapter);
        controlIfEmpty();
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                controlIfEmpty();
            }
        });
    }
}
