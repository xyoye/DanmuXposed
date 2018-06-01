package com.xyoye.danmuxposed.ui.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.xyoye.danmuxposed.R;
import com.xyoye.danmuxposed.ui.adapter.DrawerAdapter;
import com.xyoye.danmuxposed.bean.Event;
import com.xyoye.danmuxposed.database.DatabaseDao;
import com.xyoye.danmuxposed.database.SharedPreferencesHelper;
import com.xyoye.danmuxposed.ui.fragment.AboutFragment;
import com.xyoye.danmuxposed.ui.fragment.DanmuPathFragment;
import com.xyoye.danmuxposed.ui.fragment.DanmuSettingFragment;
import com.xyoye.danmuxposed.ui.fragment.MainFragment;
import com.xyoye.danmuxposed.utils.ToastUtil;
import com.xyoye.danmuxposed.utils.permissionchecker.PermissionHelper;
import com.xyoye.danmuxposed.ui.weight.SubmitButton;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.drawer_list_view)
    ListView drawerListView;

    private DatabaseDao databaseDao;

    private List<String> drawerText;
    private List<Integer> drawerImage;
    private boolean waitExit = true;

    private MainFragment mainFragment;
    private DanmuSettingFragment danmuSettingFragment;
    private DanmuPathFragment danmuPathFragment;
    private AboutFragment introFragment;
    private AboutFragment aboutFragment;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initData();

        initView();

        initListener();
    }

    private void initData(){
        databaseDao = new DatabaseDao(this);

        drawerText = new ArrayList<>();
        drawerImage = new ArrayList<>();
        drawerText.add("主界面");
        drawerText.add("弹幕设置");
        drawerText.add("弹幕管理");
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

        new PermissionHelper().with(this).request(new PermissionHelper.OnSuccessListener() {
            @Override
            public void onPermissionSuccess() {

            }
        }, "android.permission.READ_EXTERNAL_STORAGE","android.permission.WRITE_EXTERNAL_STORAGE");
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

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        initFragment();
        hideFragments(transaction);
        transaction.show(mainFragment);
        transaction.commitAllowingStateLoss();
    }

    private void initListener() {
        drawerListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (position != 3)
            hideFragments(transaction);
        switch (position) {
            case 0:
                title.setText(getResources().getString(R.string.main_title));
                transaction.show(mainFragment);
                break;
            case 1:
                title.setText(getResources().getString(R.string.setting_title));
                transaction.show(danmuSettingFragment);
                break;
            case 2:
                title.setText(getResources().getString(R.string.path_title));
                transaction.show(danmuPathFragment);
                break;
            case 3:
                deleteData();
                break;
            case 5:
                title.setText(getResources().getString(R.string.intro_title));
                introFragment.setReadType(AboutFragment.READ_INTRO);
                transaction.show(introFragment);
                break;
            case 6:
                title.setText(getResources().getString(R.string.about_title));
                aboutFragment.setReadType(AboutFragment.READ_ABOUT);
                transaction.show(aboutFragment);
                break;
        }
        transaction.commitAllowingStateLoss();
        mDrawerLayout.closeDrawers();
    }

    private void initFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mainFragment = MainFragment.newInstance();
        danmuSettingFragment = DanmuSettingFragment.newInstance();
        danmuPathFragment = DanmuPathFragment.newInstance();
        introFragment = AboutFragment.newInstance();
        aboutFragment = AboutFragment.newInstance();
        transaction.add(R.id.content_view, mainFragment);
        transaction.add(R.id.content_view, danmuSettingFragment);
        transaction.add(R.id.content_view, danmuPathFragment);
        transaction.add(R.id.content_view, introFragment);
        transaction.add(R.id.content_view, aboutFragment);
        transaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (mainFragment != null){
            transaction.hide(mainFragment);
        }
        if (danmuSettingFragment != null){
            transaction.hide(danmuSettingFragment);
        }
        if (danmuPathFragment != null){
            transaction.hide(danmuPathFragment);
        }
        if (aboutFragment != null){
            transaction.hide(aboutFragment);
        }
        if (introFragment != null){
            transaction.hide(introFragment);
        }
    }

    /**
     * 删除缓存
     */
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
