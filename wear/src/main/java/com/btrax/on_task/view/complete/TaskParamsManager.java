package com.btrax.on_task.view.complete;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;

/**
 * manage parameter of text
 */
class TaskParamsManager {

    private final static int TEXT_SIZE = 30;
    private final static int TEXT_COLOR = Color.WHITE;
    public final static int ALPHA_APPEARED = 255;
    public final static int ALPHA_PREAPPEARED = 0;
    public final static int ALPHA_APPEARED_SPEED = 25;
    public final static int ALPHA_DISAPPEARED_SPEED = -15;

    private Paint mPaint = new Paint();
    private Point mPoint = new Point();
    private int mDisplayWidth = -1;
    private int mDisplayHeight = -1;

    public TaskParamsManager() {
    }

    public void setDisplaySize(int width, int height) {
        mDisplayWidth = width;
        mDisplayHeight = height;
        reset();
    }

    public void reset(){
        mPaint.setColor(TEXT_COLOR);
        mPaint.setTextSize(TEXT_SIZE);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint.setAntiAlias(true);
        mPaint.setAlpha(ALPHA_PREAPPEARED);

        mPoint.x = 10;
        mPoint.y =  mDisplayHeight / 2;
    }

    /**
     * set next parameter of expload animation
     *
     * @return true: animation has next frame, false: otherwise
     */
    public boolean goNextToExplode() {

        if(mPaint.getAlpha() + ALPHA_APPEARED_SPEED < ALPHA_APPEARED) {
            int alpha = mPaint.getAlpha() + ALPHA_APPEARED_SPEED;
            mPaint.setAlpha(alpha);
            return true;
        } else {
            mPaint.setAlpha(ALPHA_APPEARED);
            return false;
        }
    }

    /**
     * set next parameter of fadeout animation
     *
     * @return true: animation has next frame, false: otherwise
     */
    public boolean goNextToFadeout() {
        if(mPaint.getAlpha()+ ALPHA_DISAPPEARED_SPEED > ALPHA_PREAPPEARED) {
            mPaint.setAlpha( mPaint.getAlpha()+ ALPHA_DISAPPEARED_SPEED );
            return true;
        } else {
            mPaint.setAlpha(ALPHA_PREAPPEARED);
            return false;
        }
    }

    /*
     * getter
     */

    public Paint getPaint() {
        return mPaint;
    }

    public Point getPoint(String msg) {
        float textWidth = mPaint.measureText(msg);
        mPoint.x = mDisplayWidth / 2 - (int)(textWidth/2);
        mPoint.y = mDisplayHeight / 2;
        return mPoint;
    }
}
