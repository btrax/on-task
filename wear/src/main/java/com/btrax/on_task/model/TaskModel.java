package com.btrax.on_task.model;

import android.content.Context;

import com.btrax.on_task.data.TaskData;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class TaskModel {

    private final static String TAG = TaskModel.class.getSimpleName();
    private final static String FILE_NAME = "pref_task_key";

    private Context mContext;

    public TaskModel(Context context) {
        mContext = context;
    }

    public TaskData get() {
        try {
            FileInputStream fis = mContext.openFileInput(FILE_NAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            TaskData data = (TaskData) ois.readObject();
            ois.close();
            return data;
        } catch (Exception e) {
            // TODO Handle Error
            e.printStackTrace();
            return new TaskData();
        }
    }

    public boolean save(TaskData task) {
        try {
            FileOutputStream fos = mContext.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(task);
            oos.close();
            return true;

        } catch (Exception e) {
            // TODO Handle Error
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete() {
        TaskData task = new TaskData();
        return save(task);
    }
}