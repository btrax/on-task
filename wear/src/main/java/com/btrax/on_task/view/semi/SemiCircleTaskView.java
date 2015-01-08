package com.btrax.on_task.view.semi;

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

/**
 * usually used. this does not use whole of parent view, but just needed space.
 */
public class SemiCircleTaskView extends TaskViewBase {

    private final static String TAG = SemiCircleTaskView.class.getSimpleName();
    private final static int SWIPE_RIGHT_MOVE_X = 30;
    private final static int SWIPE_LEFT_MOVE_X = -50;

    private Handler mHandler;
    private CircleParamsManager mCircleParamsManager;
    private TaskParamsManager mTaskParamsManager;

    public SemiCircleTaskView(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }

    public SemiCircleTaskView(Context context) {
        super(context);
        init();
    }

    public void init() {
        mCircleParamsManager = new CircleParamsManager(getResources().getColor(R.color.task_color_red));
        mTaskParamsManager = new TaskParamsManager();
        mHandler = new Handler();

        setOnTouchListenerInView();
        resetParams();
    }

    private void resetParams() {
        mCircleParamsManager.reset();
        mTaskParamsManager.reset();
    }

    private void setOnTouchListenerInView() {

        setOnTouchListener(new OnTouchListener() {

            private boolean mIsExpanding = false;
            private Point mStartPoint = new Point();

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                MyLog.i(TAG, "onTouch x:" + event.getRawX() + ", y;" + event.getRawY());


                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mEventListener.onTaped();
                        MyLog.i(TAG, "action down");
                        mStartPoint.x = (int) event.getRawX();
                        mStartPoint.y = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        MyLog.i(TAG, "action move");

                        if (mStartPoint.x + SWIPE_RIGHT_MOVE_X < event.getRawX()) {
                            if (mIsExpanding == false) {
                                mEventListener.onExpand();
                            }
                            mIsExpanding = true;

                        } else if (mStartPoint.x + SWIPE_LEFT_MOVE_X > event.getRawX()) {
                            disAppear();
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        MyLog.i(TAG, "action up");
                        mIsExpanding = false;

                        break;
                    default:
                        break;

                }

                return true;
            }
        });

    }

    @Override
    public void onDraw(Canvas canvas) {
        drawCircle(canvas);
        drawTaskName(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        mTaskParamsManager.setParentSize(width, height);
        mCircleParamsManager.setDisplaySize(width, height);

        // not use whole of parent view.
        setMeasuredDimension(mCircleParamsManager.getRadius() + CircleParamsManager.CIRCLE_MARGIN_R, height);
    }

    private void drawCircle(Canvas canvas) {
        MyLog.i(TAG,
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

    private void drawTaskName(Canvas canvas) {
        int centerX = getWidth() / 2;
        String name = getTask().name;
        float nameWidth = mTaskParamsManager.getPaint().measureText(name);

        float minMarginLeft = 10f;
        float minMarginRight = 10f;
        float startX = -1f;

        float textMaxWidth = getWidth() - minMarginLeft - minMarginRight;
        if (nameWidth > textMaxWidth) {
            startX = minMarginLeft;

            float charWidth = nameWidth / name.length();
            int textMaxCount = (int) Math.floor(textMaxWidth / charWidth);
            name = name.substring(0, textMaxCount - 2);
            name += getResources().getString(R.string.task_name_ellipsize);

        } else {
            startX = centerX - nameWidth / 2;
        }

        canvas.drawText(
                name,
                startX,
                mTaskParamsManager.getPoint().y,
                mTaskParamsManager.getPaint()
        );
    }

    /*
     * set view status
     */

    public void reset() {
        resetParams();
        invalidate();
    }

    public void setPreApper() {
        mCircleParamsManager.setPreappeared();
        mTaskParamsManager.setPreappeared();
        invalidate();
    }

    public void setAppeared() {
        mCircleParamsManager.setAppeared();
        mTaskParamsManager.setAppeared();
        invalidate();
    }

    /*
     * animation
     */

    public void disAppear() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isCircleHasNextFrame = mCircleParamsManager.goNextToPreappeared();
                boolean isTextHasNextFrame = mTaskParamsManager.goNextToPreappeared();

                if (isCircleHasNextFrame || isTextHasNextFrame) {

                    invalidate();
                    disAppear();

                    // animation ended
                } else {
                    invalidate();
                    mEventListener.onVanish();
                }
            }
        }, FLAME_RATE_MILLIS);
    }

    public void appear() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                boolean isCircleHasNextFrame = mCircleParamsManager.goNextToAppeared();
                boolean isTextHasNextFrame = mTaskParamsManager.goNextToAppeared();
                if (isCircleHasNextFrame || isTextHasNextFrame) {
                    invalidate();
                    appear();

                    // animation ended
                } else {
                    invalidate();
                    mEventListener.onAppeared();
                }
            }
        }, FLAME_RATE_MILLIS);
    }

    /*
     * define own event listener
     */
    public interface EventListener {
        void onTaped();

        void onExpand();

        void onAppeared();

        void onVanish();
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    private EventListener mEventListener;
}
