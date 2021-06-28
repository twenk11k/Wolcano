package com.twenk11k.seekarc

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.*

class SeekArc : View {

    companion object {
        private val TAG = SeekArc::class.java.simpleName
        private const val INVALID_PROGRESS_VALUE = -1

        // The initial rotational offset -90 means we start at 12 o'clock
        private const val angleOffset = -90
    }

    /**
     * The Drawable for the seek arc thumbnail
     */
    private var thumb: Drawable? = null

    /**
     * The Maximum value that this SeekArc can be set to
     */
    private var max = 100

    /**
     * The Current value that the SeekArc is set to
     */
    private var progress = 0

    /**
     * The width of the progress line for this SeekArc
     */
    private var progressWidth = 4

    /**
     * The Width of the background arc for the SeekArc
     */
    private var arcWidth = 2

    /**
     * The Angle to start drawing this Arc from
     */
    private var startAngle = 0

    /**
     * The Angle through which to draw the arc (Max is 360)
     */
    private var sweepAngle = 360

    /**
     * The rotation of the SeekArc- 0 is twelve o'clock
     */
    private var rotation = 0

    /**
     * Give the SeekArc rounded edges
     */
    private var roundedEdges = false

    /**
     * Enable touch inside the SeekArc
     */
    private var touchInside = true

    /**
     * Will the progress increase clockwise or anti-clockwise
     */
    private var clockwise = true

    /**
     * is the control enabled/touchable
     */
    private var enabled = true

    // Internal variables
    private var arcRadius = 0
    private var progressSweep = 0f
    private val arcRect = RectF()
    private lateinit var arcPaint: Paint
    private lateinit var progressPaint: Paint
    private var translateX = 0
    private var translateY = 0
    private var thumbXPos = 0
    private var thumbYPos = 0
    private var touchAngle = 0.0
    private var touchIgnoreRadius = 0f
    private var onSeekArcChangeListener: OnSeekArcChangeListener? = null

    interface OnSeekArcChangeListener {
        /**
         * Notification that the progress level has changed. Clients can use the
         * fromUser parameter to distinguish user-initiated changes from those
         * that occurred programmatically.
         *
         * @param seekArc  The SeekArc whose progress has changed
         * @param progress The current progress level. This will be in the range
         * 0..max where max was set by
         * [ProgressArc.setMax]. (The default value for
         * max is 100.)
         * @param fromUser True if the progress change was initiated by the user.
         */
        fun onProgressChanged(seekArc: SeekArc, progress: Int, fromUser: Boolean)

        /**
         * Notification that the user has started a touch gesture. Clients may
         * want to use this to disable advancing the seekbar.
         *
         * @param seekArc The SeekArc in which the touch gesture began
         */
        fun onStartTrackingTouch(seekArc: SeekArc)

        /**
         * Notification that the user has finished a touch gesture. Clients may
         * want to use this to re-enable advancing the seekarc.
         *
         * @param seekArc The SeekArc in which the touch gesture began
         */
        fun onStopTrackingTouch(seekArc: SeekArc)
    }

    constructor(context: Context?) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {

        init(context, attrs, R.attr.seekArcStyle)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(context, attrs, defStyle)
    }

    private fun init(context: Context?, attrs: AttributeSet?, defStyle: Int) {
        Log.d(TAG, "Initialising SeekArc")
        val density = context!!.resources.displayMetrics.density
        // Defaults, may need to link this into theme settings

        var arcColor = ContextCompat.getColor(context, R.color.progress_gray)

        var progressColor = ContextCompat.getColor(context, R.color.default_blue_light)
        val thumbHalfHeight: Int
        val thumbHalfWidth: Int

        thumb = ContextCompat.getDrawable(context, R.drawable.seek_arc_control_selector)
        // Convert progress width to pixels for current density
        progressWidth = (progressWidth * density).toInt()
        if (attrs != null) {
            // Attribute initialization
            val a = context.obtainStyledAttributes(
                attrs,
                R.styleable.SeekArc, defStyle, 0
            )
            val thumb = a.getDrawable(R.styleable.SeekArc_thumb)

            thumbHalfHeight = thumb!!.intrinsicHeight / 2
            thumbHalfWidth = thumb.intrinsicWidth / 2
            thumb.setBounds(
                -thumbHalfWidth, -thumbHalfHeight, thumbHalfWidth,
                thumbHalfHeight
            )
            max = a.getInteger(R.styleable.SeekArc_max, max)
            progress = a.getInteger(R.styleable.SeekArc_progress, progress)
            progressWidth = a.getDimension(
                R.styleable.SeekArc_progressWidth, progressWidth.toFloat()
            ).toInt()
            arcWidth = a.getDimension(
                R.styleable.SeekArc_arcWidth,
                arcWidth.toFloat()
            ).toInt()
            startAngle = a.getInt(R.styleable.SeekArc_startAngle, startAngle)
            sweepAngle = a.getInt(R.styleable.SeekArc_sweepAngle, sweepAngle)
            rotation = a.getInt(R.styleable.SeekArc_rotation, rotation)
            roundedEdges = a.getBoolean(R.styleable.SeekArc_roundEdges, roundedEdges)
            touchInside = a.getBoolean(R.styleable.SeekArc_touchInside, touchInside)
            clockwise = a.getBoolean(R.styleable.SeekArc_clockwise, clockwise)
            enabled = a.getBoolean(R.styleable.SeekArc_enabled, enabled)
            arcColor = a.getColor(R.styleable.SeekArc_arcColor, arcColor)
            progressColor = a.getColor(
                R.styleable.SeekArc_progressColor,
                progressColor
            )
            a.recycle()
        }
        progress = if (progress > max) max else progress
        progress = if (progress < 0) 0 else progress
        sweepAngle = if (sweepAngle > 360) 360 else sweepAngle
        sweepAngle = if (sweepAngle < 0) 0 else sweepAngle
        progressSweep = progress.toFloat() / max * sweepAngle
        startAngle = if (startAngle > 360) 0 else startAngle
        startAngle = if (startAngle < 0) 0 else startAngle
        arcPaint = Paint()
        arcPaint.color = arcColor
        arcPaint.isAntiAlias = true
        arcPaint.style = Paint.Style.STROKE
        arcPaint.strokeWidth = arcWidth.toFloat()
        //arcPaint.setAlpha(45);
        progressPaint = Paint()
        progressPaint.color = progressColor
        progressPaint.isAntiAlias = true
        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeWidth = progressWidth.toFloat()
        if (roundedEdges) {
            arcPaint.strokeCap = Paint.Cap.ROUND
            progressPaint.strokeCap = Paint.Cap.ROUND
        }
    }


    override fun onDraw(canvas: Canvas) {
        if (!clockwise) {
            canvas.scale(-1f, 1f, arcRect.centerX(), arcRect.centerY())
        }

        // Draw the arcs
        val arcStart: Int = startAngle + angleOffset + rotation
        val arcSweep: Int = sweepAngle
        canvas.drawArc(arcRect, arcStart.toFloat(), arcSweep.toFloat(), false, arcPaint)
        canvas.drawArc(
            arcRect, arcStart.toFloat(), progressSweep, false,
            progressPaint
        )
        if (enabled) {
            // Draw the thumb nail
            canvas.translate(
                (translateX - thumbXPos).toFloat(),
                (translateY - thumbYPos).toFloat()
            )
            thumb?.draw(canvas)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = getDefaultSize(
            suggestedMinimumHeight,
            heightMeasureSpec
        )
        val width = getDefaultSize(
            suggestedMinimumWidth,
            widthMeasureSpec
        )
        val min = min(width, height)
        val top: Float
        val left: Float
        val arcDiameter: Int
        translateX = (width * 0.5f).toInt()
        translateY = (height * 0.5f).toInt()
        arcDiameter = min - paddingLeft
        arcRadius = arcDiameter / 2
        top = (height / 2 - arcDiameter / 2).toFloat()
        left = (width / 2 - arcDiameter / 2).toFloat()
        arcRect[left, top, left + arcDiameter] = top + arcDiameter
        val arcStart: Int = progressSweep.toInt() + startAngle + rotation + 90
        thumbXPos = (arcRadius * cos(Math.toRadians(arcStart.toDouble()))).toInt()
        thumbYPos = (arcRadius * sin(Math.toRadians(arcStart.toDouble()))).toInt()
        setTouchInSide(touchInside)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (enabled) {
            this.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    onStartTrackingTouch()
                    updateOnTouch(event)
                }
                MotionEvent.ACTION_MOVE -> updateOnTouch(event)
                MotionEvent.ACTION_UP -> {
                    onStopTrackingTouch()
                    isPressed = false
                    this.parent.requestDisallowInterceptTouchEvent(false)
                }
                MotionEvent.ACTION_CANCEL -> {
                    onStopTrackingTouch()
                    isPressed = false
                    this.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            return true
        }
        return false
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (thumb != null && thumb!!.isStateful) {
            val state = drawableState
            thumb?.state = state
        }
        invalidate()
    }

    private fun onStartTrackingTouch() {
        onSeekArcChangeListener?.onStartTrackingTouch(this)
    }

    private fun onStopTrackingTouch() {
        onSeekArcChangeListener?.onStopTrackingTouch(this)
    }

    private fun updateOnTouch(event: MotionEvent) {
        val ignoreTouch: Boolean = ignoreTouch(event.x, event.y)
        if (ignoreTouch) {
            return
        }
        isPressed = true
        touchAngle = getTouchDegrees(event.x, event.y)
        val progress: Int = getProgressForAngle(touchAngle)
        onProgressRefresh(progress, true)
    }

    private fun ignoreTouch(xPos: Float, yPos: Float): Boolean {
        var ignore = false
        val x = xPos - translateX
        val y = yPos - translateY
        val touchRadius = sqrt((x * x + y * y).toDouble()).toFloat()
        if (touchRadius < touchIgnoreRadius) {
            ignore = true
        }
        return ignore
    }

    private fun getTouchDegrees(xPos: Float, yPos: Float): Double {
        var x = xPos - translateX
        val y = yPos - translateY
        //invert the x-coord if we are rotating anti-clockwise
        x = if (clockwise) x else -x
        // convert to arc Angle
        var angle = Math.toDegrees(
            atan2(y.toDouble(), x.toDouble()) + Math.PI / 2
                    - Math.toRadians(rotation.toDouble())
        )
        if (angle < 0) {
            angle += 360
        }
        angle -= startAngle.toDouble()
        return angle
    }

    private fun getProgressForAngle(angle: Double): Int {
        var touchProgress = Math.round(valuePerDegree() * angle).toInt()
        touchProgress = if (touchProgress < 0) INVALID_PROGRESS_VALUE else touchProgress
        touchProgress = if (touchProgress > max) INVALID_PROGRESS_VALUE else touchProgress
        return touchProgress
    }

    private fun valuePerDegree(): Float {
        return max.toFloat() / sweepAngle
    }

    private fun onProgressRefresh(progress: Int, fromUser: Boolean) {
        updateProgress(progress, fromUser)
    }

    private fun updateThumbPosition() {
        val thumbAngle = (startAngle + progressSweep + rotation + 90)
        thumbXPos = (arcRadius * cos(Math.toRadians(thumbAngle.toDouble()))).toInt()
        thumbYPos = (arcRadius * sin(Math.toRadians(thumbAngle.toDouble()))).toInt()
    }

    private fun updateProgress(progress: Int, fromUser: Boolean) {
        var progress = progress
        if (progress == INVALID_PROGRESS_VALUE) {
            return
        }
        progress = if (progress > max) max else progress
        progress = if (progress < 0) 0 else progress
        progress = progress
        onSeekArcChangeListener?.onProgressChanged(this, progress, fromUser)
        progressSweep = progress.toFloat() / max * sweepAngle
        updateThumbPosition()
        invalidate()
    }

    /**
     * Sets a listener to receive notifications of changes to the SeekArc's
     * progress level. Also provides notifications of when the user starts and
     * stops a touch gesture within the SeekArc.
     *
     * @param onSeekArcChangeListener The seek bar notification listener
     * @see SeekArc.OnSeekBarChangeListener
     */
    fun setOnSeekArcChangeListener(onSeekArcChangeListener: OnSeekArcChangeListener) {
        this.onSeekArcChangeListener = onSeekArcChangeListener
    }

    fun setProgress(progress: Int) {
        updateProgress(progress, false)
    }

    fun getProgress(): Int {
        return progress
    }

    fun getProgressWidth(): Int {
        return progressWidth
    }

    fun setProgressWidth(progressWidth: Int) {
        this.progressWidth = progressWidth
        progressPaint.strokeWidth = progressWidth.toFloat()
    }

    fun getArcWidth(): Int {
        return arcWidth
    }

    fun setArcWidth(arcWidth: Int) {
        this.arcWidth = arcWidth
        arcPaint.strokeWidth = arcWidth.toFloat()
    }

    fun getArcRotation(): Int {
        return rotation
    }

    fun setArcRotation(rotation: Int) {
        this.rotation = rotation
        updateThumbPosition()
    }

    fun getStartAngle(): Int {
        return startAngle
    }

    fun setStartAngle(startAngle: Int) {
        this.startAngle = startAngle
        updateThumbPosition()
    }

    fun getSweepAngle(): Int {
        return sweepAngle
    }

    fun setSweepAngle(sweepAngle: Int) {
        this.sweepAngle = sweepAngle
        updateThumbPosition()
    }

    fun setRoundedEdges(isEnabled: Boolean) {
        roundedEdges = isEnabled
        if (roundedEdges) {
            arcPaint.strokeCap = Paint.Cap.ROUND
            progressPaint.strokeCap = Paint.Cap.ROUND
        } else {
            arcPaint.strokeCap = Paint.Cap.SQUARE
            progressPaint.strokeCap = Paint.Cap.SQUARE
        }
    }

    fun setTouchInSide(isEnabled: Boolean) {
        val thumbHalfHeight = thumb!!.intrinsicHeight / 2
        val thumbHalfWidth = thumb!!.intrinsicWidth / 2
        touchInside = isEnabled
        touchIgnoreRadius = if (touchInside) {
            arcRadius.toFloat() / 4
        } else {
            // Don't use the exact radius makes interaction too tricky
            (arcRadius - min(thumbHalfWidth, thumbHalfHeight)).toFloat()
        }
    }

    fun setClockwise(clockwise: Boolean) {
        this.clockwise = clockwise
    }

    fun isClockwise(): Boolean {
        return clockwise
    }

    override fun isEnabled(): Boolean {
        return enabled
    }

    override fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }

    fun getProgressColor(): Int {
        return progressPaint.color
    }

    fun setProgressColor(color: Int) {
        progressPaint.color = color
        invalidate()
    }

    fun getArcColor(): Int {
        return arcPaint.color
    }

    fun setArcColor(color: Int) {
        arcPaint.color = color
        invalidate()
    }

    fun getMax(): Int {
        return max
    }

    fun setMax(max: Int) {
        this.max = max
    }
}