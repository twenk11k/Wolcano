package com.wolcano.musicplayer.music.widgets;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.wolcano.musicplayer.music.R;
import com.wolcano.musicplayer.music.utils.ImageUtils;


public class ModelView extends View implements ValueAnimator.AnimatorUpdateListener {



    private final float HELPER_PLAY = 0.0f;
    private final float HELPER_PAUSE = -25.0f;
    private final float DICS_INCR = 0.5f;
    private boolean isPlaying = false;
    private Point dicsPnt = new Point();
    private Point modelCoverPnt = new Point();
    private Handler handler = new Handler();
    private Bitmap dicsBitmap;
    private Bitmap helperBitmap;
    private Drawable aboveLine;
    private Point helperCntPnt = new Point();
    private Point modelCoverCntPnt = new Point();
    private float helperRtte = HELPER_PLAY;
    private int cizgiHeight;
    private int modelBWidth;
    private Point helperPnt = new Point();
    private Point discCntPnt = new Point();
    private Drawable modelB;
    private Matrix dicsMatrix = new Matrix();
    private Matrix modelMatrix = new Matrix();
    private Matrix helperMatrix = new Matrix();
    private ValueAnimator playAnim;
    private ValueAnimator pauseAnim;
    private float dicsRtt = 0.0f;
    private Bitmap modelBitmap;
    private static final long GUN_ZAMN = 50L;



    public ModelView(Context context) {
        this(context, null);
    }




    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            initModelViewLayout();
        }
    }
    public ModelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initModelView();
    }
    public ModelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private void initModelView() {
        aboveLine = getResources().getDrawable(R.drawable.play_topline);
        modelB = getResources().getDrawable(R.drawable.play_border);
        dicsBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.album_dics);
        modelBitmap = SongCover.get().loadOval(getContext(),null);
        helperBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.grip);
        cizgiHeight = dpDenPx(1);
        modelBWidth = dpDenPx(1);

        playAnim = ValueAnimator.ofFloat(HELPER_PAUSE, HELPER_PLAY);
        playAnim.setDuration(300);
        playAnim.addUpdateListener(this);
        pauseAnim = ValueAnimator.ofFloat(HELPER_PLAY, HELPER_PAUSE);
        pauseAnim.setDuration(300);
        pauseAnim.addUpdateListener(this);
    }


    public void initHelper(boolean isPlaying) {
        helperRtte = isPlaying ? HELPER_PLAY : HELPER_PAUSE;
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        aboveLine.setBounds(0, 0, getWidth(), cizgiHeight);
        aboveLine.draw(canvas);
        modelB.setBounds(dicsPnt.x - modelBWidth, dicsPnt.y - modelBWidth,
                dicsPnt.x + dicsBitmap.getWidth() + modelBWidth, dicsPnt.y + dicsBitmap.getHeight() + modelBWidth);
        modelB.draw(canvas);
        dicsMatrix.setRotate(dicsRtt, discCntPnt.x, discCntPnt.y);
        dicsMatrix.preTranslate(dicsPnt.x, dicsPnt.y);
        canvas.drawBitmap(dicsBitmap, dicsMatrix, null);
        modelMatrix.setRotate(dicsRtt, modelCoverCntPnt.x, modelCoverCntPnt.y);
        modelMatrix.preTranslate(modelCoverPnt.x, modelCoverPnt.y);
        canvas.drawBitmap(modelBitmap, modelMatrix, null);
        helperMatrix.setRotate(helperRtte, helperCntPnt.x, helperCntPnt.y);
        helperMatrix.preTranslate(helperPnt.x, helperPnt.y);
        canvas.drawBitmap(helperBitmap, helperMatrix, null);
    }

    private void initModelViewLayout() {
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        int unit = Math.min(getWidth(), getHeight()) / 8;
        SongCover.get().setOvalLength(unit * 4);
        dicsBitmap = ImageUtils.chgImage(dicsBitmap, unit * 6, unit * 6);
        modelBitmap = ImageUtils.chgImage(modelBitmap, unit * 4, unit * 4);
        helperBitmap = ImageUtils.chgImage(helperBitmap, unit * 2, unit * 3);

        int discOffsetY = helperBitmap.getHeight() / 2;
        dicsPnt.x = (getWidth() - dicsBitmap.getWidth()) / 2;
        dicsPnt.y = discOffsetY;
        modelCoverPnt.x = (getWidth() - modelBitmap.getWidth()) / 2;
        modelCoverPnt.y = discOffsetY + (dicsBitmap.getHeight() - modelBitmap.getHeight()) / 2;
        helperPnt.x = getWidth() / 2 - helperBitmap.getWidth() / 6;
        helperPnt.y = -helperBitmap.getWidth() / 6;
        discCntPnt.x = getWidth() / 2;
        discCntPnt.y = dicsBitmap.getHeight() / 2 + discOffsetY;
        modelCoverCntPnt.x = discCntPnt.x;
        modelCoverCntPnt.y = discCntPnt.y;
        helperCntPnt.x = discCntPnt.x;
        helperCntPnt.y = 0;
    }

    public void setRoundBitmap(Bitmap bitmap) {
        modelBitmap = bitmap;
        dicsRtt = 0.0f;
        invalidate();
    }
    public void pause() {
        if (!isPlaying) {
            return;
        }
        isPlaying = false;
        handler.removeCallbacks(runnableRtt);
        pauseAnim.start();
    }
    public void start() {
        if (isPlaying) {
            return;
        }
        isPlaying = true;
        handler.post(runnableRtt);
        playAnim.start();
    }



    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        helperRtte = (float) animation.getAnimatedValue();
        invalidate();
    }
    private int dpDenPx(float dpValue) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }
    private Runnable runnableRtt = new Runnable() {
        @Override
        public void run() {
            if (isPlaying) {
                dicsRtt += DICS_INCR;
                if (dicsRtt >= 360) {
                    dicsRtt = 0;
                }
                invalidate();
            }
            handler.postDelayed(this, GUN_ZAMN);
        }
    };


}
