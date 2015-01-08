package com.btrax.on_task.window;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;

import com.btrax.on_task.util.MyLog;

public abstract class WindowViewManagerBase {

    private final static String TAG = WindowViewManagerBase.class.getSimpleName();

    private WindowManager mWindowManager;
    private View mView = null;

    public WindowViewManagerBase(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public void add(View view) {
        if (mView != null) {
            MyLog.w(TAG, "you tried to add view already have a parent.");
            return;
        }

        mView = view;
        mWindowManager.addView(mView, createLayoutParams());
    }

    public void remove() {
        if (mView == null) {
            return;
        } else if (mView.getParent() == null) {
            MyLog.w(TAG, "you tried to remove view does not have a parent.");
            return;
        }

        mWindowManager.removeView(mView);
        mView = null;
    }

    abstract protected WindowManager.LayoutParams createLayoutParams();
}