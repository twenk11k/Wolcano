package com.wolcano.musicplayer.music.ui.dialog

import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.customview.customView
import com.triggertrap.seekarc.SeekArc
import com.triggertrap.seekarc.SeekArc.OnSeekArcChangeListener
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.databinding.DialogSleepTimerBinding
import com.wolcano.musicplayer.music.provider.MusicService
import com.wolcano.musicplayer.music.utils.Constants.ACTION_PAUSE
import com.wolcano.musicplayer.music.utils.Constants.ACTION_STOP_SLEEP
import com.wolcano.musicplayer.music.utils.Utils.getAccentColor
import com.wolcano.musicplayer.music.utils.Utils.getLastSleepTimerValue
import com.wolcano.musicplayer.music.utils.Utils.getNextSleepTimerElapsedRealTime
import com.wolcano.musicplayer.music.utils.Utils.getOpeningValSleep
import com.wolcano.musicplayer.music.utils.Utils.setLastSleepTimerValue
import com.wolcano.musicplayer.music.utils.Utils.setNextSleepTimerElapsedRealtime
import kotlin.math.min

class SleepTimerDialog : DialogFragment() {

    private var seekArcProgress = 0
    private lateinit var materialDialog: MaterialDialog
    private var timerUpdater: TimerUpdater? = null

    private lateinit var binding: DialogSleepTimerBinding
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        timerUpdater?.cancel()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_sleep_timer,
            null,
            false
        )
        timerUpdater = TimerUpdater()
        materialDialog = MaterialDialog(requireActivity()).show {
            title(text = requireActivity().resources!!.getString(R.string.sleeptimer))
            positiveButton(R.string.action_set) {
                if (activity == null) {
                    return@positiveButton
                }
                val minutes = seekArcProgress
                val pi: PendingIntent? = makeTimerPendingIntent(PendingIntent.FLAG_CANCEL_CURRENT)
                val nextSleepTimerElapsedTime =
                    SystemClock.elapsedRealtime() + minutes * 60 * 1000
                setNextSleepTimerElapsedRealtime(
                    requireContext(),
                    nextSleepTimerElapsedTime
                )
                val alarmManager =
                    requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager[AlarmManager.ELAPSED_REALTIME_WAKEUP, nextSleepTimerElapsedTime] = pi
                if (minutes == 1) {
                    Toast.makeText(
                        requireActivity(),
                        HtmlCompat.fromHtml(
                            requireContext().getString(
                                R.string.sleep_timer_set_one,
                                minutes
                            ),
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireActivity(),
                        HtmlCompat.fromHtml(
                            requireContext().getString(
                                R.string.sleep_timer_set,
                                minutes
                            ),
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            neutralButton {
                if (activity == null) {
                    return@neutralButton
                }
                val previous: PendingIntent? = makeTimerPendingIntent(PendingIntent.FLAG_NO_CREATE)
                if (previous != null) {
                    val am =
                        requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    am.cancel(previous)
                    previous.cancel()
                    Toast.makeText(
                        requireActivity(),
                        requireActivity().getString(R.string.sleep_timer_canceled),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            setOnShowListener {
                if (makeTimerPendingIntent(PendingIntent.FLAG_NO_CREATE) != null) {
                    timerUpdater?.start()
                }
            }
            customView(view = binding.root)
        }

        if (activity == null) {
            return materialDialog
        }

        binding.seekArc.progressColor = getAccentColor(requireContext())
        binding.seekArc.setThumbColor(getAccentColor(requireContext()))

        binding.seekArc.post {
            val width = binding.seekArc.width
            val height = binding.seekArc.height
            val small = min(width, height)
            val layoutParams =
                FrameLayout.LayoutParams(binding.seekArc.layoutParams)
            layoutParams.height = small
            binding.seekArc.layoutParams = layoutParams
        }

        seekArcProgress = getLastSleepTimerValue(requireContext())
        updateTimeDisplayTime()
        binding.seekArc.progress = seekArcProgress

        binding.seekArc.setOnSeekArcChangeListener(object : OnSeekArcChangeListener {
            override fun onProgressChanged(seekArc: SeekArc, i: Int, b: Boolean) {
                if (i < 1) {
                    seekArc.progress = 1
                    return
                }
                seekArcProgress = i
                updateTimeDisplayTime()
            }

            override fun onStartTrackingTouch(seekArc: SeekArc) {}
            override fun onStopTrackingTouch(seekArc: SeekArc) {
                setLastSleepTimerValue(requireContext(), seekArcProgress)
            }
        })

        return materialDialog
    }

    private fun updateTimeDisplayTime() {
        binding.timerDisplay.text =
            seekArcProgress.toString() + " " + getString(R.string.sleeptimer_min)
    }

    private fun makeTimerPendingIntent(flag: Int): PendingIntent? {
        return PendingIntent.getService(requireActivity(), 0, makeTimerIntent(), flag)
    }

    private fun makeTimerIntent(): Intent {
        val sleepval = getOpeningValSleep(requireContext())
        return if (sleepval == 1) {
            Intent(requireActivity(), MusicService::class.java)
                .setAction(ACTION_STOP_SLEEP)
        } else {
            Intent(requireActivity(), MusicService::class.java)
                .setAction(ACTION_PAUSE)
        }
    }

    private inner class TimerUpdater : CountDownTimer(
        getNextSleepTimerElapsedRealTime(requireContext()) - SystemClock.elapsedRealtime(), 1000
    ) {
        override fun onTick(millisUntilFinished: Long) {
            materialDialog.setActionButtonEnabled(WhichButton.NEUTRAL, true)
            /*materialDialog.setActionButton(
                DialogAction.NEUTRAL,
                materialDialog.context.getString(R.string.cancel_current_timer)
                    .toString() + " (" + getReadableDura(millisUntilFinished) + ")"
            )
            */
        }

        override fun onFinish() {
            materialDialog.setActionButtonEnabled(WhichButton.NEUTRAL, false)
        }
    }

}