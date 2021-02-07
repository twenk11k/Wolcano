package com.wolcano.musicplayer.music.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;

import com.wolcano.musicplayer.music.R;

public class BorderCircleView extends FrameLayout {

    private final Drawable mCheck;
    private final Paint paint;
    private final Paint paintBorder;
    private final int borderWidth;
    private Paint paintCheck;
    private PorterDuffColorFilter blackFilter;
    private PorterDuffColorFilter whiteFilter;

    public BorderCircleView(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public BorderCircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BorderCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mCheck = ContextCompat.getDrawable(context, R.drawable.border_circle_view_check);
        this.borderWidth = (int) this.getResources().getDimension(R.dimen.border_circle_view_border);
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paintBorder = new Paint();
        this.paintBorder.setAntiAlias(true);
        this.paintBorder.setColor(-16777216);
        this.setWillNotDraw(false);
    }

    public void setBackgroundColor(int color) {
        this.paint.setColor(color);
        this.requestLayout();
        this.invalidate();
    }

    public void setBorderColor(int color) {
        this.paintBorder.setColor(color);
        this.requestLayout();
        this.invalidate();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == 1073741824 && heightMode != 1073741824) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = width;
            if (heightMode == -2147483648) {
                height = Math.min(width, MeasureSpec.getSize(heightMeasureSpec));
            }

            this.setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int canvasSize = canvas.getWidth();
        if (canvas.getHeight() < canvasSize) {
            canvasSize = canvas.getHeight();
        }

        int circleCenter = (canvasSize - this.borderWidth * 2) / 2;
        canvas.drawCircle((float) (circleCenter + this.borderWidth), (float) (circleCenter + this.borderWidth), (float) ((canvasSize - this.borderWidth * 2) / 2 + this.borderWidth) - 4.0F, this.paintBorder);
        canvas.drawCircle((float) (circleCenter + this.borderWidth), (float) (circleCenter + this.borderWidth), (float) ((canvasSize - this.borderWidth * 2) / 2) - 4.0F, this.paint);
        if (this.isActivated()) {
            int offset = canvasSize / 2 - this.mCheck.getIntrinsicWidth() / 2;
            if (this.paintCheck == null) {
                this.paintCheck = new Paint();
                this.paintCheck.setAntiAlias(true);
            }

            if (this.whiteFilter == null || this.blackFilter == null) {
                this.blackFilter = new PorterDuffColorFilter(-16777216, PorterDuff.Mode.SRC_IN);
                this.whiteFilter = new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN);
            }

            if (this.paint.getColor() == -1) {
                this.paintCheck.setColorFilter(this.blackFilter);
            } else {
                this.paintCheck.setColorFilter(this.whiteFilter);
            }

            this.mCheck.setBounds(offset, offset, this.mCheck.getIntrinsicWidth() - offset, this.mCheck.getIntrinsicHeight() - offset);
            this.mCheck.draw(canvas);
        }

    }

}
