package com.xyoye.danmuxposed.bean;

import java.io.Serializable;

/**
 * Created by xyy on 2018-03-21 上午 9:23
 */

public class Event implements Serializable{
    public static final String EVENT_MX_START = "mxStart";
    public static final String EVENT_START = "start";
    public static final String EVENT_PAUSE = "pause";
    public static final String EVENT_DURATION = "duration";
    public static final String EVENT_PROGRESS = "progress";
    public static final String EVENT_SPEED = "speed";
    public static final String EVENT_TITLE = "title";

    private String Key;
    private Object value;

    public Event(String key, Object value) {
        Key = key;
        this.value = value;
    }

    public Event(String key) {
        Key = key;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
