package com.btrax.on_task.view.semi;

import android.graphics.Paint;
import android.graphics.Point;

/**
 * manage parameter of circle of semicircle view and expandable view
 */
public class CircleParamsManager {

    public final static int CIRCLE_MARGIN_R = -20;

    private final static int PRE_APPEAR_ALPHA = 140;
    private final static int APPEAR_SPEED_X = 25;
    private final static int APPEAR_SPEED_Y = 0;
    private final static int DISAPPEAR_SPEED_X = -16;
    private final static int DISAPPEAR_SPEED_Y = 0;
    private final static int EXPAND_SPEED_X = 20;
    private final static int EXPAND_SPEED_Y = 0;
    private final static int EXPAND_SPEED_R = 12;
    private final static int SHRINK_SPEED_X = -20;
    private final static int SHRINK_SPEED_Y = 0;
    private final static int SHRINK_SPEED_R = -12;
    private final static int APPEAR_ALPHA = 140;

    private Point mPoint = new Point();
    private Paint mPaint = new Paint();
    private int mRadius = -1;
    private int mDisplayWidth = -1;
    private int mDisplayHeight = -1;
    private int mColor = -1;


    public CircleParamsManager(int color) {
        mColor = color;
        reset();
    }

    public void reset() {
        mPoint = getPreAppearPoint();
        mPaint.setColor(mColor);
        mPaint.setAlpha(PRE_APPEAR_ALPHA);
        mPaint.setAntiAlias(true);

        mRadius = mDisplayWidth / 2;
    }

    public void setAppeared() {
        mPaint.setAlpha(APPEAR_ALPHA);
        mPaint.setAntiAlias(true);

        mRadius = getAppearedRadius();
        mPoint = getAppearedPoint();
    }

    public void setPreappeared() {
        mPoint = getPreAppearPoint();
    }

    public void setDisplaySize(int width, int height) {
        mDisplayWidth = width;
        mDisplayHeight = height;
        reset();
    }

    /*
     * animations
     */

    /**
     * set next parameter of appear animation
     *
     * @return true: animation has next frame, false: otherwise
     */
    public boolean goNextToAppeared() {
        if (getAppearedPoint().x - getPoint().x > APPEAR_SPEED_X) {
            mPoint.x += APPEAR_SPEED_X;
            mPoint.y += APPEAR_SPEED_Y;
            return true;
        } else {
            mPoint = getAppearedPoint();
            return false;
        }
    }

    /**
     * set next parameter of disapper animation
     *
     * @return true: animation has next frame, false: otherwise
     */
    public boolean goNextToPreappeared() {
        if (getPreAppearPoint().x - getPoint().x < DISAPPEAR_SPEED_X) {
            mPoint.x += DISAPPEAR_SPEED_X;
            mPoint.y += DISAPPEAR_SPEED_Y;
            return true;
        } else {
            mPoint = getPreAppearPoint();
            return false;
        }
    }

    /**
     * set next parameter of expand animation
     *
     * @return true: animation has next frame, false: otherwise
     */
    public boolean goNextToExpanded() {
        if (getExpandedPoint().x - getPoint().x > EXPAND_SPEED_X) {
            mPoint.x += EXPAND_SPEED_X;
            mPoint.y += EXPAND_SPEED_Y;
            mRadius += EXPAND_SPEED_R;
            return true;
        } else {
            mPoint = getExpandedPoint();
            return false;
        }
    }

    /**
     * set next parameter of shrink animation
     *
     * @return true: animation has next frame, false: otherwise
     */
    public boolean goNextToShrinked() {
        if (getAppearedPoint().x - getPoint().x < SHRINK_SPEED_X) {
            mPoint.x += SHRINK_SPEED_X;
            mPoint.y += SHRINK_SPEED_Y;
            mRadius += SHRINK_SPEED_R;
            return true;
        } else {
            mPoint = getAppearedPoint();
            return false;
        }
    }

    /*
     * getters
     */

    public Point getPreAppearPoint() {
        Point tmp = new Point();
        tmp.x = -mDisplayWidth / 2;
        tmp.y = mDisplayHeight / 2;
        return tmp;
    }

    public Point getExpandedPoint() {
        Point tmp = new Point();
        tmp.x = mDisplayWidth / 2;
        tmp.y = mDisplayHeight / 2;
        return tmp;
    }

    public Point getAppearedPoint() {
        Point tmp = new Point();
        tmp.x = CIRCLE_MARGIN_R;
        tmp.y = mDisplayHeight / 2;
        return tmp;
    }

    public int getAppearedRadius() {
        return mDisplayWidth / 2;
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
