package com.xyoye.danmuxposed.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.xyoye.danmuxposed.R;
import com.xyoye.danmuxposed.bean.Event;
import com.xyoye.danmuxposed.receiver.EventBroadcast;

import org.greenrobot.eventbus.EventBus;

import master.flame.danmaku.ui.widget.DanmakuView;

/**
 * Created by xyy on 2018-03-21 下午 4:20
 */


public abstract class BaseService extends Service {
    boolean view_close = false;
    RelativeLayout mLayout;
    WindowManager.LayoutParams wmParams;
    WindowManager mWindowManager;

    RelativeLayout mDanmuLayout;
    DanmakuView mDanmuView;
    Button viewCloseBt;

    IntentFilter intentFilter;
    EventBroadcast myBrodcast;

    @Override
    public void onCreate(){
        super.onCreate();
        initRegister();
        initFloatView();
    }

    /**
     * 注册广播、EventBus
     */
    private void initRegister(){
        EventBus.getDefault().register(this);
        myBrodcast = new EventBroadcast();
        intentFilter = new IntentFilter();
        intentFilter.addAction(Event.EVENT_MX_START);
        intentFilter.addAction(Event.EVENT_START);
        intentFilter.addAction(Event.EVENT_PAUSE);
        intentFilter.addAction(Event.EVENT_SPEED);
        intentFilter.addAction(Event.EVENT_DURATION);
        intentFilter.addAction(Event.EVENT_PROGRESS);
        intentFilter.addAction(Event.EVENT_TITLE);
        registerReceiver(myBrodcast, intentFilter);
    }

    /**
     * 初始化悬浮窗界面
     */
    @SuppressLint({"ClickableViewAccessibility", "RtlHardcoded"})
    private void initFloatView(){
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        mLayout = (RelativeLayout) inflater.inflate(R.layout.view_danmu, null);
        mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);

        wmParams = new WindowManager.LayoutParams();
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.CENTER | Gravity.RIGHT;

        mWindowManager.addView(mLayout, wmParams);
        mLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        mDanmuLayout = mLayout.findViewById(R.id.danmu_layout);
        viewCloseBt = mLayout.findViewById(R.id.danmu_view_close);

        viewCloseBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if (view_close){
                    mDanmuLayout.setVisibility(View.GONE);
                    wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    mWindowManager.updateViewLayout(mLayout, wmParams);
                    viewCloseBt.setText("开启");
                    view_close = false;

                    mDanmuView.hide();
                }else {
                    mDanmuLayout.setVisibility(View.VISIBLE);
                    wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                    wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                    mWindowManager.updateViewLayout(mLayout, wmParams);
                    viewCloseBt.setText("关闭");
                    view_close = true;
                    if (mDanmuView.isShown()){
                        startDanmu();
                    }else {
                        mDanmuView.resume();
                    }
                    mDanmuView.show();
                }

            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mLayout != null) {
            mWindowManager.removeView(mLayout);
        }
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        unregisterReceiver(myBrodcast);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public abstract void startDanmu();
}
