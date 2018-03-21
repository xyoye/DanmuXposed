package com.xyoye.danmuxposed.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xyoye.danmuxposed.bean.Event;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by xyy on 2018-03-21 下午 2:58
 */


public class EventBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != intent) {
            String action = intent.getAction();
            assert action != null;
            switch (action) {
                case Event.EVENT_MX_START:
                    EventBus.getDefault().post(new Event(Event.EVENT_MX_START));
                    break;
                case Event.EVENT_START:
                    EventBus.getDefault().post(new Event(Event.EVENT_START));
                    break;
                case Event.EVENT_PAUSE:
                    EventBus.getDefault().post(new Event(Event.EVENT_PAUSE));
                    break;
                case Event.EVENT_SPEED:
                    int speed = intent.getIntExtra(Event.EVENT_SPEED,0);
                    EventBus.getDefault().post(new Event(Event.EVENT_SPEED,speed));
                    break;
                case Event.EVENT_DURATION:
                    int duration = intent.getIntExtra(Event.EVENT_DURATION,0);
                    EventBus.getDefault().post(new Event(Event.EVENT_SPEED,duration));
                    break;
                case Event.EVENT_PROGRESS:
                    int progress = intent.getIntExtra(Event.EVENT_PROGRESS,0);
                    EventBus.getDefault().post(new Event(Event.EVENT_SPEED,progress));
                    break;
                case Event.EVENT_TITLE:
                    String title = intent.getStringExtra(Event.EVENT_TITLE);
                    EventBus.getDefault().post(new Event(Event.EVENT_TITLE,title));
                    break;
            }
        }
    }
}
