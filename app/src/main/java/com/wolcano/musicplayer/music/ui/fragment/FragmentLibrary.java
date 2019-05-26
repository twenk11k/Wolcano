package com.wolcano.musicplayer.music.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.common.ATHToolbarActivity;
import com.kabouzeid.appthemehelper.util.TintHelper;
import com.kabouzeid.appthemehelper.util.ToolbarContentTintHelper;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.DisposableManager;
import com.wolcano.musicplayer.music.ui.fragment.base.BaseFragment;
import com.wolcano.musicplayer.music.utils.PermissionUtils;
import com.wolcano.musicplayer.music.widgets.StatusBarView;
import com.wolcano.musicplayer.music.ui.adapter.LibraryFragmentPagerAdapter;
import com.wolcano.musicplayer.music.ui.fragment.library.FragmentAlbums;
import com.wolcano.musicplayer.music.ui.fragment.library.FragmentArtists;
import com.wolcano.musicplayer.music.ui.fragment.library.FragmentGenres;
import com.wolcano.musicplayer.music.ui.fragment.library.FragmentSongs;
import com.wolcano.musicplayer.music.utils.Utils;

public class FragmentLibrary extends BaseFragment {

    private StatusBarView statusBarView;
    private Toolbar toolbar;
    private boolean isHidden = false;
    public ViewPager viewPager;
    private Context context;
    private AppBarLayout appBarLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mylibrary, container, false);
        setHasOptionsMenu(true);
        context = getContext();
        appBarLayout = view.findViewById(R.id.appbar);



        toolbar = view.findViewById(R.id.toolbar);
        statusBarView = view.findViewById(R.id.statusBarCustom);
        int color = Utils.getPrimaryColor(getContext());
        setStatusbarColorAuto(statusBarView, color);

        if (Build.VERSION.SDK_INT < 21 && view.findViewById(R.id.statusBarCustom) != null) {
            view.findViewById(R.id.statusBarCustom).setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= 19) {
                int statusBarHeight = Utils.getStatHeight(getContext());
                AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
                params.setMargins(0, statusBarHeight, 0, 0);
                params.setScrollFlags(0);
                toolbar.setLayoutParams(params);

            }
        }

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        toolbar.setBackgroundColor(color);
        if (Utils.isColorLight(color)) {
            toolbar.setTitleTextColor(Color.BLACK);
        } else {
            toolbar.setTitleTextColor(Color.WHITE);
        }


        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setTitle(R.string.library);
        if (toolbar.getNavigationIcon() != null) {
            toolbar.setNavigationIcon(TintHelper.createTintedDrawable(toolbar.getNavigationIcon(), ToolbarContentTintHelper.toolbarContentColor(getContext(), color)));
        }
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        LibraryFragmentPagerAdapter adapter = new LibraryFragmentPagerAdapter(getContext(), getChildFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setSaveFromParentEnabled(false);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    Fragment fragment = (Fragment) adapter.instantiateItem(viewPager, 0);

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

        viewPager.removeAllViews();
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setBackgroundColor(color);
        int normalColor = ToolbarContentTintHelper.toolbarSubtitleColor(getActivity(), color);
        int selectedColor = ToolbarContentTintHelper.toolbarTitleColor(getActivity(), color);
        tabLayout.setTabTextColors(normalColor, selectedColor);
        tabLayout.setSelectedTabIndicatorColor(ThemeStore.accentColor(getActivity()));


        return view;
    }

    public void addOnAppBarOffsetChangedListener(AppBarLayout.OnOffsetChangedListener onOffsetChangedListener) {
        appBarLayout.addOnOffsetChangedListener(onOffsetChangedListener);
    }

    public void removeOnAppBarOffsetChangedListener(AppBarLayout.OnOffsetChangedListener onOffsetChangedListener) {
        appBarLayout.removeOnOffsetChangedListener(onOffsetChangedListener);
    }

    public int getTotalAppBarScrollingRange() {
        return appBarLayout.getTotalScrollRange();
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
        viewPager.removeAllViews();
        viewPager.destroyDrawingCache();
        viewPager = null;
        statusBarView = null;
        toolbar = null;
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
        ToolbarContentTintHelper.handleOnCreateOptionsMenu(getActivity(), toolbar, menu, ATHToolbarActivity.getToolbarBackgroundColor(toolbar));
    }

}
