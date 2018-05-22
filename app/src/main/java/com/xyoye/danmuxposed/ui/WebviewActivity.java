package com.xyoye.danmuxposed.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.xyoye.danmuxposed.R;
import com.xyoye.danmuxposed.weight.IWebView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xyy on 2018/5/22.
 */

public class WebviewActivity extends AppCompatActivity {
    @BindView(R.id.i_webview)
    IWebView IwebView;
    @BindView(R.id.toolbar)
    android.support.v7.widget.Toolbar toolbar;
    @BindView(R.id.title)
    TextView toolbarTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar =  getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        toolbarTitle.setText("选择链接");
        IwebView.loadUrl("http://www.bilibili.com");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.select_web:
                Intent intent = getIntent();
                intent.putExtra("selectUrl", IwebView.getUrl());
                setResult(DownloadActivity.SELECT_WEB, intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        IwebView.onPause();
        IwebView.pauseTimers();
        super.onPause();
    }

    @Override
    protected void onResume() {
        IwebView.onResume();
        IwebView.resumeTimers();
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && IwebView.canGoBack()) {
            IwebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
