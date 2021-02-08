package com.wolcano.musicplayer.music.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.wolcano.musicplayer.music.R
import kotlin.math.min

class BorderCircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var mCheck: Drawable? =
        ContextCompat.getDrawable(context, R.drawable.border_circle_view_check)
    private val paint: Paint = Paint()
    private val paintBorder: Paint
    private val borderWidth: Int =
        this.resources.getDimension(R.dimen.border_circle_view_border).toInt()
    private var paintCheck: Paint? = null
    private var blackFilter: PorterDuffColorFilter? = null
    private var whiteFilter: PorterDuffColorFilter? = null
    override fun setBackgroundColor(color: Int) {
        paint.color = color
        requestLayout()
        this.invalidate()
    }

    fun setBorderColor(color: Int) {
        paintBorder.color = color
        requestLayout()
        this.invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (widthMode == 1073741824 && heightMode != 1073741824) {
            val width = MeasureSpec.getSize(widthMeasureSpec)
            var height = width
            if (heightMode == -2147483648) {
                height = min(width, MeasureSpec.getSize(heightMeasureSpec))
            }
            setMeasuredDimension(width, height)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var canvasSize = width
        if (height < canvasSize) {
            canvasSize = height
        }
        val circleCenter = (canvasSize - borderWidth * 2) / 2
        canvas.drawCircle(
            (circleCenter + borderWidth).toFloat(),
            (circleCenter + borderWidth).toFloat(),
            ((canvasSize - borderWidth * 2) / 2 + borderWidth).toFloat() - 4.0f,
            paintBorder
        )
        canvas.drawCircle(
            (circleCenter + borderWidth).toFloat(),
            (circleCenter + borderWidth).toFloat(),
            ((canvasSize - borderWidth * 2) / 2).toFloat() - 4.0f,
            paint
        )
        if (this.isActivated) {
            val offset = canvasSize / 2 - mCheck!!.intrinsicWidth / 2
            if (paintCheck == null) {
                paintCheck = Paint()
                paintCheck?.isAntiAlias = true
            }
            if (whiteFilter == null || blackFilter == null) {
                blackFilter = PorterDuffColorFilter(-16777216, PorterDuff.Mode.SRC_IN)
                whiteFilter = PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN)
            }
            if (paint.color == -1) {
                paintCheck?.colorFilter = blackFilter
            } else {
                paintCheck?.colorFilter = whiteFilter
            }
            mCheck?.setBounds(
                offset,
                offset,
                mCheck!!.intrinsicWidth - offset,
                mCheck!!.intrinsicHeight - offset
            )
            mCheck?.draw(canvas)
        }
    }

    init {
        paint.isAntiAlias = true
        paintBorder = Paint()
        paintBorder.isAntiAlias = true
        paintBorder.color = -16777216
        setWillNotDraw(false)
    }

}