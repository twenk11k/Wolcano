package com.wolcano.musicplayer.music.listener

import android.content.Context
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import com.wolcano.musicplayer.music.provider.RemotePlay.next
import com.wolcano.musicplayer.music.provider.RemotePlay.prev
import kotlin.math.abs

open class OnSwipeTouchListener(private val context: Context) : OnTouchListener {

    private var gestureDetector: GestureDetector? = null

    init {
        gestureDetector = GestureDetector(context, GestureListener(this))
    }

    open fun onSwipeLeft() {
        next(context, false)
    }

    open fun onSwipeRight() {
        prev(context)
    }

    open fun onClick() {}

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return gestureDetector!!.onTouchEvent(event)
    }

    private inner class GestureListener(private val mHelper: OnSwipeTouchListener) :
        SimpleOnGestureListener() {
        private val SWIPE_DISTANCE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            mHelper.onClick()
            return true
        }

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val distanceX = e2.x - e1.x
            val distanceY = e2.y - e1.y
            if (abs(distanceX) > abs(distanceY) && abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && abs(
                    velocityX
                ) > SWIPE_VELOCITY_THRESHOLD
            ) {
                if (distanceX > 0) onSwipeRight() else onSwipeLeft()
                return true
            }
            return false
        }
    }

}