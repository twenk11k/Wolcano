package com.wolcano.musicplayer.music.prefs

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.SwitchCompat
import com.wolcano.musicplayer.music.ui.helper.TintHelper.setTint
import com.wolcano.musicplayer.music.utils.Utils.getAccentColor

class ColorSwitchCompat : SwitchCompat {

    constructor(context: Context) : super(context) {
        init(context, null)
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

    private fun init(context: Context, attrs: AttributeSet?) {
        setTint(this, getAccentColor(context), false)
    }

    override fun isShown(): Boolean {
        return parent != null && visibility == VISIBLE
    }

}