package com.xyoye.danmuxposed;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;

import master.flame.danmaku.ui.widget.DanmakuView;

public class DanmuService extends Service {

    RelativeLayout mFloatLayout;
    RelativeLayout mDanmuLayout;
    DanmakuView mDanmuView;
    WindowManager.LayoutParams wmParams;
    WindowManager mWindowManager;
    boolean view_close = false;

    @Override
    public void onCreate(){
        super.onCreate();
        createFloatView();
    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @SuppressLint({"ClickableViewAccessibility", "RtlHardcoded"})
    private void createFloatView(){
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        mFloatLayout = (RelativeLayout) inflater.inflate(R.layout.view_danmu, null);
        mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);

        wmParams = new WindowManager.LayoutParams();
        wmParams.width = LayoutParams.WRAP_CONTENT;
        wmParams.height = LayoutParams.WRAP_CONTENT;
        wmParams.type = LayoutParams.TYPE_PHONE;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.CENTER | Gravity.RIGHT;

        mWindowManager.addView(mFloatLayout, wmParams);
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        mDanmuLayout = mFloatLayout.findViewById(R.id.danmu_layout);
        mDanmuView = mFloatLayout.findViewById(R.id.danmu_view);
        final Button viewCloseBt = mFloatLayout.findViewById(R.id.danmu_view_close);
        viewCloseBt.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v)
            {
                if (view_close){
                    mDanmuLayout.setVisibility(View.GONE);
                    wmParams.width = LayoutParams.WRAP_CONTENT;
                    wmParams.height = LayoutParams.WRAP_CONTENT;
                    mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                    viewCloseBt.setText("开启");
                    view_close = false;
                }else {
                    mDanmuLayout.setVisibility(View.VISIBLE);
                    wmParams.width = LayoutParams.MATCH_PARENT;
                    wmParams.height = LayoutParams.MATCH_PARENT;
                    mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                    viewCloseBt.setText("关闭");
                    view_close = true;
                }

            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mFloatLayout != null) {
            mWindowManager.removeView(mFloatLayout);
        }
    }

}
