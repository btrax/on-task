package com.btrax.on_task.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

import com.btrax.on_task.data.TaskData;

public class TaskViewBase extends SurfaceView {

    private TaskData mTask;
    protected final static int FLAME_RATE_MILLIS = 50;

    public void setTask(TaskData task) {
        mTask = task;
    }

    public TaskData getTask() {
        return mTask;
    }

    public TaskViewBase(Context context) {
        super(context);
    }

    public TaskViewBase(Context context, AttributeSet attr) {
        super(context, attr);
    }
}
