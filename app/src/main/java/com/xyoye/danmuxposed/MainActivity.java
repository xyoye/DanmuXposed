package com.xyoye.danmuxposed;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xyoye.danmuxposed.adapter.DrawerAdapter;
import com.xyoye.danmuxposed.database.DatabaseDao;
import com.xyoye.danmuxposed.database.SharedPreferencesHelper;
import com.xyoye.danmuxposed.service.DanmuService;
import com.xyoye.danmuxposed.utils.ToastUtil;
import com.xyoye.danmuxposed.weight.AmountView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.xyoye.danmuxposed.utils.DanmuConfig.BUTTON_DANMU_KEY;
import static com.xyoye.danmuxposed.utils.DanmuConfig.DANMU_FONT_SIZE_KEY;
import static com.xyoye.danmuxposed.utils.DanmuConfig.DANMU_SPEED_KEY;
import static com.xyoye.danmuxposed.utils.DanmuConfig.MOBILE_DANMU_KEY;
import static com.xyoye.danmuxposed.utils.DanmuConfig.READ_FILE_PATH_KEY;
import static com.xyoye.danmuxposed.utils.DanmuConfig.READ_FILE_TYPE_KEY;
import static com.xyoye.danmuxposed.utils.DanmuConfig.READ_FOLDER_PATH_KEY;
import static com.xyoye.danmuxposed.utils.DanmuConfig.TOP_DANMU_KEY;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener{
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.drawer_list_view)
    ListView drawerListView;

    @BindView(R.id.layout1)
    RelativeLayout layout1;
    @BindView(R.id.danmu_switch)
    Button danmuSwitch;
    @BindView(R.id.mobile_danmu_iv)
    ImageView mobileDanmuIv;
    @BindView(R.id.button_danmu_iv)
    ImageView buttonDanmuIv;
    @BindView(R.id.top_danmu_iv)
    ImageView topDanmuIv;
    @BindView(R.id.default_font_size)
    TextView defaultFontSize;
    @BindView(R.id.default_speed)
    TextView defaultSpeed;
    @BindView(R.id.shielding_activity_bt)
    Button shieldAcivityBt;
    @BindView(R.id.danmu_setting_confirm)
    Button danmuSettingConfirm;
    @BindView(R.id.shield_number)
    TextView shieldNumberTv;


    @BindView(R.id.layout2)
    RelativeLayout layout2;
    @BindView(R.id.danmu_speed_input)
    AmountView danmuSpeedInput;
    @BindView(R.id.font_size_input)
    AmountView fontSizeInput;
    @BindView(R.id.layout3)
    RelativeLayout layout3;

    SharedPreferencesHelper preferencesHelper;
    DatabaseDao databaseDao;

    private static final int READ_EXTERNAL_STORAGE = 101;
    private boolean readFilePermission = false;
    private boolean danmuStart = false;
    private List<String> drawerText;
    private List<Integer> drawerImage;
    private boolean waitExit = true;
    private int displayLayout;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerAdapter drawerAdapter;
    private float font_size;
    private float danmu_speed;
    private boolean mobile_danmu;
    private boolean top_danmu;
    private boolean button_danmu;
    private int read_file_type;
    private String read_file_path;
    private String read_folder_path;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        SharedPreferencesHelper.init(this);

        initData();

        initView();

        initListener();

        initOther();
    }

    private void initData(){
        preferencesHelper = SharedPreferencesHelper.getInstance();
        databaseDao = new DatabaseDao(this);

        drawerText = new ArrayList<>();
        drawerImage = new ArrayList<>();
        drawerText.add("主界面");
        drawerText.add("弹幕设置");
        drawerText.add("路径设置");
        drawerImage.add(R.drawable.home);
        drawerImage.add(R.drawable.danmu);
        drawerImage.add(R.drawable.file);
    }

    private void initView(){
        title.setText(getResources().getString(R.string.main_title));
        setSupportActionBar(toolbar);
        ActionBar actionBar =  getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                loseEtFocus();
                super.onDrawerOpened(drawerView);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        drawerAdapter = new DrawerAdapter(drawerText,drawerImage,this);
        drawerListView.setAdapter(drawerAdapter);
    }

    private void initListener(){
        drawerListView.setOnItemClickListener(this);

        danmuSwitch.setOnClickListener(this);

        defaultFontSize.setOnClickListener(this);
        defaultSpeed.setOnClickListener(this);
        shieldAcivityBt.setOnClickListener(this);
        danmuSettingConfirm.setOnClickListener(this);
        mobileDanmuIv.setOnClickListener(this);
        buttonDanmuIv.setOnClickListener(this);
        topDanmuIv.setOnClickListener(this);
    }

    private void initOther(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_EXTERNAL_STORAGE);
        }else {
            readFilePermission = true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.danmu_switch:
                floatViewSwitch();
                break;
            case R.id.default_font_size:
                fontSizeInput.setValue(1.0f);
                preferencesHelper.saveString(DANMU_FONT_SIZE_KEY,"1.0");
                break;
            case R.id.default_speed:
                danmuSpeedInput.setValue(1.0f);
                preferencesHelper.saveString(DANMU_SPEED_KEY,"1.0");
                break;
            case R.id.shielding_activity_bt:
                Intent intent = new Intent(MainActivity.this,ShieldingActivity.class);
                startActivity(intent);
                break;
            case R.id.danmu_setting_confirm:
                preferencesHelper.saveString(DANMU_FONT_SIZE_KEY,String.valueOf(fontSizeInput.getValue()));
                preferencesHelper.saveString(DANMU_SPEED_KEY,String.valueOf(danmuSpeedInput.getValue()));
                preferencesHelper.saveBoolean(MOBILE_DANMU_KEY,mobile_danmu);
                preferencesHelper.saveBoolean(BUTTON_DANMU_KEY,button_danmu);
                preferencesHelper.saveBoolean(TOP_DANMU_KEY,top_danmu);
                ToastUtil.showToast(MainActivity.this,"保存成功！");
                loseEtFocus();
                break;
            case R.id.mobile_danmu_iv:
                mobile_danmu = !mobile_danmu;
                int resId = mobile_danmu ? R.drawable.moblie_danmu_checked : R.drawable.moblie_danmu_unchecked;
                mobileDanmuIv.setImageResource(resId);
                break;
            case R.id.button_danmu_iv:
                button_danmu = !button_danmu;
                resId = button_danmu ? R.drawable.botton_danmu_checked : R.drawable.moblie_danmu_unchecked;
                buttonDanmuIv.setImageResource(resId);
                break;
            case R.id.top_danmu_iv:
                top_danmu = !top_danmu;
                resId = top_danmu ? R.drawable.top_danmu_checked : R.drawable.top_danmu_unchecked;
                topDanmuIv.setImageResource(resId);
                break;
        }
    }

    private void floatViewSwitch(){
        if (readFilePermission && !danmuStart){
            danmuStart = true;
            danmuSwitch.setText("关闭弹幕");
            Intent intent = new Intent(MainActivity.this, DanmuService.class);
            startService(intent);
            finish();
        }else if (danmuStart){
            danmuStart = false;
            danmuSwitch.setText("关闭弹幕");
            Intent intent = new Intent(MainActivity.this, DanmuService.class);
            stopService(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_EXTERNAL_STORAGE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                readFilePermission = true;
            }else {
                ToastUtil.showToast(MainActivity.this,"读取弹幕文件权限被拒绝");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                title.setText("弹幕播放器");
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);
                layout3.setVisibility(View.GONE);
                mDrawerLayout.closeDrawers();
                initLayout(0);
                break;
            case 1:
                title.setText("弹幕设置");
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
                layout3.setVisibility(View.GONE);
                mDrawerLayout.closeDrawers();
                initLayout(1);
                break;
            case 2:
                title.setText("路径设置");
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.GONE);
                layout3.setVisibility(View.VISIBLE);
                mDrawerLayout.closeDrawers();
                initLayout(2);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (waitExit) {
            waitExit = false;
            ToastUtil.showToast(MainActivity.this,getString(R.string.press_to_exit));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    waitExit = true;
                }
            }, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }

    private boolean checkOutSave(){
        switch (displayLayout){
            case 1:

                break;
            case 2:
                break;
        }
        return true;
    }

    private void initLayout(int layoutN){
        if (!checkOutSave()){
            ToastUtil.showToast(MainActivity.this,"有数据未保存");
        }
        displayLayout = layoutN;
        switch (displayLayout){
            case 0:
                break;
            case 1:
                font_size = Float.parseFloat(preferencesHelper.getString(DANMU_FONT_SIZE_KEY,"1.0"));
                danmu_speed = Float.parseFloat(preferencesHelper.getString(DANMU_SPEED_KEY,"1.0"));
                mobile_danmu = preferencesHelper.getBoolean(MOBILE_DANMU_KEY,false);
                top_danmu = preferencesHelper.getBoolean(TOP_DANMU_KEY,false);
                button_danmu = preferencesHelper.getBoolean(BUTTON_DANMU_KEY,false);
                read_file_type = preferencesHelper.getInteger(READ_FILE_TYPE_KEY,0);
                read_file_path = preferencesHelper.getString(READ_FILE_PATH_KEY,"");
                read_folder_path = preferencesHelper.getString(READ_FOLDER_PATH_KEY,"");

                fontSizeInput.setValue(font_size);
                danmuSpeedInput.setValue(danmu_speed);
                int resId = mobile_danmu ? R.drawable.moblie_danmu_checked : R.drawable.moblie_danmu_unchecked;
                mobileDanmuIv.setImageResource(resId);
                resId = button_danmu ? R.drawable.botton_danmu_checked : R.drawable.moblie_danmu_unchecked;
                buttonDanmuIv.setImageResource(resId);
                resId = top_danmu ? R.drawable.top_danmu_checked : R.drawable.top_danmu_unchecked;
                topDanmuIv.setImageResource(resId);
                String shieldN = databaseDao.queryAllShield().size()+"";
                shieldNumberTv.setText(shieldN);
                break;
            case 2:
                break;
        }
    }

    private void loseEtFocus(){
        View view = MainActivity.this.getCurrentFocus();
        if (view != null){
            view.clearFocus();
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert manager != null;
            manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String shieldN = databaseDao.queryAllShield().size()+"";
        shieldNumberTv.setText(shieldN);
    }
}
