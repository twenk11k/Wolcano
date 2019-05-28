package com.wolcano.musicplayer.music.ui.fragment;

import android.Manifest;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.appbar.AppBarLayout;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.common.ATHToolbarActivity;
import com.kabouzeid.appthemehelper.util.TintHelper;
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.databinding.FragmentLibraryBinding;
import com.wolcano.musicplayer.music.di.component.ApplicationComponent;
import com.wolcano.musicplayer.music.mvp.DisposableManager;
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragment;
import com.wolcano.musicplayer.music.utils.PermissionUtils;
import com.wolcano.musicplayer.music.ui.adapter.statepager.LibraryFragmentPagerAdapter;
import com.wolcano.musicplayer.music.ui.fragment.library.FragmentAlbums;
import com.wolcano.musicplayer.music.ui.fragment.library.FragmentArtists;
import com.wolcano.musicplayer.music.ui.fragment.library.FragmentGenres;
import com.wolcano.musicplayer.music.ui.fragment.library.FragmentSongs;
import com.wolcano.musicplayer.music.utils.Utils;


public class FragmentLibrary extends BaseFragment {

    private boolean isHidden = false;
    private FragmentLibraryBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_library, container, false);

        setHasOptionsMenu(true);
        int color = Utils.getPrimaryColor(getContext());
        setStatusbarColorAuto(binding.statusBarCustom, color);

        if (Build.VERSION.SDK_INT < 21 && binding.statusBarCustom != null) {
            binding.statusBarCustom.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= 19) {
                int statusBarHeight = Utils.getStatHeight(getContext());
                AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) binding.toolbar.getLayoutParams();
                params.setMargins(0, statusBarHeight, 0, 0);
                params.setScrollFlags(0);
                binding.toolbar.setLayoutParams(params);

            }
        }

        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);

        binding.toolbar.setBackgroundColor(color);
        if (Utils.isColorLight(color)) {
            binding.toolbar.setTitleTextColor(Color.BLACK);
        } else {
            binding.toolbar.setTitleTextColor(Color.WHITE);
        }


        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setTitle(R.string.library);
        if (binding.toolbar.getNavigationIcon() != null) {
            binding.toolbar.setNavigationIcon(TintHelper.createTintedDrawable(binding.toolbar.getNavigationIcon(), ToolbarContentTintHelper.toolbarContentColor(getContext(), color)));
        }

        LibraryFragmentPagerAdapter adapter = new LibraryFragmentPagerAdapter(getContext(), getChildFragmentManager());
        binding.viewpager.setAdapter(adapter);
        binding.viewpager.setSaveFromParentEnabled(false);
        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    Fragment fragment = (Fragment) adapter.instantiateItem(binding.viewpager, 0);

                    if (fragment != null && fragment instanceof FragmentSongs) {
                        if (isHidden)
                            ((FragmentSongs) fragment).handleOptionsMenu();


                    } else if (fragment != null && fragment instanceof FragmentArtists) {
                        if (isHidden)
                            ((FragmentArtists) fragment).handleOptionsMenu();

                    } else if (fragment != null && fragment instanceof FragmentAlbums) {
                        if (isHidden)
                            ((FragmentAlbums) fragment).handleOptionsMenu();

                    } else if (fragment != null && fragment instanceof FragmentGenres) {
                        if (isHidden)
                            ((FragmentGenres) fragment).handleOptionsMenu();

                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        binding.viewpager.removeAllViews();
        binding.tabLayout.setupWithViewPager(binding.viewpager);
        binding.tabLayout.setBackgroundColor(color);
        int normalColor = ToolbarContentTintHelper.toolbarSubtitleColor(getActivity(), color);
        int selectedColor = ToolbarContentTintHelper.toolbarTitleColor(getActivity(), color);
        binding.tabLayout.setTabTextColors(normalColor, selectedColor);
        binding.tabLayout.setSelectedTabIndicatorColor(ThemeStore.accentColor(getActivity()));


        return binding.getRoot();
    }

    public ViewPager getViewPager(){
        return binding.viewpager;
    }

    public void addOnAppBarOffsetChangedListener(AppBarLayout.OnOffsetChangedListener onOffsetChangedListener) {
        binding.appbar.addOnOffsetChangedListener(onOffsetChangedListener);
    }

    public void removeOnAppBarOffsetChangedListener(AppBarLayout.OnOffsetChangedListener onOffsetChangedListener) {
        binding.appbar.removeOnOffsetChangedListener(onOffsetChangedListener);
    }

    public int getTotalAppBarScrollingRange() {
        return binding.appbar.getTotalScrollRange();
    }

    private void checkPerm(LibraryFragmentPagerAdapter adapter) {
        PermissionUtils.with(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .result(new PermissionUtils.PermInterface() {
                    @Override
                    public void onPermGranted() {
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onPermUnapproved() {
                        adapter.notifyDataSetChanged();
                    }
                })
                .reqPerm();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.viewpager.removeAllViews();
        binding.viewpager.destroyDrawingCache();
        DisposableManager.dispose();

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            isHidden = true;
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(getActivity(), binding.toolbar, menu, ATHToolbarActivity.getToolbarBackgroundColor(binding.toolbar));
    }

}
