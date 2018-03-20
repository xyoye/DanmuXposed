package com.xyoye.danmuxposed;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int READ_EXTERNAL_STORAGE = 101;

    private Button danmuSwitch;

    private boolean readFilePermission = false;
    private boolean danmuStart = false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        danmuSwitch = findViewById(R.id.float_view_controller);
        danmuSwitch.setOnClickListener(this);

        //申请权限
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_EXTERNAL_STORAGE);
        }else {
            readFilePermission = true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.float_view_controller:
                floatViewSwitch();
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
}
