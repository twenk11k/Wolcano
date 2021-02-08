package com.wolcano.musicplayer.music.prefs

import android.content.Context
import android.util.AttributeSet
import androidx.preference.ListPreference
import com.wolcano.musicplayer.music.R

class CustomListPreference : ListPreference {

    constructor(context: Context) : super(context) {
        init(context, null as AttributeSet?)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        this.layoutResource = R.layout.custom_list_preference
        if (this.summary == null || this.summary.toString().trim { it <= ' ' }.isEmpty()) {
            this.summary = "%s"
        }
    }

}