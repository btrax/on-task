package com.btrax.on_task.view.semi;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;

import com.btrax.on_task.data.TaskData;

/**
 * manage parameter of text of semicircle view and expandable view
 */
public class TaskParamsManager {

    private final static int TEXT_SIZE = 22;
    private final static int TEXT_COLOR = Color.WHITE;
    private final static int ALPHA_APPEARED = 255;
    private final static int ALPHA_PREAPPEARED = 0;
    private final static int ALPHA_APPEARED_SPEED = 10;
    private final static int ALPHA_DISAPPEARED_SPEED = -10;

    private int mDisplayWidth = -1;
    private int mDisplayHeight = -1;
    private Paint mPaint = new Paint();
    private Point mPoint = new Point();

    public TaskParamsManager() {
    }

    public void reset() {
        mPaint.setColor(TEXT_COLOR);
        mPaint.setTextSize(TEXT_SIZE);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint.setAntiAlias(true);

        mPoint.x = 10; // TODO magic number
        mPoint.y = mDisplayHeight / 2;

    }

    public void setParentSize(int width, int height) {
        mDisplayWidth = width;
        mDisplayHeight = height;
        reset();
    }

    /**
     * set next parameter of appear animation
     *
     * @return true: animation has next frame, false: otherwise
     */
    public boolean goNextToAppeared() {
        int alpha = mPaint.getAlpha() + ALPHA_APPEARED_SPEED;
        if (alpha < ALPHA_APPEARED) {
            mPaint.setAlpha(alpha);
            return true;

        } else {
            mPaint.setAlpha(ALPHA_APPEARED);
            return false;
        }
    }

    /**
     * set next parameter of disappear nimation
     *
     * @return true: animation has next frame, false: otherwise
     */
    public boolean goNextToPreappeared() {

        int alpha = mPaint.getAlpha() + ALPHA_DISAPPEARED_SPEED;
        if (alpha > ALPHA_PREAPPEARED) {
            mPaint.setAlpha(alpha);
            return true;
        } else {
            mPaint.setAlpha(ALPHA_PREAPPEARED);
            return false;
        }
    }

    public void setAppeared() {
        mPaint.setAlpha(ALPHA_APPEARED);
    }

    public void setPreappeared() {
        mPaint.setAlpha(ALPHA_PREAPPEARED);
    }

    public Paint getPaint() {
        return mPaint;
    }

    public Point getPoint() {
        return mPoint;
    }

}
