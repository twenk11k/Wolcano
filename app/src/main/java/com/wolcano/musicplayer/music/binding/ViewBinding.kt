package com.wolcano.musicplayer.music.binding

import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import com.skydoves.whatif.whatIfNotNullOrEmpty
import com.wolcano.musicplayer.music.utils.Utils

object ViewBinding {

    @JvmStatic
    @BindingAdapter("gone")
    fun bindGone(view: View, shouldBeGone: Boolean) {
        view.visibility = if (shouldBeGone) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    @JvmStatic
    @BindingAdapter("textDuration", "textArtist")
    fun convertDurationForOnline(view: TextView, durationLong: Long, artist: String) {
        var duration: String = Utils.getDuration(durationLong)
        duration = (if (duration.isEmpty()) "" else duration) + "  |  " + artist
        view.text = duration
    }

    @JvmStatic
    @BindingAdapter("toast")
    fun bindToast(view: View, text: String?) {
        text.whatIfNotNullOrEmpty {
            Toast.makeText(view.context, it, Toast.LENGTH_SHORT).show()
        }
    }

}