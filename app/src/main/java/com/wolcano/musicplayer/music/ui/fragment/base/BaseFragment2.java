package com.wolcano.musicplayer.music.ui.fragment.base;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import com.hwangjr.rxbus.RxBus;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.wolcano.musicplayer.music.widgets.StatusBarView;
import com.wolcano.musicplayer.music.utils.PermissionUtils;
import com.wolcano.musicplayer.music.content.Binder;
import com.wolcano.musicplayer.music.ui.activity.MainActivity;
import dagger.android.support.AndroidSupportInjection;


public abstract class BaseFragment2<T extends ViewDataBinding> extends Fragment {

    protected Handler handler;
    private T mViewDataBinding;
    private View mRootView;

    public abstract int getBindingVariable();

    public abstract
    @LayoutRes
    int getLayoutId();


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        handler = new Handler(Looper.getMainLooper());
        Binder.bindIt(this, getView());
        RxBus.get().register(this);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewDataBinding.setLifecycleOwner(this);
        mViewDataBinding.executePendingBindings();
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewDataBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        mRootView = mViewDataBinding.getRoot();
        return mRootView;
    }
    public T getViewDataBinding() {
        return mViewDataBinding;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    private void performDependencyInjection() {
        AndroidSupportInjection.inject(this);
    }

    public void setStatusbarColorAuto(StatusBarView statusBarView, int color) {
        setStatusbarColor(color, statusBarView);
    }
}
