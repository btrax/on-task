package com.btrax.on_task.window;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;

public class WindowTaskViewManager extends WindowViewManagerBase {

    public WindowTaskViewManager(Context context) {
        super(context);
    }

    @Override
    protected WindowManager.LayoutParams createLayoutParams() {

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | // put status bar on activity
                        // WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | // do not get touch event
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | // touch on except modal goes down layer
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN |
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR, // for moto360 bug
                PixelFormat.TRANSPARENT
        );
        params.gravity = Gravity.LEFT | Gravity.TOP;

        return params;
    }

}