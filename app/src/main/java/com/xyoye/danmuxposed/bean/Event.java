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
    public static final String EVENT_DANMU_SIZE = "danmu_size";
    public static final String EVENT_DANMU_SPEED = "danmu_speed";
    public static final String EVENT_DANMU_MOBILE = "danmu_mobile";
    public static final String EVENT_DANMU_TOP = "danmu_top";
    public static final String EVENT_DANMU_BUTTON = "danmu_button";
    public static final String EVENT_DANMU_SHIELD_ADD = "danmu_shield_add";
    public static final String EVENT_DANMU_SHIELD_REMOVE = "danmu_shield_remove";
    public static final String EVENT_DANMU_SHIELD_REMOVE_ALL = "danmu_shield_remove_all";

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
