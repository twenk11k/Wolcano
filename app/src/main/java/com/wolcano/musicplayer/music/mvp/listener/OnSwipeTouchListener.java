package com.wolcano.musicplayer.music.mvp.listener;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.wolcano.musicplayer.music.provider.RemotePlay;


public class OnSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;
    private Context context;
    protected OnSwipeTouchListener(Context context) {
        this.context = context;
        gestureDetector = new GestureDetector(context, new GestureListener(this));
    }
    public void onSwipeLeft() {
        RemotePlay.get().next(context,false);
    }

    public void onSwipeRight() {
        RemotePlay.get().prev(context);
    }
    public void onClick() {

    }
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private final int SWIPE_DISTANCE_THRESHOLD = 100;
        private final int SWIPE_VELOCITY_THRESHOLD = 100;
        private OnSwipeTouchListener mHelper;

        GestureListener(OnSwipeTouchListener helper) {
            mHelper = helper;
        }
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            mHelper.onClick();
            return true;
        }
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0)
                    onSwipeRight();
                else
                    onSwipeLeft();
                return true;
            }
            return false;
        }
    }
}