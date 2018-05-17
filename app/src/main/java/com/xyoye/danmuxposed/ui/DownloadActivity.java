package com.xyoye.danmuxposed.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xyoye.danmuxposed.R;
import com.xyoye.danmuxposed.weight.DownloadDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xyy on 2018/5/14.
 */

public class DownloadActivity extends AppCompatActivity implements View.OnClickListener{
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.download_by_av)
    TextView downloadAvTv;
    @BindView(R.id.download_by_url)
    TextView downloadUrlTv;
    @BindView(R.id.select_url_bt)
    TextView selectUrlBt;
    @BindView(R.id.av_input_et)
    EditText avInputEt;
    @BindView(R.id.url_input_et)
    EditText urlInputEt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);

        title.setText("下载弹幕");
        setSupportActionBar(toolbar);
        ActionBar actionBar =  getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        initListener();
    }

    private void initListener(){
        downloadAvTv.setOnClickListener(this);
        downloadUrlTv.setOnClickListener(this);
        selectUrlBt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.download_by_av:
                String avNumber = avInputEt.getText().toString();
                DownloadDialog downloadByAvDialog = new DownloadDialog(DownloadActivity.this, R.style.Dialog_Et, avNumber, "av");
                downloadByAvDialog.show();
            case R.id.download_by_url:
                String urlLink = urlInputEt.getText().toString();
                DownloadDialog downloadByUrlDialog = new DownloadDialog(DownloadActivity.this, R.style.Dialog_Et, urlLink, "url");
                downloadByUrlDialog.show();
                break;
            case R.id.select_url_bt:

                break;
        }
    }
}
