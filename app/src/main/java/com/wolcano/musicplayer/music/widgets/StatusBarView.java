package com.wolcano.musicplayer.music.widgets;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.wolcano.musicplayer.music.utils.Utils;


public class StatusBarView extends View {

    public StatusBarView(Context context) {
        super(context);
    }

    public StatusBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StatusBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public boolean canDrawBehindStatusBar() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (canDrawBehindStatusBar()) {
            setMeasuredDimension(widthMeasureSpec, (int) Utils.getStatHeight(getContext()));
        } else {
            setMeasuredDimension(0, 0);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (isInEditMode()) {
            return;
        }

    }
}