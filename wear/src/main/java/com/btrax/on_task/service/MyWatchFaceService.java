package com.btrax.on_task.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
import android.view.SurfaceHolder;

import com.btrax.on_task.R;
import com.btrax.on_task.analytics.SendEventManager;
import com.btrax.on_task.config.AppConsts;
import com.btrax.on_task.data.TaskData;
import com.btrax.on_task.model.TaskModel;
import com.btrax.on_task.util.MyLog;
import com.btrax.on_task.view.TaskViewGroup;
import com.btrax.on_task.window.WindowTaskViewManager;
import com.btrax.on_task.activity.InputActivity;
import com.crittercism.app.Crittercism;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Service for watch face
 */
public class MyWatchFaceService extends CanvasWatchFaceService {

    private final static String TAG = MyWatchFaceService.class.getSimpleName();
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    /**
     * provide your watch face implementation
     */
    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private void startInputActivity() {
        Intent i = new Intent(getApplicationContext(), InputActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    /**
     * implement service callback methods
     */
    private class Engine extends CanvasWatchFaceService.Engine {
        private final static float HOUR_HAND_LENGTH = 80.0f;
        private final static float MINUTE_HAND_LENGTH = 120.0f;
        private final static float SEC_HAND_LENGTH = 120.0f;
        private static final int REAPPEAR_INTERVAL_AFTER_COMPLEATE_MS = 3000;
        private static final int MSG_UPDATE_TIME = 0;

        private boolean mLowBitAmbient = false;
        private boolean mMute = false;
        private Paint mHourPaint;
        private Paint mMinutePaint;
        private Paint mSecondPaint;
        private Bitmap mBackgroundBitmap;
        private Bitmap mBackgroundScaledBitmap;
        private Paint mDotPaint;
        private Handler mHandler;
        private Time mTime;
        private TaskModel mTaskModel;
        private TaskViewGroup mTaskViewGroup;
        private WindowTaskViewManager mWindowTaskViewManager;
        private boolean mRegisteredTimeZoneReceiver = false;
        private SendEventManager mSendEventManager;

        /**
         * initialize
         */
        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            MyLog.d(TAG, "onCreate");

            setWatchFaceStyle(new WatchFaceStyle.Builder(MyWatchFaceService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());

            mSendEventManager = new SendEventManager(getApplicationContext());
            mSendEventManager.sendWatchSet();
            Crittercism.initialize(getApplicationContext(), AppConsts.CRITTERCISM_APP_ID);

            MyLog.d(TAG, "MODEL NAME:" + Build.MODEL);

            mTaskModel = new TaskModel(MyWatchFaceService.this);
            mHandler = new Handler();
            mHourPaint = new Paint();
            mMinutePaint = new Paint();
            mSecondPaint = new Paint();
            mDotPaint = new Paint();
            mTime = new Time();
            mWindowTaskViewManager = new WindowTaskViewManager(MyWatchFaceService.this);

            setupTaskGroupView();
            setupWatchFaceParams();
        }

        /**
         * initialize taskGroupView
         */
        private void setupTaskGroupView() {
            mTaskViewGroup = new TaskViewGroup(MyWatchFaceService.this);
            mTaskViewGroup.setBackgroundColor(Color.TRANSPARENT);
            mTaskViewGroup.setTaskViewEventListener(new TaskViewGroup.TaskViewEventListener() {
                @Override
                public void onTaped() {
                    if (mTaskModel.get().isEmpty()) {
                        startInputActivity();
                    }
                    MyLog.i(TAG, "onTaped");
                }

                @Override
                public void onExpanded() {
                    mSendEventManager.sendExpandTask(mTaskModel.get().name);
                }

                @Override
                public void onCompleted() {
                    stopTaskViewGroup();

                    mSendEventManager.sendCompleteTask(mTaskModel.get().name);
                    mTaskModel.delete();

                    mTaskViewGroup.reset();
                    mWindowTaskViewManager.add(mTaskViewGroup);

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            TaskData task = mTaskModel.get();
                            if (task.isEmpty()) {
                                task.name = getResources().getString(R.string.task_name_no_task);
                            }
                            mTaskViewGroup.setTask(task);
                            mTaskViewGroup.start();
                        }
                    }, REAPPEAR_INTERVAL_AFTER_COMPLEATE_MS);
                }
            });
        }

        private void startTaskViewGroup() {
            mWindowTaskViewManager.add(mTaskViewGroup);
            TaskData task = mTaskModel.get();
            if (task.isEmpty()) {
                task.name = getResources().getString(R.string.task_name_no_task);
            }
            mTaskViewGroup.setTask(task);
            mTaskViewGroup.start();
        }

        private void stopTaskViewGroup() {
            mWindowTaskViewManager.remove();
        }

        /**
         * the time changed. called every minute.
         */
        @Override
        public void onTimeTick() {
            super.onTimeTick();
            MyLog.d(TAG, "onTimeTick: ambient = " + isInAmbientMode());
            invalidate();

        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
            MyLog.d(TAG, "onPropertiesChanged: low-bit ambient = " + mLowBitAmbient);
        }

        @Override
        public void onInterruptionFilterChanged(int interruptionFilter) {
            super.onInterruptionFilterChanged(interruptionFilter);
            boolean inMuteMode = (interruptionFilter == MyWatchFaceService.INTERRUPTION_FILTER_NONE);

            if (mMute != inMuteMode) {
                mMute = inMuteMode;
                mHourPaint.setAlpha(inMuteMode ? 100 : 255);
                mMinutePaint.setAlpha(inMuteMode ? 100 : 255);
                mSecondPaint.setAlpha(inMuteMode ? 80 : 255);
                mDotPaint.setAlpha(inMuteMode ? 80 : 255);
                invalidate();
            }
        }

        /**
         * called when ambient mode changed.
         */
        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);

            if (inAmbientMode) {
                stopTaskViewGroup();
            } else {
                startTaskViewGroup();
            }

            if (mLowBitAmbient) {
                boolean antiAlias = !inAmbientMode;
                mHourPaint.setAntiAlias(antiAlias);
                mMinutePaint.setAntiAlias(antiAlias);
                mSecondPaint.setAntiAlias(antiAlias);
                mDotPaint.setAntiAlias(antiAlias);
            }
            invalidate();

            // Whether the timer should be running depends on whether we're in ambient mode (as well
            // as whether we're visible), so we may need to start or stop the timer.
            updateTimer();
        }

        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        /**
         * the watch face became visible or invisible
         */
        @Override
        public void onVisibilityChanged(boolean visible) {
            MyLog.d(TAG, ":onVisibilityChanged " + visible);
            if (visible) {
                startTaskViewGroup();

                registerReceiver();
                // Update time zone in case it changed while we weren't visible.
                mTime.clear(TimeZone.getDefault().getID());
                mTime.setToNow();
            } else {
                stopTaskViewGroup();
                unregisterReceiver();
            }
            updateTimer();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            mSendEventManager.sendWatchUnset();
            stopTaskViewGroup();
            MyLog.d(TAG, ":onDestroy");
        }

        /**
         * draw watch face
         */
        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            mTime.setToNow();

            drawBackground(canvas, bounds);
            drawHands(canvas, bounds);
            drawDot(canvas, bounds);
        }

        private void drawHands(Canvas canvas, Rect bounds) {
            int width = bounds.width();
            int height = bounds.height();
            float centerX = width / 2f;
            float centerY = height / 2f;

            // draw minute hand
            float minRot = mTime.minute / 30f * (float) Math.PI;
            if (isInAmbientMode()) {
                mMinutePaint.setARGB(255, 200, 200, 200);
            } else {
                mMinutePaint.setARGB(255, 255, 0, 0);
            }
            float minX = (float) Math.sin(minRot) * MINUTE_HAND_LENGTH;
            float minY = (float) -Math.cos(minRot) * MINUTE_HAND_LENGTH;
            canvas.drawLine(centerX, centerY, centerX + minX, centerY + minY, mMinutePaint);

            // draw hour hand
            float hrRot = ((mTime.hour + (mTime.minute / 60f)) / 6f) * (float) Math.PI;
            if (isInAmbientMode()) {
                mHourPaint.setARGB(255, 200, 200, 200);
            } else {
                mHourPaint.setARGB(255, 255, 255, 255);
            }
            float hrX = (float) Math.sin(hrRot) * HOUR_HAND_LENGTH;
            float hrY = (float) -Math.cos(hrRot) * HOUR_HAND_LENGTH;
            canvas.drawLine(centerX, centerY, centerX + hrX, centerY + hrY, mHourPaint);

            // draw second
            float secRot = mTime.second / 30f * (float) Math.PI;
            if (!isInAmbientMode()) {
                float secX = (float) Math.sin(secRot) * SEC_HAND_LENGTH;
                float secY = (float) -Math.cos(secRot) * SEC_HAND_LENGTH;
                canvas.drawLine(centerX, centerY, centerX + secX, centerY + secY, mSecondPaint);
            }
        }

        private void drawBackground(Canvas canvas, Rect bounds) {
            // draw background
            Resources resources = MyWatchFaceService.this.getResources();
            int imgResId;
            imgResId = R.drawable.black_background;

            Drawable backgroundDrawable = resources.getDrawable(imgResId);
            mBackgroundBitmap = ((BitmapDrawable) backgroundDrawable).getBitmap();
            mBackgroundScaledBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap,
                    bounds.width(), bounds.height(), true /* filter */);
            canvas.drawBitmap(mBackgroundScaledBitmap, 0, 0, null);
        }

        private void drawDot(Canvas canvas, Rect bounds) {
            // draw dot
            int dotDia = 5;
            int margin = 20;
            canvas.drawCircle(margin, bounds.height() / 2, dotDia, mDotPaint);
            canvas.drawCircle(bounds.width() - margin, bounds.height() / 2, dotDia, mDotPaint);
            canvas.drawCircle(bounds.width() / 2, margin, dotDia, mDotPaint);
            canvas.drawCircle(bounds.width() / 2, bounds.height() - margin, dotDia, mDotPaint);

            canvas.drawCircle(bounds.width() / 2, bounds.height() / 2, dotDia, mDotPaint);
        }

        private void setupWatchFaceParams() {

            mHourPaint.setStrokeWidth(5f);
            mHourPaint.setAntiAlias(true);
            mHourPaint.setStrokeCap(Paint.Cap.ROUND);

            mMinutePaint.setStrokeWidth(3f);
            mMinutePaint.setAntiAlias(true);
            mMinutePaint.setStrokeCap(Paint.Cap.ROUND);

            mSecondPaint.setARGB(255, 255, 255, 255);
            mSecondPaint.setStrokeWidth(1f);
            mSecondPaint.setAntiAlias(true);
            mSecondPaint.setStrokeCap(Paint.Cap.ROUND);

            mDotPaint.setColor(Color.WHITE);
            mDotPaint.setAlpha(255);
            mDotPaint.setAntiAlias(true);
        }


        /*
         * for update watch face every second
         */

        final Handler mUpdateTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MSG_UPDATE_TIME:
                        invalidate();
                        if (shouldTimerBeRunning()) {
                            long timeMs = System.currentTimeMillis();
                            long delayMs = INTERACTIVE_UPDATE_RATE_MS
                                    - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                            mUpdateTimeHandler
                                    .sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
                        }
                        break;
                }
            }
        };

        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }


        /*
         * receiver for timezone
         */

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            MyWatchFaceService.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            MyWatchFaceService.this.unregisterReceiver(mTimeZoneReceiver);
        }

        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mTime.clear(intent.getStringExtra("time-zone"));
                mTime.setToNow();
            }
        };
    }
}
