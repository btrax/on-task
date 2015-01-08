package com.btrax.on_task.view;

import android.content.Context;
import android.graphics.Color;
import android.widget.FrameLayout;

import com.btrax.on_task.data.TaskData;
import com.btrax.on_task.util.MyLog;
import com.btrax.on_task.view.complete.CompleteView;
import com.btrax.on_task.view.expandable.ExpandableTaskView;
import com.btrax.on_task.view.semi.SemiCircleTaskView;

/**
 * task view added to window.
 */
public class TaskViewGroup extends FrameLayout {

    private final static String TAG = TaskViewGroup.class.getSimpleName();

    /*
     * child views
     */
    private CompleteView mCompleteView; // shown when a task is completed.
    private ExpandableTaskView mTaskExpandView; // shown when a task is expanded. include details of task.
    private SemiCircleTaskView mSemiCircleTaskView; // shown every time user look watch-face.

    public TaskViewGroup(Context context) {
        super(context);
        initChildViews();
    }

    public void reset() {
        removeAllViews();

        mSemiCircleTaskView.reset();
        mTaskExpandView.reset();
        mCompleteView.reset();
    }

    public void setTask(TaskData task) {
        mSemiCircleTaskView.setTask(task);
        mTaskExpandView.setTask(task);
    }

    public void start() {
        reset();
        if (mSemiCircleTaskView.getParent() == null) {
            addView(mSemiCircleTaskView);
            mSemiCircleTaskView.setPreApper();
            mSemiCircleTaskView.appear();
        }

        MyLog.i(TAG, "start task view");
    }

    /*
     * define own event listener
     */

    public interface TaskViewEventListener {
        /**
         * called when semi circle is taped.
         */
        void onTaped();

        /**
         * called when semi circle is expanded and the view cover whole window.
         */
        void onExpanded();

        /**
         * called when task is completed
         */
        void onCompleted();
    }

    private TaskViewEventListener mTaskViewEventListener;

    public void setTaskViewEventListener(TaskViewEventListener taskViewEventListener) {
        mTaskViewEventListener = taskViewEventListener;
    }

    /*
     * imple childviews event listener
     */

    public SemiCircleTaskView.EventListener mSemiCircleEventListener = new SemiCircleTaskView.EventListener() {

        @Override
        public void onTaped() {
            mTaskViewEventListener.onTaped();
        }

        @Override
        public void onExpand() {
            if (mTaskExpandView.getParent() == null) {
                addView(mTaskExpandView);
                mTaskExpandView.reset();
                mTaskExpandView.expand();

                mTaskViewEventListener.onExpanded();
            }
        }

        @Override
        public void onAppeared() {

        }

        @Override
        public void onVanish() {
            if (mCompleteView.getParent() == null) {
                addView(mCompleteView);
                mCompleteView.explode();
            }
        }
    };


    private ExpandableTaskView.EventListener mEventListener = new ExpandableTaskView.EventListener() {
        @Override
        public void onExpanded() {
        }

        @Override
        public void onShrinked() {
            // switch view from expand view to semi circle view to allow home screen to detect user's touch event.

            if (mSemiCircleTaskView.getParent() == null) {
                addView(mSemiCircleTaskView);
                mSemiCircleTaskView.setPreApper();
                mSemiCircleTaskView.setAppeared();
            }

            if (mTaskExpandView.getParent() != null) {
                removeView(mTaskExpandView);
                mTaskExpandView.reset();
            }
        }
    };

    private CompleteView.EventListener mCompleteViewEventListener = new CompleteView.EventListener() {
                @Override
                public void onExPanded() {

                }

                @Override
                public void onFadeOuted() {
                    mTaskViewEventListener.onCompleted();
                }
            };

    /*
     * init child views.
     */

    private void initChildViews() {
        initTaskCompleteView();
        initTaskExpandView();
        initSemiCircleTaskView();
    }

    private void initTaskExpandView() {
        mTaskExpandView = new ExpandableTaskView(getContext());
        mTaskExpandView.setBackgroundColor(Color.TRANSPARENT);
        mTaskExpandView.setEventListener(mEventListener);
    }

    private void initTaskCompleteView() {
        mCompleteView = new CompleteView(getContext());
        mCompleteView.setBackgroundColor(Color.TRANSPARENT);
        mCompleteView.setEventListener(mCompleteViewEventListener);
    }

    private void initSemiCircleTaskView() {
        mSemiCircleTaskView = new SemiCircleTaskView(getContext());
        mSemiCircleTaskView.setPreApper();
        mSemiCircleTaskView.setBackgroundColor(Color.TRANSPARENT);
        mSemiCircleTaskView.setEventListener(mSemiCircleEventListener);
    }
}
