package com.example.androidtvremote.logic;

import java.util.Calendar;
import java.util.Objects;

public class Task {
    private String actionName;
    private Calendar time;
    private byte actionId;
    private String id;
    public Task(String actionName, Calendar time, byte actionId, String id){
        this.actionName = actionName;
        this.time = time;
        this.actionId = actionId;
        this.id = id;
    }

    public String getActionName() {
        return actionName;
    }

    public Calendar getTime() {
        return time;
    }

    public byte getActionId() {
        return actionId;
    }

    public void setActionId(byte actionId) {
        this.actionId = actionId;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return actionId == task.actionId && actionName.equals(task.actionName) && time.equals(task.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionName, time, actionId);
    }
}
