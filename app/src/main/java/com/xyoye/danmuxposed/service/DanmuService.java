package com.xyoye.danmuxposed.service;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;

import com.xyoye.danmuxposed.R;
import com.xyoye.danmuxposed.bean.Event;
import com.xyoye.danmuxposed.database.DatabaseDao;
import com.xyoye.danmuxposed.database.SharedPreferencesHelper;
import com.xyoye.danmuxposed.utils.BiliDanmukuParser;
import com.xyoye.danmuxposed.utils.FileUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.ui.widget.DanmakuView;

import static com.xyoye.danmuxposed.utils.DanmuConfig.BUTTON_DANMU_KEY;
import static com.xyoye.danmuxposed.utils.DanmuConfig.DANMU_FONT_SIZE_KEY;
import static com.xyoye.danmuxposed.utils.DanmuConfig.DANMU_SPEED_KEY;
import static com.xyoye.danmuxposed.utils.DanmuConfig.FOLDER;
import static com.xyoye.danmuxposed.utils.DanmuConfig.MOBILE_DANMU_KEY;
import static com.xyoye.danmuxposed.utils.DanmuConfig.READ_FILE_PATH_KEY;
import static com.xyoye.danmuxposed.utils.DanmuConfig.READ_FILE_TYPE_KEY;
import static com.xyoye.danmuxposed.utils.DanmuConfig.READ_FOLDER_PATH_KEY;
import static com.xyoye.danmuxposed.utils.DanmuConfig.TOP_DANMU_KEY;

public class DanmuService extends BaseService {
    private DanmakuContext mDanmukuContext;
    private SharedPreferencesHelper preferencesHelper;
    private String title;
    private int progress;

    private DatabaseDao databaseDao;
    private float fontSize;
    private float danmuSpeed;
    private int read_file_type;
    private String filePath;
    private boolean mobileDanmu;
    private boolean topDanmu;
    private boolean buttonDanmu;
    private List<String> shieldList;
    private List<String> fileList;
    private double changeSpeed = 0;

    @Override
    public void onCreate(){
        super.onCreate();
        preferencesHelper = SharedPreferencesHelper.getInstance();

        initData();

        initDanmuView();

        initListener();
    }

    private void initData(){
        read_file_type = preferencesHelper.getInteger(READ_FILE_TYPE_KEY,FOLDER);
        if (read_file_type == FOLDER){
            String read_folder_path = preferencesHelper.getString(READ_FOLDER_PATH_KEY,"");
            fileList = new ArrayList<>();
            if (!"".equals(read_folder_path)){
                fileList = FileUtil.getAllFile(read_folder_path,false);
            }
        }else {
            filePath = preferencesHelper.getString(READ_FILE_PATH_KEY,"");
        }

        databaseDao = new DatabaseDao(getApplicationContext());
        fontSize = Float.parseFloat(preferencesHelper.getString(DANMU_FONT_SIZE_KEY,"1.0"));
        danmuSpeed = Float.parseFloat(preferencesHelper.getString(DANMU_SPEED_KEY,"1.0"));
        shieldList = databaseDao.queryAllShield();

        mobileDanmu = preferencesHelper.getBoolean(MOBILE_DANMU_KEY,true);
        topDanmu = preferencesHelper.getBoolean(TOP_DANMU_KEY,true);
        buttonDanmu = preferencesHelper.getBoolean(BUTTON_DANMU_KEY,true);
    }

    /**
     * 初始化DanmuView
     */
    @SuppressLint("UseSparseArrays")
    private void initDanmuView(){
        initDanmuContext();
        mDanmuView = mLayout.findViewById(R.id.danmu_view);

        if (!mobileDanmu) {
            mDanmukuContext.setR2LDanmakuVisibility(true);
            mDanmukuContext.setL2RDanmakuVisibility(true);
        }
        if (!topDanmu) mDanmukuContext.setFBDanmakuVisibility(true);
        if (!buttonDanmu) mDanmukuContext.setFTDanmakuVisibility(true);
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
                    mDanmuView.seekTo((long) (progress));
                    mDanmuView.show();
                    Log.i("PREPARED","解析完成,弹幕已启动！！！");
                }
            });
            mDanmuView.enableDanmakuDrawingCache(true);
        }
    }

    public void initListener(){
        speedA.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (mDanmuView.isPrepared()){
                    mDanmuView.seekTo(mDanmuView.getCurrentTime()+500);
                    changeSpeed += 0.5;
                    speedText.setText(changeSpeed+"");
                }
            }
        });
        speedDA.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (mDanmuView.isPrepared()){
                    mDanmuView.seekTo(mDanmuView.getCurrentTime()-500);
                    changeSpeed -= 0.5;
                    speedText.setText(changeSpeed+"");
                }
            }
        });
    }

    /**
     * 启动弹幕
     */
    public void startDanmu(){
        if (mDanmuView.isPrepared()){
            mDanmuView.resume();
            mDanmuView.seekTo((long)progress);
        }else {
            try {
                boolean getXml = false;
                if (read_file_type == FOLDER){
                    //从文件夹获取
                    for (int i = 0; i < fileList.size(); i++) {
                        String fileStr = fileList.get(i);
                        if (fileStr.contains(FileUtil.getFileName(title)+".xml")){
                            filePath = fileStr;
                            getXml = true;
                            break;
                        }
                    }
                    //从上一次播放获取
                    if (!getXml){
                        List<String> list = databaseDao.query(title);
                        if (list.size() > 0){
                            filePath = list.get(0);
                            getXml = true;
                        }
                    }
                    if (getXml){
                        File file = new File(filePath);
                        if (!file.exists())return;
                        mDanmuView.release();
                        FileInputStream danmu = new FileInputStream(filePath);
                        mDanmuView.prepare(BiliDanmukuParser.createParser(danmu), mDanmukuContext);
                        databaseDao.insert(title,filePath);
                    }
                }else {
                    //从上一次播放获取
                    if ("".equals(filePath)){
                        List<String> list = databaseDao.query(title);
                        if (list.size() > 0){
                            filePath = list.get(0);
                            getXml = true;
                        }
                    }else {
                        File file = new File(filePath);
                        if (!file.exists())return;
                        mDanmuView.release();
                        FileInputStream danmu = new FileInputStream(filePath);
                        mDanmuView.prepare(BiliDanmukuParser.createParser(danmu), mDanmukuContext);
                        databaseDao.insert(title,filePath);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void initDanmuContext(){
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示5行
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
        mDanmukuContext = DanmakuContext.create();
        mDanmukuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(danmuSpeed)
                .setScaleTextSize(fontSize)
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair)
                .setKeyWordBlackList(shieldList);
    }

    /**
     * 控制弹幕
     */
    @Subscribe(threadMode = ThreadMode.ASYNC )
    public void EventMessage(Event event) {
        switch (event.getKey()){
            case Event.EVENT_START:
                System.out.println("video start");
                startDanmu();
                break;
            case Event.EVENT_PAUSE:
                System.out.println("video pause");
                mDanmuView.pause();
                break;
            case Event.EVENT_PROGRESS:
                System.out.println("video progress："+(int)event.getValue());
                progress = (int)event.getValue();
                break;
            case Event.EVENT_TITLE:
                System.out.println("video title："+event.getValue());
                title = (String)event.getValue();
                break;
            case Event.EVENT_DANMU_SIZE:
                mDanmukuContext.setScaleTextSize((float)event.getValue());
                break;
            case Event.EVENT_DANMU_SPEED:
                mDanmukuContext.setScrollSpeedFactor((float)event.getValue());
                break;
            case Event.EVENT_DANMU_MOBILE:
                mDanmukuContext.setR2LDanmakuVisibility((boolean)event.getValue());
                mDanmukuContext.setL2RDanmakuVisibility((boolean)event.getValue());
                break;
            case Event.EVENT_DANMU_TOP:
                mDanmukuContext.setFBDanmakuVisibility((boolean)event.getValue());
                break;
            case Event.EVENT_DANMU_BUTTON:
                mDanmukuContext.setFTDanmakuVisibility((boolean)event.getValue());
                break;
            case Event.EVENT_DANMU_SHIELD_ADD:
                mDanmukuContext.setKeyWordBlack((String)event.getValue());
                break;
            case Event.EVENT_DANMU_SHIELD_REMOVE:
                mDanmukuContext.removeKeyWordBlack((String)event.getValue());
                break;
            case Event.EVENT_DANMU_SHIELD_REMOVE_ALL:
                mDanmukuContext.removeAllKeyWordBlack();
                break;
        }
    }
}
