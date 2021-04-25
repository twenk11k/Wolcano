package com.wolcano.musicplayer.music.widgets

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.wolcano.musicplayer.music.R
import com.wolcano.musicplayer.music.utils.ImageUtils
import com.wolcano.musicplayer.music.widgets.SongCover.loadOval
import com.wolcano.musicplayer.music.widgets.SongCover.setOvalLength
import kotlin.math.min

class ModelView : View, AnimatorUpdateListener {

    private val HELPER_PLAY = 0.0f
    private val HELPER_PAUSE = -25.0f
    private val DISC_INCR = 0.5f
    private var isPlaying = false
    private val discPoint = Point()
    private val modelCoverPnt = Point()
    private var aboveLine: Drawable? = null
    private val helperCntPnt = Point()
    private val modelCoverCntPnt = Point()
    private var helperRtte = HELPER_PLAY
    private var lineHeight = 0
    private var modelBWidth = 0
    private val helperPnt = Point()
    private val discCntPnt = Point()
    private var modelB: Drawable? = null
    private val discMatrix = Matrix()
    private val modelMatrix = Matrix()
    private val helperMatrix = Matrix()
    private var playAnim: ValueAnimator? = null
    private var pauseAnim: ValueAnimator? = null
    private var discRtt = 0.0f
    private var discBitmap: Bitmap? = null
    private var modelBitmap: Bitmap? = null
    private var helperBitmap: Bitmap? = null
    private val delay = 50L

    constructor(context: Context) : super(context, null)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initModelView()
    }

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            initModelViewLayout()
        }
    }

    private fun initModelView() {

        aboveLine = ContextCompat.getDrawable(context, R.drawable.play_topline)
        modelB = ContextCompat.getDrawable(context, R.drawable.play_border)
        discBitmap = BitmapFactory.decodeResource(resources, R.drawable.album_dics)
        modelBitmap = loadOval(context, null)
        helperBitmap = BitmapFactory.decodeResource(resources, R.drawable.grip)
        lineHeight = dpDenPx(1f)
        modelBWidth = dpDenPx(1f)
        playAnim = ValueAnimator.ofFloat(HELPER_PAUSE, HELPER_PLAY)
        playAnim?.duration = 300
        playAnim?.addUpdateListener(this)
        pauseAnim = ValueAnimator.ofFloat(HELPER_PLAY, HELPER_PAUSE)
        pauseAnim?.duration = 300
        pauseAnim?.addUpdateListener(this)
    }

    fun initHelper(isPlaying: Boolean) {
        helperRtte = if (isPlaying) HELPER_PLAY else HELPER_PAUSE
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        aboveLine?.setBounds(0, 0, width, lineHeight)
        aboveLine?.draw(canvas)
        modelB?.setBounds(
            discPoint.x - modelBWidth,
            discPoint.y - modelBWidth,
            discPoint.x + discBitmap!!.width + modelBWidth,
            discPoint.y + discBitmap!!.height + modelBWidth
        )
        modelB?.draw(canvas)
        discMatrix.setRotate(discRtt, discCntPnt.x.toFloat(), discCntPnt.y.toFloat())
        discMatrix.preTranslate(discPoint.x.toFloat(), discPoint.y.toFloat())
        if (discBitmap != null) {
            canvas.drawBitmap(discBitmap, discMatrix, null)
        }
        modelMatrix.setRotate(discRtt, modelCoverCntPnt.x.toFloat(), modelCoverCntPnt.y.toFloat())
        modelMatrix.preTranslate(modelCoverPnt.x.toFloat(), modelCoverPnt.y.toFloat())
        if (modelBitmap != null) {
            canvas.drawBitmap(modelBitmap!!, modelMatrix, null)
        }
        helperMatrix.setRotate(helperRtte, helperCntPnt.x.toFloat(), helperCntPnt.y.toFloat())
        helperMatrix.preTranslate(helperPnt.x.toFloat(), helperPnt.y.toFloat())
        if (helperBitmap != null) {
            canvas.drawBitmap(helperBitmap, helperMatrix, null)
        }
    }


    private fun initModelViewLayout() {
        if (width == 0 || height == 0) {
            return
        }
        val unit = min(width, height) / 8
        setOvalLength(unit * 4)
        discBitmap = ImageUtils.updateImage(discBitmap, unit * 6, unit * 6)
        modelBitmap = ImageUtils.updateImage(modelBitmap, unit * 4, unit * 4)
        helperBitmap = ImageUtils.updateImage(helperBitmap, unit * 2, unit * 3)
        val discOffsetY = helperBitmap!!.height / 2
        discPoint.x = (width - discBitmap!!.width) / 2
        discPoint.y = discOffsetY
        modelCoverPnt.x = (width - modelBitmap?.width!!) / 2
        modelCoverPnt.y = discOffsetY + (discBitmap!!.height - modelBitmap?.height!!) / 2
        helperPnt.x = width / 2 - helperBitmap?.width!! / 6
        helperPnt.y = -helperBitmap?.width!! / 6
        discCntPnt.x = width / 2
        discCntPnt.y = discBitmap!!.height / 2 + discOffsetY
        modelCoverCntPnt.x = discCntPnt.x
        modelCoverCntPnt.y = discCntPnt.y
        helperCntPnt.x = discCntPnt.x
        helperCntPnt.y = 0
    }

    fun setRoundBitmap(bitmap: Bitmap?) {
        modelBitmap = bitmap
        discRtt = 0.0f
        invalidate()
    }

    fun pause() {
        if (!isPlaying) {
            return
        }
        isPlaying = false
        handler.removeCallbacks(runnableRtt)
        pauseAnim?.start()
    }

    fun start() {
        if (isPlaying) {
            return
        }
        isPlaying = true
        handler.post(runnableRtt)
        playAnim?.start()
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        helperRtte = animation.animatedValue as Float
        invalidate()
    }

    private fun dpDenPx(dpValue: Float): Int {
        val density = context.resources.displayMetrics.density
        return (dpValue * density + 0.5f).toInt()
    }

    private val runnableRtt: Runnable = object : Runnable {
        override fun run() {
            if (isPlaying) {
                discRtt += DISC_INCR
                if (discRtt >= 360) {
                    discRtt = 0f
                }
                invalidate()
            }
            handler?.postDelayed(this, delay)
        }
    }

}