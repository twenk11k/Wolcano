package com.wolcano.musicplayer.music.ui.activity.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.hwangjr.rxbus.RxBus;
import com.kabouzeid.appthemehelper.ATH;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.wolcano.musicplayer.music.content.Binder;
import com.wolcano.musicplayer.music.provider.MusicService;
import com.wolcano.musicplayer.music.provider.RemotePlay;
import com.wolcano.musicplayer.music.utils.Utils;
import com.wolcano.musicplayer.music.widgets.StatusBarView;
import com.wolcano.musicplayer.music.utils.PermissionUtils;


public abstract class BaseActivity extends AppCompatActivity {

    protected MusicService musicService;
    private ServiceConnection serviceConnection;
    protected Handler baseHandler;
    private boolean receiverRegistered;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // status bar
        setStatusBar();
        // volume control
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        // handler
        baseHandler = new Handler(Looper.getMainLooper());
        // service connection
        connectService();
        // register RxBus if sdk api above 19
        if (Build.VERSION.SDK_INT > 19)
            RxBus.get().register(this);

    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setView();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setView();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        setView();
    }
    private void connectService() {
        Intent intent = new Intent();
        intent.setClass(this, MusicService.class);
        serviceConnection = new RemoteServiceConn();
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setView() {
        Binder.bindIt(this);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private class RemoteServiceConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((MusicService.ServiceInit) service).getMusicService();

            if(!Utils.getServiceDestroy(getApplicationContext())){
                onServiceConnection();
                handleListener();
            } else {
                Utils.setServiceDestroy(getApplicationContext(),false);

            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Utils.setServiceDestroy(getApplicationContext(),true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    protected void onServiceConnection() {
    }

    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onDestroy() {
        if (serviceConnection != null) {
            unbindService(serviceConnection);
        }

        if (receiverRegistered) {
            receiverRegistered = false;
        }
        if (Build.VERSION.SDK_INT > 19)
            RxBus.get().unregister(this);

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Utils.getServiceDestroy(this)){
            Intent intent = new Intent();
            intent.setClass(this, MusicService.class);
            if(serviceConnection!=null){
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            } else {
                serviceConnection = new RemoteServiceConn();
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            }
        }
    }

    protected void handleListener() {
    }

    public void setLightStatusbar(boolean enabled) {
        ATH.setLightStatusbar(this, enabled);
    }

    public void setLightStatusbarAuto(int bgColor) {
        setLightStatusbar(ColorUtil.isColorLight(bgColor));
    }

    public void setStatusbarColor(int color, StatusBarView statusBarView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (statusBarView != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    statusBarView.setBackgroundColor(ColorUtil.darkenColor(color));
                    this.setLightStatusbarAuto(color);
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
