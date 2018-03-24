package com.xyoye.danmuxposed.service;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xyoye.danmuxposed.R;
import com.xyoye.danmuxposed.bean.Event;
import com.xyoye.danmuxposed.database.SharedPreferencesHelper;
import com.xyoye.danmuxposed.utils.BiliDanmukuParser;
import com.xyoye.danmuxposed.utils.FileUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;

import static com.xyoye.danmuxposed.utils.DanmuConfig.DANMU_FONT_SIZE_KEY;
import static com.xyoye.danmuxposed.utils.DanmuConfig.DANMU_SERVICE_START;
import static com.xyoye.danmuxposed.utils.DanmuConfig.DANMU_SPEED_KEY;
import static com.xyoye.danmuxposed.utils.DanmuConfig.READ_FILE_TYPE_KEY;
import static com.xyoye.danmuxposed.utils.DanmuConfig.READ_FOLDER_PATH_KEY;

public class DanmuService extends BaseService {
    private DanmakuContext mDanmukuContext;
    private SharedPreferencesHelper preferencesHelper;
    private String title;
    private int duration;
    private int progress;
    private int speed;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 101:
                    viewCloseBt.setText("开启");
                    break;
            }
            return false;
        }
    });

    @Override
    public void onCreate(){
        super.onCreate();
        preferencesHelper = SharedPreferencesHelper.getInstance();

        initDanmuView();
    }

    /**
     * 初始化DanmuView
     */
    @SuppressLint("UseSparseArrays")
    private void initDanmuView(){
        //速度
        float speed = Float.parseFloat(preferencesHelper.getString(DANMU_SPEED_KEY,"1.0"));
        float font_size = Float.parseFloat(preferencesHelper.getString(DANMU_FONT_SIZE_KEY,"1.0"));
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
                .setScrollSpeedFactor(speed)
                .setScaleTextSize(font_size)
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

    public void startDanmu(){
        try {
            int read_file_type = preferencesHelper.getInteger(READ_FILE_TYPE_KEY,1);
            if (read_file_type == 1){
                String read_folder_path = preferencesHelper.getString(READ_FOLDER_PATH_KEY,"");
                if ("".equals(read_folder_path))return;
                List<String> fileList = FileUtil.getAllFile(read_folder_path,false);
                for (int i = 0; i < fileList.size(); i++) {
                    String fileStr = fileList.get(i);
                    if (fileStr.contains(FileUtil.getFileName(title)+".xml")){
                        mDanmuView.release();
                        FileInputStream danmu = new FileInputStream(fileStr);
                        BaseDanmakuParser mParser = createParser(danmu);
                        mDanmuView.prepare(mParser, mDanmukuContext);
                    }
                }
            }
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
                startDanmu();
                break;
            case Event.EVENT_PAUSE:
                System.out.println("video pause");
                mDanmuView.pause();
                break;
            case Event.EVENT_SPEED:
                System.out.println("video speed："+(int)event.getValue());
                speed = (int)event.getValue();
                break;
            case Event.EVENT_DURATION:
                System.out.println("video duration："+(int)event.getValue());
                duration = (int)event.getValue();
                break;
            case Event.EVENT_PROGRESS:
                System.out.println("video progress："+(int)event.getValue());
                progress = (int)event.getValue();
                break;
            case Event.EVENT_TITLE:
                System.out.println("video title："+event.getValue());
                title = (String)event.getValue();
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
