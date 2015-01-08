package com.btrax.on_task.view.complete;

import android.graphics.Paint;
import android.graphics.Point;

/**
 * manage parameter of circle
 */
class CircleParamsManager {

    private final static int INIT_ALPHA = 180;
    private final static int FADE_OUT_ALPHA = 0;
    private final static float ExpandedRadiusRatio = 1.5f;
    private final static int EXTEND_SPEED_R = 25;
    private final static int FADE_OUT_ALPHA_SPEED = -25;

    private Point mPoint = new Point();
    private Paint mPaint = new Paint();
    private int mRadius = -1;
    private int mDisplayWidth = -1;
    private int mDisplayHeight = -1;
    private int mColor = -1;

    public CircleParamsManager(int color) {
        mColor = color;
    }

    public void setDisplaySize(int width, int height) {
        mDisplayWidth = width;
        mDisplayHeight = height;
        reset();
    }

    private int getExpandedRadius() {
        return (int) (mDisplayWidth * ExpandedRadiusRatio);
    }

    public void reset() {
        mPoint = getInitPosition();
        mPaint.setColor(mColor);
        mPaint.setAlpha(INIT_ALPHA);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        mRadius = 0;
    }

    /**
     * set next parameter of Expload animation
     *
     * @return true: animation has next frame, false: otherwise
     */
    public boolean goNextToExplode() {
        if (mRadius + EXTEND_SPEED_R < getExpandedRadius()) {
            mRadius += EXTEND_SPEED_R;
            return true;
        } else {
            mRadius = getExpandedRadius();
            return false;
        }
    }

    /**
     * set next parameter of fadeout animation
     *
     * @return true: animation has next frame, false: otherwise
     */
    public boolean goNextToFadeOut() {

        if (mPaint.getAlpha() + FADE_OUT_ALPHA_SPEED > FADE_OUT_ALPHA) {
            mPaint.setAlpha(mPaint.getAlpha() + FADE_OUT_ALPHA_SPEED);
            return true;
        } else {
            mPaint.setAlpha(FADE_OUT_ALPHA);
            return false;
        }
    }

    /*
     * getter
     */
    public Point getInitPosition() {
        Point tmp = new Point();
        tmp.x = mDisplayWidth / 2;
        tmp.y = mDisplayHeight / 2;
        return tmp;
    }

    public Paint getPaint() {
        return mPaint;
    }

    public Point getPoint() {
        return mPoint;
    }

    public int getRadius() {
        return mRadius;
    }
}