package com.xyoye.danmuxposed;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xyoye.danmuxposed.adapter.DrawerAdapter;
import com.xyoye.danmuxposed.service.DanmuService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener{
    private static final int READ_EXTERNAL_STORAGE = 101;
    private boolean readFilePermission = false;
    private boolean danmuStart = false;
    private List<String> drawerText;
    private List<Integer> drawerImage;

    private Button danmuSwitch;
    private Toolbar toolbar;
    private TextView title;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView drawerListView;
    private DrawerAdapter drawerAdapter;

    private RelativeLayout layout1;
    private RelativeLayout layout2;
    private RelativeLayout layout3;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();

        initView();

        initListener();

        initOther();
    }

    private void initData(){
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
        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);

        mDrawerLayout =  findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.title);
        danmuSwitch = findViewById(R.id.danmu_switch);
        drawerListView =  findViewById(R.id.drawer_list_view);

        title.setText("弹幕播放器");
        setSupportActionBar(toolbar);
        ActionBar actionBar =  getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close) {
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
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        drawerAdapter = new DrawerAdapter(drawerText,drawerImage,this);
        drawerListView.setAdapter(drawerAdapter);
    }

    private void initListener(){
        danmuSwitch.setOnClickListener(this);
        drawerListView.setOnItemClickListener(this);
    }

    private void initOther(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_EXTERNAL_STORAGE);
        }else {
            readFilePermission = true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            mDrawerLayout.openDrawer(GravityCompat.START);//打开侧滑菜单
            return true ;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.danmu_switch:
                //floatViewSwitch();
                Intent intent = new Intent(MainActivity.this,ShieldingActivity.class);
                startActivity(intent);
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
                Toast.makeText(MainActivity.this,"读取弹幕文件权限被拒绝",Toast.LENGTH_LONG).show();
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
                break;
            case 1:
                title.setText("弹幕设置");
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
                layout3.setVisibility(View.GONE);
                mDrawerLayout.closeDrawers();
                break;
            case 2:
                title.setText("路径设置");
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.GONE);
                layout3.setVisibility(View.VISIBLE);
                mDrawerLayout.closeDrawers();
                break;
        }
    }
}
