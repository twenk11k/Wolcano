package com.wolcano.musicplayer.music.widgets

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.wolcano.musicplayer.music.utils.Utils.getStatHeight

class StatusBarView : View {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun canDrawBehindStatusBar(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (canDrawBehindStatusBar()) {
            setMeasuredDimension(
                widthMeasureSpec, getStatHeight(
                    context
                )
            )
        } else {
            setMeasuredDimension(0, 0)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isInEditMode) {
            return
        }
    }

}