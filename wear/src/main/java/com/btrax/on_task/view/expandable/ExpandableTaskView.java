package com.btrax.on_task.view.expandable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.btrax.on_task.R;
import com.btrax.on_task.util.MyLog;
import com.btrax.on_task.view.TaskViewBase;
import com.btrax.on_task.view.semi.CircleParamsManager;


/**
 * used when users touch semicircle and expand it.
 * This view can expand and shrink. But this needs whole space of display.
 */
public class ExpandableTaskView extends TaskViewBase {

    private final static String TAG = ExpandableTaskView.class.getSimpleName();
    private final static int AUTO_SHRINK_TIME_MS = 5000;
    private final static int SWIPE_SHRINK_MOVE_X = -50;

    private CircleParamsManager mCircleParamsManager;
    private ExpandableTaskParamsManager mTaskParamsManager;
    private boolean mIsShrinking = false;
    private Handler mHandler;

    public ExpandableTaskView(Context context) {
        super(context);
        init();
    }

    public ExpandableTaskView(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }

    public void init() {

        mHandler = new Handler();
        mCircleParamsManager = new CircleParamsManager(getResources().getColor(R.color.task_color_red));
        mTaskParamsManager = new ExpandableTaskParamsManager();

        resetParams();
        setOnTouchListenerInView();
    }

    private void setOnTouchListenerInView() {
        setOnTouchListener(new OnTouchListener() {

            private Point mStartPoint = new Point();

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MyLog.i(TAG, "onTouch x:" + event.getRawX() + ", y;" + event.getRawY());

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        MyLog.i(TAG, "action down");
                        mStartPoint.x = (int) event.getRawX();
                        mStartPoint.y = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        MyLog.i(TAG, "action move");

                        if (mStartPoint.x + SWIPE_SHRINK_MOVE_X > event.getRawX() && mIsShrinking == false) {
                            shrink();
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        MyLog.i(TAG, "action up");

                        break;
                    default:
                        break;

                }
                return true;
            }
        });
    }

    public void resetParams() {
        mCircleParamsManager.setAppeared();
        mTaskParamsManager.reset();
    }

    public void reset() {
        resetParams();
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawCircle(canvas);
        drawTaskInfo(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width, height);
        mTaskParamsManager.setParentSize(width, height);
        mCircleParamsManager.setDisplaySize(width, height);
    }

    private void drawCircle(Canvas canvas) {
        MyLog.d(TAG,
                "onDraw. " +
                        "x:" + mCircleParamsManager.getPoint().x +
                        ", y:" + mCircleParamsManager.getPoint().y +
                        ", r:" + mCircleParamsManager.getRadius() +
                        ", alpha:" + mCircleParamsManager.getPaint().getAlpha() +
                        ", color:" + mCircleParamsManager.getPaint().getColor()
        );

        canvas.drawCircle(
                mCircleParamsManager.getPoint().x,
                mCircleParamsManager.getPoint().y,
                mCircleParamsManager.getRadius(),
                mCircleParamsManager.getPaint()
        );
    }

    private void drawTaskInfo(Canvas canvas) {
        int marginLeft = 15;
        int marginRight = 15;
        int centerX = getWidth() / 2;

        String taskName = getTask().name;
        float taskNameWidth = mTaskParamsManager.getNamePaint().measureText(taskName);
        // TODO string is longer than its space
        if (taskNameWidth > getWidth() - marginLeft - marginRight) {
        }

        canvas.drawText(
                taskName,
                centerX - taskNameWidth / 2,
                130,
                mTaskParamsManager.getNamePaint()
        );

        String place = getTask().place;
        float placeWidth = mTaskParamsManager.getDescPaint().measureText(place);
        canvas.drawText(
                place,
                centerX - placeWidth / 2,
                170,
                mTaskParamsManager.getDescPaint()
        );

        String due = "";
        if (getTask().due.length() > 0) {
            due = getResources().getString(R.string.task_place_prefix) + getTask().due;
        }
        float dueWidth = mTaskParamsManager.getDescPaint().measureText(due);

        MyLog.d(TAG, "alpha:" + mTaskParamsManager.getDescPaint().getAlpha());

        canvas.drawText(
                due,
                centerX - dueWidth / 2,
                200,
                mTaskParamsManager.getDescPaint()
        );
    }

    /*
     * animation
     */


    public void expand() {

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isCircleHasNextFrame = mCircleParamsManager.goNextToExpanded();
                boolean isTextHasNextFrame = mTaskParamsManager.goNextToExpanded();

                if (isCircleHasNextFrame || isTextHasNextFrame) {
                    invalidate();

                    // call recursive
                    expand();

                    // animation end
                } else {
                    mEventListener.onExpanded();

                    // auto shrink
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            shrink();
                        }
                    }, AUTO_SHRINK_TIME_MS);
                }
            }
        }, FLAME_RATE_MILLIS);
    }

    public void shrink() {

        mIsShrinking = true;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isCircleHasNextFrame = mCircleParamsManager.goNextToShrinked();
                boolean isTextHasNextFrame =  mTaskParamsManager.goNextToShrinked();

                if (isCircleHasNextFrame || isTextHasNextFrame) {

                    invalidate();

                    // call recursively
                    shrink();

                    // animation end
                } else {
                    mEventListener.onShrinked();
                    mIsShrinking = false;
                }
            }
        }, FLAME_RATE_MILLIS);
    }


    /*
     * define event listener
     */

    public interface EventListener {
        /**
         * on circle expanded and covered whole display
         */
        void onExpanded();

        /**
         * on circle shrinked to semicircle
         */
        void onShrinked();
    }

    private EventListener mEventListener;

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

}