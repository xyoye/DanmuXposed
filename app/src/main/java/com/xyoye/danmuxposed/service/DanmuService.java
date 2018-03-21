package com.xyoye.danmuxposed.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.xyoye.danmuxposed.R;
import com.xyoye.danmuxposed.bean.Event;
import com.xyoye.danmuxposed.receiver.EventBroadcast;
import com.xyoye.danmuxposed.utils.BiliDanmukuParser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

import static com.xyoye.danmuxposed.utils.DanmuConfig.DANMU_FONT_SIZE;
import static com.xyoye.danmuxposed.utils.DanmuConfig.DANMU_SPEED;

public class DanmuService extends BaseService {
    private DanmakuContext mDanmukuContext;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 101:
                    viewCloseBt.setText("更改");
                    break;
            }
            return false;
        }
    });

    @Override
    public void onCreate(){
        super.onCreate();
        initDanmuView();
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
        mDanmuView = mLayout.findViewById(R.id.danmu_view);
        mDanmukuContext = DanmakuContext.create();
        mDanmukuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(DANMU_SPEED)
                .setScaleTextSize(DANMU_FONT_SIZE)
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

    @Override
    public void startDanmu(){
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

    /**
     * 控制弹幕
     */
    @Subscribe(threadMode = ThreadMode.ASYNC )
    public void EventMessage(Event event) {
        switch (event.getKey()){
            case Event.EVENT_MX_START:
                System.out.println("mxPlayer is Start");
                handler.sendEmptyMessage(101);
                break;
            case Event.EVENT_START:
                System.out.println("video start");
                break;
            case Event.EVENT_PAUSE:
                System.out.println("video pause");
                break;
            case Event.EVENT_SPEED:
                System.out.println("video speed："+(int)event.getValue());
                break;
            case Event.EVENT_DURATION:
                System.out.println("video duration："+(int)event.getValue());
                break;
            case Event.EVENT_PROGRESS:
                System.out.println("video progress："+(int)event.getValue());
                break;
            case Event.EVENT_TITLE:
                System.out.println("video title："+event.getValue());
                break;
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
}
