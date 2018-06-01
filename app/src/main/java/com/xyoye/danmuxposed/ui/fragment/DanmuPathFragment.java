package com.xyoye.danmuxposed.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xyoye.danmuxposed.R;
import com.xyoye.danmuxposed.database.SharedPreferencesHelper;
import com.xyoye.danmuxposed.ui.activities.DownloadActivity;
import com.xyoye.danmuxposed.ui.activities.FolderChooserActivity;
import com.xyoye.danmuxposed.utils.DanmuConfig;
import com.xyoye.danmuxposed.utils.ToastUtil;
import com.xyoye.danmuxposed.utils.permissionchecker.PermissionHelper;

import butterknife.BindView;

import static android.app.Activity.RESULT_OK;
import static com.xyoye.danmuxposed.utils.DanmuConfig.DEFAULT_FOLDER;
import static com.xyoye.danmuxposed.utils.DanmuConfig.FILE;
import static com.xyoye.danmuxposed.utils.DanmuConfig.FOLDER;
import static com.xyoye.danmuxposed.utils.DanmuConfig.READ_FILE_PATH_KEY;
import static com.xyoye.danmuxposed.utils.DanmuConfig.READ_FILE_TYPE_KEY;
import static com.xyoye.danmuxposed.utils.DanmuConfig.READ_FOLDER_PATH_KEY;

/**
 * Created by YE on 2018/5/30.
 */


public class DanmuPathFragment extends BaseFragment implements View.OnClickListener{
    private final static int SELECT_FOLDER = 1;
    private final static int SELECT_FILE = 0;

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
    @BindView(R.id.download_danmu)
    Button downloadDanmu;

    private int read_file_type;
    private String read_file_path;
    private String read_folder_path;

    private SharedPreferencesHelper preferencesHelper;

    public static DanmuPathFragment newInstance(){
        return new DanmuPathFragment();
    }

    @Override
    protected int initLayoutId() {
        return R.layout.fragment_path_setting;
    }

    @Override
    protected void initData() {
        preferencesHelper = SharedPreferencesHelper.getInstance();
    }

    @Override
    protected void initView() {
        read_file_type = preferencesHelper.getInteger(READ_FILE_TYPE_KEY,FOLDER);
        read_file_path = preferencesHelper.getString(READ_FILE_PATH_KEY,"");
        read_folder_path = preferencesHelper.getString(READ_FOLDER_PATH_KEY,DEFAULT_FOLDER);

        folderPathTv.setText(read_folder_path);
        filePathTv.setText(read_file_path);
        if (read_file_type == FILE){
            readFileRadio.setChecked(true);
            filePathTv.setVisibility(View.VISIBLE);
            changeFilePathTv.setVisibility(View.VISIBLE);
            changeFolderPathTv.setVisibility(View.GONE);
            folderPathTv.setVisibility(View.GONE);
            downloadDanmu.setVisibility(View.GONE);
        }else {
            readFolderRadio.setChecked(true);
            filePathTv.setVisibility(View.GONE);
            changeFilePathTv.setVisibility(View.GONE);
            changeFolderPathTv.setVisibility(View.VISIBLE);
            folderPathTv.setVisibility(View.VISIBLE);
            downloadDanmu.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initListener() {
        changeFilePathTv.setOnClickListener(this);
        changeFolderPathTv.setOnClickListener(this);
        pathSettingConfirmBt.setOnClickListener(this);
        downloadDanmu.setOnClickListener(this);

        readTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.read_file_radio:
                        read_file_type = FILE;
                        filePathTv.setVisibility(View.VISIBLE);
                        changeFilePathTv.setVisibility(View.VISIBLE);
                        changeFolderPathTv.setVisibility(View.GONE);
                        folderPathTv.setVisibility(View.GONE);
                        changeFilePathTv.setTextColor(Color.parseColor("#33b5e5"));
                        break;
                    case R.id.read_folder_radio:
                        read_file_type = FOLDER;
                        filePathTv.setVisibility(View.GONE);
                        changeFilePathTv.setVisibility(View.GONE);
                        changeFolderPathTv.setVisibility(View.VISIBLE);
                        folderPathTv.setVisibility(View.VISIBLE);
                        changeFolderPathTv.setTextColor(Color.parseColor("#33b5e5"));
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.change_file_path:
                selectPath(false);
                break;
            case R.id.change_folder_path:
                selectPath(true);
                break;
            case R.id.path_setting_update:
                preferencesHelper.saveInteger(READ_FILE_TYPE_KEY,read_file_type);
                preferencesHelper.saveString(READ_FILE_PATH_KEY,read_file_path);
                preferencesHelper.saveString(READ_FOLDER_PATH_KEY,read_folder_path);
                downloadDanmu.setVisibility(read_file_type == FOLDER ? View.VISIBLE : View.GONE);
                ToastUtil.showToast(getContext(),"保存成功！");
                break;
            case R.id.download_danmu:
                String path = preferencesHelper.getString(DanmuConfig.READ_FOLDER_PATH_KEY,DEFAULT_FOLDER);
                int type = preferencesHelper.getInteger(DanmuConfig.READ_FILE_TYPE_KEY,FOLDER);
                if (!path.isEmpty() && type == FOLDER){
                    Intent downloadIntent = new Intent(getActivity(), DownloadActivity.class);
                    startActivity(downloadIntent);
                }else {
                    ToastUtil.showToast(getActivity(), "请选择从文件夹读取弹幕");
                }
                break;
        }
    }

    /**
     * 选择读取弹幕路径
     */
    private void selectPath(final boolean folder){
        new PermissionHelper().with(this).request(new PermissionHelper.OnSuccessListener() {
            @Override
            public void onPermissionSuccess() {
                Intent intent = new Intent(getActivity(), FolderChooserActivity.class);
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
            }
        },"android.permission.READ_EXTERNAL_STORAGE","android.permission.WRITE_EXTERNAL_STORAGE");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

}
