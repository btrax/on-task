package com.btrax.on_task.data;

import java.io.Serializable;
import java.util.Calendar;

public class TaskData implements Serializable {
    public String name = "";
    public String place = "";
    public String due = "";
    public String desc = "";
    public Calendar start;
    public Calendar end;

    public boolean isEmpty() {
        return (name == null || name.length() == 0);
    }
}