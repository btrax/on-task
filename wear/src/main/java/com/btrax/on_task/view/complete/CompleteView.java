package com.btrax.on_task.view.complete;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;

import com.btrax.on_task.R;
import com.btrax.on_task.util.MyLog;
import com.btrax.on_task.view.TaskViewBase;

/**
 * used when task is completed.
 * first, circle getting bigger(explode), then it fade-out.
 */
public class CompleteView extends TaskViewBase {

    private final static String TAG = CompleteView.class.getSimpleName();
    private final static int INTERVAL_ANIMATION_MS = 1000;

    private Handler mHandler;
    private CircleParamsManager mCircleParamsManager;
    private TaskParamsManager mTaskParamsManager;

    public CompleteView(Context context) {
        super(context);
        init();
    }

    public CompleteView(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }

    private void init() {
        mHandler = new Handler();
        int completeColor = getResources().getColor(R.color.complete_background);
        mCircleParamsManager = new CircleParamsManager(completeColor);
        mTaskParamsManager = new TaskParamsManager();

        resetParams();
    }

    public void reset() {
        resetParams();
        invalidate();
    }

    private void resetParams() {
        mCircleParamsManager.reset();
        mTaskParamsManager.reset();
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawCopleteCircle(canvas);
        drawCompleteMsg(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        // match parent
        setMeasuredDimension(width, height);
        mCircleParamsManager.setDisplaySize(width, height);
        mTaskParamsManager.setDisplaySize(width, height);
    }

    private void drawCopleteCircle(Canvas canvas) {
        MyLog.d(TAG,
                "onDraw. complete circle" +
                        "x:" + mCircleParamsManager.getPoint().x +
                        ", y:" + mCircleParamsManager.getPoint().y +
                        ", r:" + mCircleParamsManager.getRadius() +
                        ", alpha:" + mCircleParamsManager.getPaint().getAlpha() +
                        ", color:" + mCircleParamsManager.getPaint().getColor());

        canvas.drawCircle(
                mCircleParamsManager.getPoint().x,
                mCircleParamsManager.getPoint().y,
                mCircleParamsManager.getRadius(),
                mCircleParamsManager.getPaint()
        );
    }

    private void drawCompleteMsg(Canvas canvas) {
        String completeMsg = getResources().getString(R.string.complete_message);

        MyLog.d(TAG,
                "onDraw.complete msg " +
                        "x:" + mTaskParamsManager.getPoint(completeMsg).x +
                        ", y:" + mTaskParamsManager.getPoint(completeMsg).y +
                        ", alpha:" + mTaskParamsManager.getPaint().getAlpha() +
                        ", color:" + mTaskParamsManager.getPaint().getColor());
        canvas.drawText(
                completeMsg,
                mTaskParamsManager.getPoint(completeMsg).x,
                mTaskParamsManager.getPoint(completeMsg).y,
                mTaskParamsManager.getPaint()
        );
    }



    /*
     * animation
     */

    public void explode() {

        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                boolean isCircleHasNextFrame = mCircleParamsManager.goNextToExplode();
                boolean isTextHasNextFrame = mTaskParamsManager.goNextToExplode();

                if (isCircleHasNextFrame || isTextHasNextFrame) {
                    invalidate();

                    // call recursively
                    explode();

                    // animation ended
                } else {
                    invalidate();
                    mEventListener.onExPanded();

                    // call next animation, fadeout
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fadeOut();
                        }
                    }, INTERVAL_ANIMATION_MS);
                }
            }

        }, FLAME_RATE_MILLIS);
    }

    public void fadeOut() {

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isCircleHasNextFrame = mCircleParamsManager.goNextToFadeOut();
                boolean isTextHasNextFrame = mTaskParamsManager.goNextToFadeout();

                if (isCircleHasNextFrame || isTextHasNextFrame) {

                    invalidate();

                    // call recursively
                    fadeOut();

                    // animation ended
                } else {
                    invalidate();
                    mEventListener.onFadeOuted();
                }
            }
        }, FLAME_RATE_MILLIS);

    }

    /*
     * define event listener
     */

    public interface EventListener {
        /**
         * circle expanded and cover whole of window
         */
        void onExPanded();

        /**
         * circle fadeouted
         */
        void onFadeOuted();
    }

    private EventListener mEventListener;

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;

    }
}
