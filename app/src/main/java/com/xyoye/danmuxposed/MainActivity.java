package com.xyoye.danmuxposed;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xyoye.danmuxposed.adapter.DrawerAdapter;
import com.xyoye.danmuxposed.bean.Event;
import com.xyoye.danmuxposed.database.DatabaseDao;
import com.xyoye.danmuxposed.database.SharedPreferencesHelper;
import com.xyoye.danmuxposed.service.DanmuService;
import com.xyoye.danmuxposed.ui.FolderChooserActivity;
import com.xyoye.danmuxposed.ui.ShieldingActivity;
import com.xyoye.danmuxposed.utils.ToastUtil;
import com.xyoye.danmuxposed.weight.AmountView;
import com.xyoye.danmuxposed.weight.SubmitButton;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.xyoye.danmuxposed.utils.DanmuConfig.BUTTON_DANMU_KEY;
import static com.xyoye.danmuxposed.utils.DanmuConfig.DANMU_FONT_SIZE_KEY;
import static com.xyoye.danmuxposed.utils.DanmuConfig.DANMU_SPEED_KEY;
import static com.xyoye.danmuxposed.utils.DanmuConfig.FILE;
import static com.xyoye.danmuxposed.utils.DanmuConfig.FOLDER;
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

    //layout1
    @BindView(R.id.layout1)
    RelativeLayout layout1;
    @BindView(R.id.danmu_switch)
    Button danmuSwitch;

    //layout2
    @BindView(R.id.layout2)
    RelativeLayout layout2;
    @BindView(R.id.danmu_speed_input)
    AmountView danmuSpeedInput;
    @BindView(R.id.font_size_input)
    AmountView fontSizeInput;
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
    Button shieldActivityBt;
    @BindView(R.id.danmu_setting_confirm)
    Button danmuSettingConfirmBt;
    @BindView(R.id.shield_number)
    TextView shieldNumberTv;

    //layout3
    @BindView(R.id.layout3)
    RelativeLayout layout3;
    @BindView(R.id.read_type_group)
    RadioGroup readTypeGroup;
    @BindView(R.id.read_file_radio)
    RadioButton readFileRadio;
    @BindView(R.id.read_folder_radio)
    RadioButton readFolderRadio;
    @BindView(R.id.file_path_tv)
    TextView filePathTv;
    @BindView(R.id.change_file_path)
    TextView changeFilePathTv;
    @BindView(R.id.folder_path_tv)
    TextView folderPathTv;
    @BindView(R.id.change_folder_path)
    TextView changeFolderPathTv;
    @BindView(R.id.path_setting_update)
    Button pathSettingConfirmBt;

    private final static int SELECT_FOLDER = 1;
    private final static int SELECT_FILE = 0;
    private static final int GET_FILE_PERMISSIONS = 101;

    private SharedPreferencesHelper preferencesHelper;
    private DatabaseDao databaseDao;

    private boolean filePermission = false;
    private boolean danmuStart = false;
    private List<String> drawerText;
    private List<Integer> drawerImage;
    private boolean waitExit = true;
    private int displayLayout; //0:首页，1：弹幕设置，2：路径设置
    private String defaultFolder = Environment.getExternalStorageDirectory().getAbsolutePath()+"/DanmuXposed/";

    private float font_size;
    private float danmu_speed;
    private boolean mobile_danmu;
    private boolean top_danmu;
    private boolean button_danmu;
    private int read_file_type;
    private String read_file_path;
    private String read_folder_path;

    @Override
    public void onCreate(Bundle savedInstanceState){
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
        drawerText.add("清除数据");
        drawerText.add("");
        drawerText.add("使用介绍");
        drawerText.add("关于");
        drawerImage.add(R.mipmap.home);
        drawerImage.add(R.mipmap.danmu);
        drawerImage.add(R.mipmap.file);
        drawerImage.add(R.mipmap.deleteimage);
        drawerImage.add(0);
        drawerImage.add(R.mipmap.help);
        drawerImage.add(R.mipmap.about);
    }

    private void initView(){
        title.setText(getResources().getString(R.string.main_title));
        setSupportActionBar(toolbar);
        ActionBar actionBar =  getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close) {
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

        DrawerAdapter drawerAdapter = new DrawerAdapter(drawerText,drawerImage,this);
        drawerListView.setAdapter(drawerAdapter);

        danmuSwitch.setText("启动监听" );
        danmuStart = false;
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.xyoye.danmuxposed.service.DanmuService".equals(service.service.getClassName())) {
                danmuSwitch.setText("关闭监听" );
                danmuStart = true;
            }
        }
    }

    private void initListener(){
        drawerListView.setOnItemClickListener(this);

        danmuSwitch.setOnClickListener(this);

        defaultFontSize.setOnClickListener(this);
        defaultSpeed.setOnClickListener(this);
        shieldActivityBt.setOnClickListener(this);
        danmuSettingConfirmBt.setOnClickListener(this);
        mobileDanmuIv.setOnClickListener(this);
        buttonDanmuIv.setOnClickListener(this);
        topDanmuIv.setOnClickListener(this);
        fontSizeInput.setOnAmountChangeListener(new AmountView.OnAmountChangeListener() {
            @Override
            public void onAmountChange(View view, float value) {
                font_size = value;
                EventBus.getDefault().post(new Event(Event.EVENT_DANMU_SIZE,font_size));
            }
        });
        danmuSpeedInput.setOnAmountChangeListener(new AmountView.OnAmountChangeListener() {
            @Override
            public void onAmountChange(View view, float value) {
                danmu_speed = value;
                EventBus.getDefault().post(new Event(Event.EVENT_DANMU_SPEED,danmu_speed));
            }
        });

        changeFilePathTv.setOnClickListener(this);
        changeFolderPathTv.setOnClickListener(this);
        pathSettingConfirmBt.setOnClickListener(this);

        readTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.read_file_radio:
                        read_file_type = FILE;
                        changeFilePathTv.setEnabled(true);
                        changeFolderPathTv.setEnabled(false);
                        changeFilePathTv.setTextColor(Color.parseColor("#33b5e5"));
                        changeFolderPathTv.setTextColor(Color.parseColor("#8a8a8a"));
                        break;
                    case R.id.read_folder_radio:
                        read_file_type = FOLDER;
                        changeFilePathTv.setEnabled(false);
                        changeFolderPathTv.setEnabled(true);
                        changeFilePathTv.setTextColor(Color.parseColor("#8a8a8a"));
                        changeFolderPathTv.setTextColor(Color.parseColor("#33b5e5"));
                        break;
                }
            }
        });
    }

    private void initOther(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED &&
                ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},GET_FILE_PERMISSIONS);
        } else{
            filePermission = true;
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
            case R.id.mobile_danmu_iv:
                mobile_danmu = !mobile_danmu;
                int resId = mobile_danmu ? R.mipmap.moblie_danmu_checked : R.mipmap.moblie_danmu_unchecked;
                EventBus.getDefault().post(new Event(Event.EVENT_DANMU_MOBILE,mobile_danmu));
                mobileDanmuIv.setImageResource(resId);
                break;
            case R.id.button_danmu_iv:
                button_danmu = !button_danmu;
                resId = button_danmu ? R.mipmap.bottom_danmu_checked : R.mipmap.bottom_danmu_unchecked;
                EventBus.getDefault().post(new Event(Event.EVENT_DANMU_BUTTON,button_danmu));
                buttonDanmuIv.setImageResource(resId);
                break;
            case R.id.top_danmu_iv:
                top_danmu = !top_danmu;
                resId = top_danmu ? R.mipmap.top_danmu_checked : R.mipmap.top_danmu_unchecked;
                EventBus.getDefault().post(new Event(Event.EVENT_DANMU_TOP,top_danmu));
                topDanmuIv.setImageResource(resId);
                break;
            case R.id.shielding_activity_bt:
                Intent intent = new Intent(MainActivity.this,ShieldingActivity.class);
                startActivity(intent);
                break;
            case R.id.change_file_path:
                selectPath(false);
                break;
            case R.id.change_folder_path:
                selectPath(true);
                break;
            case R.id.danmu_setting_confirm:
                saveInfo(1);
                break;
            case R.id.path_setting_update:
                saveInfo(2);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                checkOutSave(0);
                break;
            case 1:
                checkOutSave(1);
                break;
            case 2:
                checkOutSave(2);
                break;
            case 3:
                deleteData();
                break;
        }
    }

    /**
     * 选择读取弹幕路径
     */
    private void selectPath(boolean folder){
        if (filePermission){
            Intent intent = new Intent(MainActivity.this, FolderChooserActivity.class);
            int code = folder ? SELECT_FOLDER : SELECT_FILE;
            String path;
            if (folder){
                path = "".equals(read_folder_path) ? null : read_folder_path;
            }else {
                path = read_file_path;
                path = "".equals(read_file_path) ? null : path.substring(0,path.lastIndexOf("/"));
            }
            intent.putExtra("isFolderChooser", folder);
            intent.putExtra("file_path",path);
            startActivityForResult(intent,code);
        }else {
            ToastUtil.showToast(MainActivity.this,"请打开DanmuXposed写入文件权限");
        }
    }

    /**
     * 打开监听
     */
    private void floatViewSwitch(){
        if(filePermission){
            if (!danmuStart){
                danmuStart = true;
                Intent intent = new Intent(MainActivity.this, DanmuService.class);
                startService(intent);
                danmuSwitch.setText("关闭监听");
                finish();
            }else{
                danmuStart = false;
                Intent intent = new Intent(MainActivity.this, DanmuService.class);
                stopService(intent);
                danmuSwitch.setText("启动监听");
            }
        }else {
            ToastUtil.showToast(MainActivity.this,"请打开DanmuXposed读取文件权限");
        }
    }

    /**
     * 切换界面前的dialog
     */
    private void changePageDialog(final int layoutN){
        AlertDialog.Builder builder_save = new AlertDialog.Builder(MainActivity.this);
        builder_save.setTitle("提示").setMessage("信息未保存");
        builder_save.setNegativeButton("放弃", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changePage(layoutN);
            }
        }).setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveInfo(displayLayout);
                changePage(layoutN);
            }
        }).setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDrawerLayout.closeDrawers();
            }
        });
        builder_save.show();
    }

    /**
     * 切换界面
     * @param layoutN 0：主页，1：弹幕设置，2：路径设置
     */
    private void changePage(int layoutN){
        displayLayout = layoutN;
        switch (layoutN){
            case 0:
                title.setText("弹幕播放器");
                layout1.setVisibility(View.VISIBLE);
                layout2.setVisibility(View.GONE);
                layout3.setVisibility(View.GONE);
                mDrawerLayout.closeDrawers();
                break;
            case 1:
                font_size = Float.parseFloat(preferencesHelper.getString(DANMU_FONT_SIZE_KEY,"1.0"));
                danmu_speed = Float.parseFloat(preferencesHelper.getString(DANMU_SPEED_KEY,"1.0"));
                mobile_danmu = preferencesHelper.getBoolean(MOBILE_DANMU_KEY,false);
                top_danmu = preferencesHelper.getBoolean(TOP_DANMU_KEY,false);
                button_danmu = preferencesHelper.getBoolean(BUTTON_DANMU_KEY,false);

                fontSizeInput.setValue(font_size);
                danmuSpeedInput.setValue(danmu_speed);
                int resId = mobile_danmu ? R.mipmap.moblie_danmu_checked : R.mipmap.moblie_danmu_unchecked;
                mobileDanmuIv.setImageResource(resId);
                resId = button_danmu ? R.mipmap.bottom_danmu_checked : R.mipmap.bottom_danmu_unchecked;
                buttonDanmuIv.setImageResource(resId);
                resId = top_danmu ? R.mipmap.top_danmu_checked : R.mipmap.top_danmu_unchecked;
                topDanmuIv.setImageResource(resId);
                String shieldN = databaseDao.queryAllShield().size()+"";
                shieldNumberTv.setText(shieldN);

                title.setText("弹幕设置");
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
                layout3.setVisibility(View.GONE);
                mDrawerLayout.closeDrawers();
                break;
            case 2:
                read_file_type = preferencesHelper.getInteger(READ_FILE_TYPE_KEY,FOLDER);
                read_file_path = preferencesHelper.getString(READ_FILE_PATH_KEY,"");
                read_folder_path = preferencesHelper.getString(READ_FOLDER_PATH_KEY,defaultFolder);

                folderPathTv.setText(read_folder_path);
                filePathTv.setText(read_file_path);
                if (read_file_type == FILE){
                    readFileRadio.setChecked(true);
                    changeFilePathTv.setEnabled(true);
                    changeFolderPathTv.setEnabled(false);
                }else {
                    readFolderRadio.setChecked(true);
                    changeFilePathTv.setEnabled(false);
                    changeFolderPathTv.setEnabled(true);
                }

                title.setText("路径设置");
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.GONE);
                layout3.setVisibility(View.VISIBLE);
                mDrawerLayout.closeDrawers();
                break;
        }
    }

    /**
     * 检查当前页面信息是否已保存
     */
    private void checkOutSave(final int layoutN){
        if (displayLayout ==  layoutN){
            mDrawerLayout.closeDrawers();
            return;
        }

        switch (displayLayout){
            case 0:
                changePage(layoutN);
                break;
            case 1:
                if (font_size != Float.parseFloat(preferencesHelper.getString(DANMU_FONT_SIZE_KEY,"1.0")) ||
                        danmu_speed != Float.parseFloat(preferencesHelper.getString(DANMU_SPEED_KEY,"1.0")) ||
                        mobile_danmu != preferencesHelper.getBoolean(MOBILE_DANMU_KEY,false) ||
                        top_danmu != preferencesHelper.getBoolean(TOP_DANMU_KEY,false) ||
                        button_danmu != preferencesHelper.getBoolean(BUTTON_DANMU_KEY,false))
                {
                    changePageDialog(layoutN);
                }else
                    changePage(layoutN);
                break;
            case 2:
                if(read_file_type != preferencesHelper.getInteger(READ_FILE_TYPE_KEY,FOLDER) ||
                        !preferencesHelper.getString(READ_FILE_PATH_KEY,"").equals(read_file_path) ||
                        !preferencesHelper.getString(READ_FOLDER_PATH_KEY,defaultFolder).equals(read_folder_path))
                {
                    changePageDialog(layoutN);
                }else
                    changePage(layoutN);
                break;
        }
    }

    /**
     * 保存信息
     */
    private void saveInfo(int layoutN){
        switch (layoutN){
            case 0:
                break;
            case 1:
                preferencesHelper.saveString(DANMU_FONT_SIZE_KEY,String.valueOf(font_size));
                preferencesHelper.saveString(DANMU_SPEED_KEY,String.valueOf(danmu_speed));
                preferencesHelper.saveBoolean(MOBILE_DANMU_KEY,mobile_danmu);
                preferencesHelper.saveBoolean(BUTTON_DANMU_KEY,button_danmu);
                preferencesHelper.saveBoolean(TOP_DANMU_KEY,top_danmu);
                ToastUtil.showToast(MainActivity.this,"保存成功！");
                loseEtFocus();
                break;
            case 2:
                preferencesHelper.saveInteger(READ_FILE_TYPE_KEY,read_file_type);
                preferencesHelper.saveString(READ_FILE_PATH_KEY,read_file_path);
                preferencesHelper.saveString(READ_FOLDER_PATH_KEY,read_folder_path);
                ToastUtil.showToast(MainActivity.this,"保存成功！");
                break;
        }
    }

    /**
     * 取消输入框焦点
     */
    private void loseEtFocus(){
        View view = MainActivity.this.getCurrentFocus();
        if (view != null){
            view.clearFocus();
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert manager != null;
            manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void deleteData(){
        final AlertDialog.Builder builder_delete = new AlertDialog.Builder(MainActivity.this);
        View dialogView = View.inflate(MainActivity.this,R.layout.dialog_delete,null);
        final TextView deleteTv = dialogView.findViewById(R.id.delete_tv);
        final SubmitButton deleteBt = dialogView.findViewById(R.id.delete_bt);
        String tip = "确认删除所有数据？";
        deleteTv.setText(tip);
        deleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseDao.deleteAllShield();
                EventBus.getDefault().post(new Event(Event.EVENT_DANMU_SHIELD_REMOVE_ALL));
                databaseDao.deleteAll();
                deleteTv.setText("数据已清空");
                deleteBt.doResult(true);
            }
        });
        builder_delete.setView(dialogView);
        builder_delete.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_FOLDER:
                    read_folder_path = data.getStringExtra("file_path");
                    folderPathTv.setText(read_folder_path);
                    break;
                case SELECT_FILE:
                    read_file_path = data.getStringExtra("file_path");
                    filePathTv.setText(read_file_path);
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String shieldN = databaseDao.queryAllShield().size()+"";
        shieldNumberTv.setText(shieldN);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == GET_FILE_PERMISSIONS){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                filePermission = true;
            }else {
                ToastUtil.showToast(MainActivity.this,"读写文件权限被拒绝");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

}
