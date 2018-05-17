package com.xyoye.danmuxposed.weight;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.xyoye.danmuxposed.R;
import com.xyoye.danmuxposed.database.SharedPreferencesHelper;
import com.xyoye.danmuxposed.utils.DanmuConfig;
import com.xyoye.danmuxposed.utils.DownloadUtil;
import com.xyoye.danmuxposed.utils.FileUtil;
import com.xyoye.danmuxposed.utils.ToastUtil;

import org.jsoup.Jsoup;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xyy on 2018/5/17.
 */

public class DownloadDialog extends Dialog {
    @BindView(R.id.log_et)
    EditText logEt;
    @BindView(R.id.file_name_et)
    EditText fileNameEt;
    @BindView(R.id.download_start_bt)
    Button downloadStartBt;
    @BindView(R.id.download_over_bt)
    Button downloadOverBt;
    @BindView(R.id.download_more_cancel_bt)
    Button downloadMoreCancelBt;
    @BindView(R.id.download_more_confirm_bt)
    Button downloadMoreConfirmBt;
    @BindView(R.id.change_file_ll)
    LinearLayout fileNameLayout;

    private String keyWord;
    private String type;
    private Context context;
    private String cid;
    private String fileName;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 100:
                    logEt.append((String)msg.obj);
                    break;
                case 101:
                    fileNameEt.setEnabled(true);
                    fileNameEt.setText(cid);
                    downloadStartBt.setEnabled(true);
                    downloadStartBt.setText("开始下载");
                    break;
                case 102:
                    downloadStartBt.setVisibility(View.GONE);
                    downloadOverBt.setVisibility(View.VISIBLE);
                    break;
                case 103:
                    fileNameLayout.setVisibility(View.GONE);
                    downloadStartBt.setEnabled(false);
                    downloadStartBt.setText("正在下载 …");
                    break;
            }
            return false;
        }
    });

    public DownloadDialog(@NonNull Context context, int themeResId ,String keyWord, String type) {
        super(context, themeResId);
        this.context = context;
        this.keyWord = keyWord;
        this.type = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_download);
        ButterKnife.bind(this, this);

        logEt.setFocusable(false);
        logEt.setFocusableInTouchMode(false);
        fileNameEt.setEnabled(false);
        downloadStartBt.setText("正在准备 …");
        downloadStartBt.setEnabled(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if ("url".equals(type))
                        downloadByUrl(keyWord);
                    else
                        downloadByAv(keyWord);
                } catch (IOException e) {
                    ToastUtil.showToast(context,"错误的视频链接");
                    e.printStackTrace();
                }
            }
        }).start();

        initListener();
    }

    private void initListener(){
        downloadStartBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileName = fileNameEt.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(103);
                        downloadDanmu();
                    }
                }).start();
            }
        });

        downloadOverBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadDialog.this.cancel();
            }
        });
    }

    private void downloadByUrl(String url) throws IOException {
        if (!url.isEmpty()){

            sendHandlerMessage("开始连接URL...\n");
            String root = Jsoup.connect(url).timeout(10000).get().toString();
            sendHandlerMessage("连接URL成功\n");

            if(url.contains("www.bilibili.com/video")){

                sendHandlerMessage("开始获取cid...\n");
                int start = root.indexOf("\\\"cid=")+6;
                int end = root.indexOf("&aid");
                cid = root.substring(start, end);
                sendHandlerMessage("获取cid成功\n");

                handler.sendEmptyMessage(101);

            }else if (url.contains("www.bilibili.com/bangumi")){

            }else {
                ToastUtil.showToast(context,"错误的视频链接");
            }
        }else {
            ToastUtil.showToast(context,"请输入视频链接");
        }
    }

    private void downloadByAv(String avNumber) throws IOException{
        if (!avNumber.isEmpty()){

        }else {
            ToastUtil.showToast(context,"请输入av号");
        }
    }

    private void downloadDanmu(){
        String path = SharedPreferencesHelper.getInstance().getString(DanmuConfig.READ_FOLDER_PATH_KEY,"");

        sendHandlerMessage("开始下载弹幕文件...\n");
        String xmlContent = DownloadUtil.getXmlString(cid);
        if (xmlContent == null){
            sendHandlerMessage("弹幕文件下载失败");
            return;
        }
        sendHandlerMessage("弹幕文件下载成功\n正在写入文件...\n");

        if (fileName.isEmpty())
            fileName = cid;
        FileUtil.writeXmlFile(xmlContent, fileName, path);
        sendHandlerMessage("写入文件成功\n文件路径：\n" + path + "/" + fileName + ".xml");

        handler.sendEmptyMessage(102);
    }

    private void sendHandlerMessage(String msg){
        Message message = new Message();
        message.what = 100;
        message.obj = msg;
        handler.sendMessage(message);
    }
}
