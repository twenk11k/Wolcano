package com.wolcano.musicplayer.music.prefs

import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceViewHolder
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.utils.Utils.getAccentColor

class CustomPreferenceCategory : PreferenceCategory {

    @TargetApi(21)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context) : super(context) {
        init(context, null as AttributeSet?)
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val titleTextView = holder.itemView as TextView
        titleTextView.setTextColor(getAccentColor(context))
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        this.layoutResource = R.layout.custom_preference_category
    }

}