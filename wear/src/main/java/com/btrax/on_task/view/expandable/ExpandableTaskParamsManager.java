package com.btrax.on_task.view.expandable;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

/**
 * manage parameter of expandable task view's text
 */
public class ExpandableTaskParamsManager {

    private final static int NAME_APPER_ALPHA = 0;
    private final static int NAME_EXPANDED_ALPHA = 255;
    private final static int NAME_EXPAND_ALPHA_SPEED = 20;
    private final static int NAME_SHRINK_ALPHA_SPEED = -20;
    private final static int NAME_TEXT_SIZE = 30;
    private final static int DESC_TEXT_SIZE = 22;
    private final static int NAME_TEXT_COLOR = Color.WHITE;
    private final static int DESC_TEXT_COLOR = Color.WHITE;

    private Paint mNamePaint = new Paint();
    private Paint mDescPaint = new Paint();
    private Point mNamePoinnt = new Point();
    private Point mDescPoint = new Point();
    private int mDisplayWidth = -1;
    private int mDisplayHeight = -1;
    private int mAlpha = -1;

    public void setParentSize(int width, int height) {
        mDisplayWidth = width;
        mDisplayHeight = height;
        reset();
    }

    public void reset() {
        mNamePaint.setTextSize(NAME_TEXT_SIZE);
        mNamePaint.setColor(NAME_TEXT_COLOR);
        mNamePaint.setAlpha(NAME_APPER_ALPHA);
        mNamePaint.setAntiAlias(true);

        mDescPaint.setTextSize(DESC_TEXT_SIZE);
        mDescPaint.setColor(DESC_TEXT_COLOR);
        mDescPaint.setAlpha(NAME_APPER_ALPHA);
        mDescPaint.setAntiAlias(true);
    }

    /**
     * set next parameter of Expand animation
     *
     * @return true: animation has next frame, false: otherwise
     */
    public boolean goNextToExpanded() {
        int alpha = mNamePaint.getAlpha() + NAME_EXPAND_ALPHA_SPEED;

        if (alpha < NAME_EXPANDED_ALPHA) {
            mNamePaint.setAlpha(alpha);
            mDescPaint.setAlpha(alpha);
            return true;

        } else {
            mNamePaint.setAlpha(NAME_EXPANDED_ALPHA);
            mDescPaint.setAlpha(NAME_EXPANDED_ALPHA);
            return false;
        }
    }

    /**
     * set next parameter of shrink animation
     *
     * @return true: animation has next frame, false: otherwise
     */
    public boolean goNextToShrinked() {

        int alpha =  mNamePaint.getAlpha() + NAME_SHRINK_ALPHA_SPEED;
        if (alpha > NAME_APPER_ALPHA) {
            mNamePaint.setAlpha(alpha);
            mDescPaint.setAlpha(alpha);
            return true;
        } else {
            mNamePaint.setAlpha(NAME_APPER_ALPHA);
            mDescPaint.setAlpha(NAME_APPER_ALPHA);
            return false;
        }
    }


    /*
     * getter
     */

    public Paint getNamePaint() {
        return mNamePaint;
    }

    public Paint getDescPaint() {
        return mDescPaint;
    }

    public Point getNamePoinntPoint() {
        return mNamePoinnt;
    }

    public Point getDescPoinntPoint() {
        return mDescPoint;
    }

}
