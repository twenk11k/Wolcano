package com.wolcano.musicplayer.music.ui.dialog;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.text.Html;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.triggertrap.seekarc.SeekArc;
import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.provider.MusicService;
import com.wolcano.musicplayer.music.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.wolcano.musicplayer.music.constants.Constants.ACTION_PAUSE;
import static com.wolcano.musicplayer.music.constants.Constants.ACTION_STOP_SLEEP;

public class SleepTimerDialog extends DialogFragment {
    @BindView(R.id.seek_arc)
    SeekArc seekArc;
    @BindView(R.id.timer_display)
    TextView timerDisplay;

    private int seekArcProgress;
    private MaterialDialog materialDialog;
    private TimerUpdater timerUpdater;

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        timerUpdater.cancel();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        timerUpdater = new TimerUpdater();
        materialDialog = new MaterialDialog.Builder(getActivity())
                .title(getActivity().getResources().getString(R.string.sleeptimer))
                .positiveText(R.string.action_set)
                .onPositive((dialog, which) -> {
                    if (getActivity() == null) {
                        return;
                    }

                    final int minutes = seekArcProgress;

                    PendingIntent pi = makeTimerPendingIntent(PendingIntent.FLAG_CANCEL_CURRENT);

                    final long nextSleepTimerElapsedTime = SystemClock.elapsedRealtime() + minutes * 60 * 1000;
                    Utils.setNextSleepTimerElapsedRealtime(getContext(),nextSleepTimerElapsedTime);
                    AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, nextSleepTimerElapsedTime, pi);
                    if(minutes==1){
                        Toast.makeText(getActivity(), Html.fromHtml(getContext().getString(R.string.sleep_timer_set_one,minutes)), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), Html.fromHtml(getContext().getString(R.string.sleep_timer_set,minutes)), Toast.LENGTH_SHORT).show();
                    }
                })
                .onNeutral((dialog, which) -> {
                    if (getActivity() == null) {
                        return;
                    }
                    final PendingIntent previous = makeTimerPendingIntent(PendingIntent.FLAG_NO_CREATE);
                    if (previous != null) {
                        AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                        am.cancel(previous);
                        previous.cancel();
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.sleep_timer_canceled), Toast.LENGTH_SHORT).show();
                    }
                })
                .showListener(dialog -> {
                    if (makeTimerPendingIntent(PendingIntent.FLAG_NO_CREATE) != null) {
                        timerUpdater.start();
                    }
                })
                .customView(R.layout.dialog_sleep_timer, false)
                .build();

        if (getActivity() == null || materialDialog.getCustomView() == null) {
            return materialDialog;
        }

        ButterKnife.bind(this, materialDialog.getCustomView());

        seekArc.setProgressColor(Utils.getAccentColor(getContext()));
        seekArc.setThumbColor(Utils.getAccentColor(getContext()));

        seekArc.post(() -> {
            int width = seekArc.getWidth();
            int height = seekArc.getHeight();
            int small = Math.min(width, height);

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(seekArc.getLayoutParams());
            layoutParams.height = small;
            seekArc.setLayoutParams(layoutParams);
        });

        seekArcProgress = Utils.getLastSleepTimerValue(getContext());
        updateTimeDisplayTime();
        seekArc.setProgress(seekArcProgress);

        seekArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(@NonNull SeekArc seekArc, int i, boolean b) {
                if (i < 1) {
                    seekArc.setProgress(1);
                    return;
                }
                seekArcProgress = i;
                updateTimeDisplayTime();
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {

            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
                Utils.setLastSleepTimerValue(getContext(),seekArcProgress);
            }
        });

        return materialDialog;
    }

    private void updateTimeDisplayTime() {
        timerDisplay.setText(seekArcProgress +" "+ getString(R.string.sleeptimer_min));
    }

    private PendingIntent makeTimerPendingIntent(int flag) {
        return PendingIntent.getService(getActivity(), 0, makeTimerIntent(), flag);
    }

    private Intent makeTimerIntent() {
        int sleepval = Utils.getOpeningValSleep(getContext());
        if (sleepval == 1) {
            return new Intent(getActivity(), MusicService.class)
                    .setAction(ACTION_STOP_SLEEP);
        }  else {
            return new Intent(getActivity(), MusicService.class)
                    .setAction(ACTION_PAUSE);
        }
    }

    private class TimerUpdater extends CountDownTimer {
        private TimerUpdater() {
            super(Utils.getNextSleepTimerElapsedRealTime(getContext()) - SystemClock.elapsedRealtime(), 1000);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            materialDialog.setActionButton(DialogAction.NEUTRAL, materialDialog.getContext().getString(R.string.cancel_current_timer) + " (" + Utils.getReadableDura(millisUntilFinished) + ")");
        }

        @Override
        public void onFinish() {
            materialDialog.setActionButton(DialogAction.NEUTRAL, null);
        }
    }
}