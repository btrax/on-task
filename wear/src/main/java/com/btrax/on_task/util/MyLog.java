package com.btrax.on_task.util;

import android.util.Log;

import com.btrax.on_task.config.AppConsts;

public class MyLog {

    public static void w(String tag, String msg) {
        if (AppConsts.isDebug()) Log.w(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (AppConsts.isDebug()) Log.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (AppConsts.isDebug()) Log.i(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (AppConsts.isDebug()) Log.e(tag, msg);
    }
}

