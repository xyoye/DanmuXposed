package com.xyoye.danmuxposed.listener;

/**
 * Created by xyy on 2018-03-20 下午 4:05
 */


public interface MXListener {
    void appStart();

    void start();

    void pause();

    void duration(int duration);

    void seekTo(int to);

    void setSpeed(int speed);

    void setTitle(String title);
}
