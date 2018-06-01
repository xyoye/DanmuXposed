package com.xyoye.danmuxposed.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xyoye.danmuxposed.R;
import com.xyoye.danmuxposed.utils.DownloadUtil;
import com.xyoye.danmuxposed.utils.ToastUtil;
import com.xyoye.danmuxposed.ui.weight.DownloadDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xyy on 2018/5/14.
 */

public class DownloadActivity extends AppCompatActivity implements View.OnClickListener{
    public final static int SELECT_WEB = 101;

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
                if (avNumber.isEmpty()){
                    ToastUtil.showToast(DownloadActivity.this, "AV号不能为空");
                }else if(!DownloadUtil.isNum(avNumber)){
                    ToastUtil.showToast(DownloadActivity.this, "请输入纯数字AV号");
                }else {
                    DownloadDialog downloadByAvDialog = new DownloadDialog(DownloadActivity.this, R.style.Dialog_Et, avNumber, "av");
                    downloadByAvDialog.show();
                }
                break;
            case R.id.download_by_url:
                String urlLink = urlInputEt.getText().toString();
                if (urlLink.isEmpty()){
                    ToastUtil.showToast(DownloadActivity.this, "视频链接不能为空");
                }else if (!DownloadUtil.isUrl(urlLink)){
                    ToastUtil.showToast(DownloadActivity.this, "请输入正确视频链接");
                }else {
                    DownloadDialog downloadByUrlDialog = new DownloadDialog(DownloadActivity.this, R.style.Dialog_Et, urlLink, "url");
                    downloadByUrlDialog.show();
                }
                break;
            case R.id.select_url_bt:
                Intent intent = new Intent(DownloadActivity.this, WebviewActivity.class);
                startActivityForResult(intent, SELECT_WEB);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_WEB && data!=null){
            String selectUrl = data.getStringExtra("selectUrl");
            if (selectUrl.isEmpty()){
                ToastUtil.showToast(DownloadActivity.this, "视频链接不能为空");
            }else if (!DownloadUtil.isUrl(selectUrl)){
                ToastUtil.showToast(DownloadActivity.this, "请输入正确视频链接");
            }else {
                DownloadDialog downloadByUrlDialog = new DownloadDialog(DownloadActivity.this, R.style.Dialog_Et, selectUrl, "url");
                downloadByUrlDialog.show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
