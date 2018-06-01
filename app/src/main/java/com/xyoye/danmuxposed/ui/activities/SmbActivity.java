package com.xyoye.danmuxposed.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xyoye.danmuxposed.R;
import com.xyoye.danmuxposed.ui.adapter.SmbAdapter;
import com.xyoye.danmuxposed.bean.SmbInfo;
import com.xyoye.danmuxposed.utils.HorizontalDividerItemDecoration;
import com.xyoye.danmuxposed.utils.SmbUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SmbActivity extends AppCompatActivity {
    private final static int ERROR_CODE = 101;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.save_path)
    TextView savePath;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.loading_view)
    LinearLayout loading_view;

    private String smbUrl = "";
    private String baseUrl = "";

    private SmbAdapter mAdapter;
    private List<SmbInfo> mData;

    private List<SmbInfo> parentContents;
    private boolean canGoUp = false;

    private ExecutorService singleThreadExecutor;
    SmbInfo smbInfo;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    savePath.setText(smbUrl);
                    mData.clear();
                    mData.addAll(getContentsArray());
                    mAdapter.notifyDataSetChanged();

                    loading_view.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    break;
                case ERROR_CODE:
                    String mess = (String)msg.obj;
                    Toast.makeText(SmbActivity.this, mess, Toast.LENGTH_SHORT).show();
                    loading_view.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smb_folder_chooser);
        ButterKnife.bind(this);

        initData();
        
        initView();
        
        setData();
    }


    private void initData(){
        mData = new ArrayList<>();

        baseUrl = getIntent().getStringExtra("smbUrl");
        smbUrl = baseUrl;
        singleThreadExecutor = Executors.newSingleThreadExecutor();

        smbInfo = new SmbInfo();
    }
    
    private void initView() {
        setSupportActionBar(toolbar);
        ActionBar actionBar =  getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        title.setText("请选择文件");
        
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(0xFFE9E7E8)
                .size(1)
                .build());
        mAdapter = new SmbAdapter(this, mData, new ItemClickCallback() {
            @Override
            public void onClick(View view, int position, SmbInfo info) {
                onSelection(position, info);
            }
        });
        recyclerView.setAdapter(mAdapter);
        savePath.setText(smbUrl);
    }

    private void setData(){
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                parentContents = listFiles();
                handler.sendEmptyMessage(0);
            }
        });
    }

    private List<SmbInfo> getContentsArray() {
        List<SmbInfo> results = new ArrayList<>();
        if (parentContents == null) {
            if (canGoUp){
                SmbInfo info = new SmbInfo();
                info.setName("...");
                info.setDirectory(false);
                info.setImage(R.mipmap.back);
                results.add(info);
            }
            return results;
        }
        if (canGoUp){
            SmbInfo info = new SmbInfo();
            info.setName("...");
            info.setDirectory(false);
            info.setImage(R.mipmap.back);
            results.add(info);
        }
        results.addAll(parentContents);
        return results;
    }

    public void onSelection(int position, SmbInfo info) {
        if (canGoUp && position == 0) {
            if (!info.isDirectory()) {
                smbUrl = smbUrl.substring(0,smbUrl.length()-1);
                smbUrl = smbUrl.substring(0,smbUrl.lastIndexOf("/")+1);
            }
            if (smbUrl.equals(baseUrl)){
                canGoUp = false;
            }
            setData();
        }else if (!info.isDirectory()) {
            ChooserEnd(position);
        }else{
            smbUrl = smbUrl + info.getName()+"/";
            canGoUp = true;
            loading_view.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            setData();
        }
    }

    private List<SmbInfo> listFiles() {
        List<SmbInfo> results = new SmbUtil().getFileNamesFromSmb(smbUrl);
        Message message = new Message();
        message.what = ERROR_CODE;
        if (results == null) {
            results = new ArrayList<>();
            message.obj = "连接失败...";
            handler.sendMessage(message);
        } else if (results.size() == 0) {
            message.obj = "空文件夹";
            handler.sendMessage(message);
        }else {
            Collections.sort(results, new FileSorter());
        }
        return results;
    }

    private static class FileSorter implements Comparator<SmbInfo> {
        @Override
        public int compare(SmbInfo lhs, SmbInfo rhs) {
            if (lhs.isDirectory() && !rhs.isDirectory()) {
                return -1;
            } else if (!lhs.isDirectory() && rhs.isDirectory()) {
                return 1;
            } else {
                return lhs.getName().compareTo(rhs.getName());
            }
        }
    }

    public interface ItemClickCallback{
        void onClick(View view, int position, SmbInfo info);
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

    /**
     * 选择完毕，返回本地缓存文件路径
     */
    private void ChooserEnd(int position){
        final String selectUrl = smbUrl + mData.get(position).getName() + "/";
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = ERROR_CODE;

                String localPath = new SmbUtil().loadFromSmb(selectUrl);
                if ("超过5M".equals(localPath)){
                    message.obj = "错误，文件大小超过5M";
                    handler.sendMessage(message);
                }else if ("内存不足".equals(localPath)){
                    message.obj = "错误，可用内存小于5M";
                    handler.sendMessage(message);
                }else {
                    Intent intent = new Intent();
                    intent.putExtra("file_path", localPath);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }
}
