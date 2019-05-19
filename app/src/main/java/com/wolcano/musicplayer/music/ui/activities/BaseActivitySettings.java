package com.wolcano.musicplayer.music.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import com.kabouzeid.appthemehelper.ATH;
import com.kabouzeid.appthemehelper.ThemeStore;
import com.kabouzeid.appthemehelper.util.ColorUtil;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.widgets.StatusBarView;
import com.wolcano.musicplayer.music.utils.Utils;

public class BaseActivitySettings extends AppCompatActivity {

    public static final int PERMISSION_REQUEST = 100;

    private boolean hadPermissions;
    private String[] permissions;
    private String permissionDeniedMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        permissions = getPermissionsToRequest();
        hadPermissions = hasPermissions();
        setPermissionDeniedMessage(null);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (!hasPermissions()) {
            requestPermissions();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final boolean hasPermissions = hasPermissions();
        if (hasPermissions != hadPermissions) {
            hadPermissions = hasPermissions;
        }
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_UP) {
            showOverflowMenu();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    protected void showOverflowMenu() {

    }

    @Nullable
    protected String[] getPermissionsToRequest() {
        return null;
    }

    protected View getSnackBarContainer() {
        return getWindow().getDecorView();
    }

    protected void setPermissionDeniedMessage(String message) {
        permissionDeniedMessage = message;
    }

    private String getPermissionDeniedMessage() {
        return permissionDeniedMessage == null ? getString(R.string.permissions_denied) : permissionDeniedMessage;
    }

    protected void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            requestPermissions(permissions, PERMISSION_REQUEST);
        }
    }

    protected boolean hasPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(BaseActivitySettings.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //User has deny from permission dialog
                        Snackbar.make(getSnackBarContainer(), getPermissionDeniedMessage(),
                                Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.action_grant, view -> requestPermissions())
                                .setActionTextColor(ThemeStore.accentColor(this))
                                .show();
                    } else {
                        // User has deny permission and checked never show permission dialog so you can redirect to Application settings page
                        Snackbar.make(getSnackBarContainer(), getPermissionDeniedMessage(),
                                Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.settings, view -> {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", BaseActivitySettings.this.getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                })
                                .setActionTextColor(ThemeStore.accentColor(this))
                                .show();
                    }
                    return;
                }
            }
            hadPermissions = true;
        }
    }
    public void setStatusbarColor(int color,StatusBarView statusBarView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (statusBarView != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    statusBarView.setBackgroundColor(ColorUtil.darkenColor(color));
                    setLightStatusbarAuto(color);
                } else {
                    statusBarView.setBackgroundColor(color);
                }
            }
        }
    }

    public void setLightStatusbar(boolean enabled) {
        ATH.setLightStatusbar(this, enabled);
    }

    public void setLightStatusbarAuto(int bgColor) {

        setLightStatusbar(ColorUtil.isColorLight(bgColor));
    }
    protected void setDrawUnderStatusbar(boolean drawUnderStatusbar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            Utils.setAllowDrawUnderStatusBar(getWindow());
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            Utils.setStatusBarTranslucent(getWindow());
    }
    public void setTaskDescriptionColorAuto() {
        setTaskDescriptionColor(Utils.getPrimaryColor(this));
    }
    public void setTaskDescriptionColor(@ColorInt int color) {
        ATH.setTaskDescriptionColor(this, color);
    }

}
