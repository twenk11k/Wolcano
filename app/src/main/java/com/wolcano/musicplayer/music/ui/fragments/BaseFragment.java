package com.wolcano.musicplayer.music.ui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.hwangjr.rxbus.RxBus;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.wolcano.musicplayer.music.widgets.StatusBarView;
import com.wolcano.musicplayer.music.utils.Perms;
import com.wolcano.musicplayer.music.content.Binder;
import com.wolcano.musicplayer.music.ui.activities.MainActivity;


public abstract class BaseFragment extends Fragment {
    protected Handler handler;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler = new Handler(Looper.getMainLooper());
        Binder.bindIt(this, getView());
        RxBus.get().register(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Perms.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onDestroy() {
        RxBus.get().unregister(this);
        super.onDestroy();
    }

    public void setStatusbarColor(int color, StatusBarView statusBarView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (statusBarView != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    statusBarView.setBackgroundColor(ColorUtil.darkenColor(color));
                    ((MainActivity) getActivity()).setLightStatusbarAuto(color);
                } else {
                    statusBarView.setBackgroundColor(color);
                }
            }
        }
    }

    public void setStatusbarColorAuto(StatusBarView statusBarView, int color) {
        setStatusbarColor(color, statusBarView);
    }
}
