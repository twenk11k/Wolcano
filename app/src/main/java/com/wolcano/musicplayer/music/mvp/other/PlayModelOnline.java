package com.wolcano.musicplayer.music.mvp.other;

import android.app.Activity;
import android.app.Dialog;

import androidx.appcompat.app.AlertDialog;

import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.mvp.listener.TaskInterface;
import com.wolcano.musicplayer.music.mvp.models.Song;
import com.wolcano.musicplayer.music.utils.NetworkUtils;
import com.wolcano.musicplayer.music.utils.Utils;

import java.util.List;


public abstract class PlayModelOnline implements TaskInterface<Song> {

    protected Song song;
    private Activity activity;
    protected List<Song> songList;

    public PlayModelOnline(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onTask() {
        checkMobileConnection();
    }

    private void getPlayModelImpl() {
        onPrepare();
        setPlayModel();
    }

    protected abstract void setPlayModel();

    private void checkMobileConnection() {
        boolean isMobileNet = Utils.getMobileInteret(activity.getApplicationContext());
        if (NetworkUtils.isMobileActive(activity) && !isMobileNet) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(R.string.warning);
            builder.setMessage(R.string.control_inter_message);
            builder.setNegativeButton(R.string.no, null);
            builder.setPositiveButton(R.string.yes, (dialog, which) -> {
                Utils.setMobileInteret(activity.getApplicationContext(), true);
                getPlayModelImpl();
            });
            Dialog mobileDialog = builder.create();
            mobileDialog.setCanceledOnTouchOutside(false);
            mobileDialog.show();
        } else {
            getPlayModelImpl();
        }
    }


}
