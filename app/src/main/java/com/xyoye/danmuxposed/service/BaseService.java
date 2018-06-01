package com.xyoye.danmuxposed.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xyoye.danmuxposed.R;
import com.xyoye.danmuxposed.bean.Event;
import com.xyoye.danmuxposed.receiver.EventBroadcast;
import com.xyoye.danmuxposed.ui.activities.MainActivity;
import com.xyoye.danmuxposed.utils.Animation;

import org.greenrobot.eventbus.EventBus;

import master.flame.danmaku.ui.widget.DanmakuView;

/**
 * Created by xyy on 2018-03-21 下午 4:20
 */


public abstract class BaseService extends Service {
    boolean view_close = true;
    boolean progressDisplay = false;
    RelativeLayout mLayout;
    WindowManager.LayoutParams wmParams;
    WindowManager mWindowManager;

    RelativeLayout mDanmuLayout;
    DanmakuView mDanmuView;
    Button viewCloseBt;

    RelativeLayout progressController;
    Button speedA;
    Button speedDA;
    TextView speedText;

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
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startNotification();
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }else {
            wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        }
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        wmParams.gravity = Gravity.CENTER | Gravity.RIGHT;
        mWindowManager.addView(mLayout, wmParams);
        mLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        mDanmuLayout = mLayout.findViewById(R.id.danmu_layout);
        viewCloseBt = mLayout.findViewById(R.id.danmu_view_close);
        progressController = mLayout.findViewById(R.id.progress_controller);
        speedA = mLayout.findViewById(R.id.danmu_speed_accelerate);
        speedDA = mLayout.findViewById(R.id.danmu_speed_deceleration);
        speedText = mLayout.findViewById(R.id.danmu_speed_text);

        viewCloseBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if (view_close){
                    mDanmuLayout.setVisibility(View.VISIBLE);
                    mDanmuView.show();
                    wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                    wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                    mWindowManager.updateViewLayout(mLayout, wmParams);
                    viewCloseBt.setText("关闭");
                    new Animation().setHideAnimation(viewCloseBt);
                    viewCloseBt.setBackgroundResource(R.drawable.btn_circular_white);
                    view_close = false;
                }else {
                    mDanmuView.hide();
                    mDanmuLayout.setVisibility(View.GONE);
                    wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    mWindowManager.updateViewLayout(mLayout, wmParams);
                    viewCloseBt.setText("开启");
                    new Animation().setShowAnimation(viewCloseBt);
                    viewCloseBt.setBackgroundResource(R.drawable.btn_circular_blue);
                    view_close = true;
                }

            }
        });

        viewCloseBt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!view_close){
                    viewCloseBt.setEnabled(false);
                    progressController.setVisibility(View.VISIBLE);
                    progressDisplay = true;
                }
                return true;
            }
        });

        progressController.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mDanmuLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new Animation().setHideAnimation(viewCloseBt);
                if (progressDisplay){
                    viewCloseBt.setEnabled(true);
                    progressController.setVisibility(View.GONE);
                    progressDisplay = false;
                }
                return false;
            }
        });
    }

    private void startNotification(){
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("DanmuXposed")
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getString(R.string.app_name))
                .setContentText("DanmuXposed正在运行")
                .setContentIntent(pendingIntent);
        Notification notification = mNotifyBuilder.build();
        startForeground(101, notification);
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
}
