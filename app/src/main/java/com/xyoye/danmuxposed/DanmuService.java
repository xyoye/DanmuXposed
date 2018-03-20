package com.xyoye.danmuxposed;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.xyoye.danmuxposed.utils.BiliDanmukuParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;
import master.flame.danmaku.ui.widget.DanmakuView;

public class DanmuService extends Service {

    RelativeLayout mFloatLayout;
    WindowManager.LayoutParams wmParams;
    WindowManager mWindowManager;
    boolean view_close = false;

    RelativeLayout mDanmuLayout;
    DanmakuView mDanmuView;
    private DanmakuContext mDanmukuContext;

    @Override
    public void onCreate(){
        super.onCreate();
        initFloatView();
        initDanmuView();
        startDanmu();
    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    /**
     * 初始化悬浮界面
     */
    @SuppressLint({"ClickableViewAccessibility", "RtlHardcoded"})
    private void initFloatView(){
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        mFloatLayout = (RelativeLayout) inflater.inflate(R.layout.view_danmu, null);
        mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);

        wmParams = new WindowManager.LayoutParams();
        wmParams.width = LayoutParams.WRAP_CONTENT;
        wmParams.height = LayoutParams.WRAP_CONTENT;
        wmParams.type = LayoutParams.TYPE_TOAST;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.CENTER | Gravity.RIGHT;

        mWindowManager.addView(mFloatLayout, wmParams);
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        mDanmuLayout = mFloatLayout.findViewById(R.id.danmu_layout);
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

                    mDanmuView.hide();
                }else {
                    mDanmuLayout.setVisibility(View.VISIBLE);
                    wmParams.width = LayoutParams.MATCH_PARENT;
                    wmParams.height = LayoutParams.MATCH_PARENT;
                    mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                    viewCloseBt.setText("关闭");
                    view_close = true;

                    mDanmuView.resume();
                    mDanmuView.show();
                }

            }
        });
    }

    /**
     * 初始化DanmuView
     */
    @SuppressLint("UseSparseArrays")
    private void initDanmuView(){
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示5行
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
        mDanmuView = mFloatLayout.findViewById(R.id.danmu_view);
        mDanmukuContext = DanmakuContext.create();
        mDanmukuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f)
                .setScaleTextSize(1.0f)
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);
        if (mDanmuView != null) {
            mDanmuView.setCallback(new master.flame.danmaku.controller.DrawHandler.Callback() {
                @Override
                public void updateTimer(DanmakuTimer timer) {

                }

                @Override
                public void drawingFinished() {

                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {
                }

                @Override
                public void prepared() {
                    mDanmuView.start();
                    // TODO: 2018/3/18 获取视频进度
//                    float percent = mSeekBar.getProgress() / (float) mSeekBar.getMax();
//                    mDanmuView.seekTo((long) (mDanmuView.getDuration() * percent));
                    mDanmuView.show();
                    Log.i("PREPARED","解析完成,弹幕已启动！！！");
                }
            });
            mDanmuView.enableDanmakuDrawingCache(true);
        }
    }

    /**
     * 解析弹幕
     */
    private BaseDanmakuParser createParser(InputStream stream) {

        if (stream == null) {
            return new BaseDanmakuParser() {

                @Override
                protected Danmakus parse() {
                    return new Danmakus();
                }
            };
        }

        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);

        try {
            assert loader != null;
            loader.load(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmukuParser();
        IDataSource<?> dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;
    }

    private void startDanmu(){
        try {
            String DANMU_PATH = "/storage/9016-4EF8/其它/刀剑神域01.xml";
            mDanmuView.release();
            FileInputStream danmu = new FileInputStream(DANMU_PATH);
            BaseDanmakuParser mParser = createParser(danmu);
            mDanmuView.prepare(mParser, mDanmukuContext);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mFloatLayout != null) {
            mWindowManager.removeView(mFloatLayout);
        }
    }

}
