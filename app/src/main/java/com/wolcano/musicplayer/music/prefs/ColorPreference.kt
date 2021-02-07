package com.wolcano.musicplayer.music.prefs

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.widgets.BorderCircleView

class ColorPreference(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    Preference(context, attrs, defStyleAttr) {
    private var mView: View? = null
    private var color = 0
    private var border = 0

    constructor(context: Context?) : this(context, null as AttributeSet?, 0) {
        init(context, null as AttributeSet?)
    }

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0) {
        init(context, attrs)
    }

    private fun init(context: Context?, attrs: AttributeSet?) {
        this.layoutResource = R.layout.custom_list_preference
        this.widgetLayoutResource = R.layout.color_preference
        this.isPersistent = false
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        mView = holder.itemView
        invalidateColor()
    }

    fun setColor(color: Int, border: Int) {
        this.color = color
        this.border = border
        invalidateColor()
    }

    private fun invalidateColor() {
        if (mView != null) {
            val circle = mView!!.findViewById<View>(R.id.circle) as BorderCircleView
            if (color != 0) {
                circle.visibility = View.VISIBLE
                circle.setBackgroundColor(color)
                circle.setBorderColor(border)
            } else {
                circle.visibility = View.GONE
            }
        }
    }

    init {
        init(context, attrs)
    }
}