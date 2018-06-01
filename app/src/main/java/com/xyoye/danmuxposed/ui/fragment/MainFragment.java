package com.xyoye.danmuxposed.ui.fragment;

import android.app.ActivityManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;

import com.xyoye.danmuxposed.R;
import com.xyoye.danmuxposed.service.DanmuService;
import com.xyoye.danmuxposed.ui.weight.CircleImageView;
import com.xyoye.danmuxposed.utils.ToastUtil;
import com.xyoye.danmuxposed.utils.permissionchecker.PermissionHelper;

import butterknife.BindView;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by YE on 2018/5/30.
 */

public class MainFragment extends BaseFragment{
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;
    @BindView(R.id.main_switch)
    CircleImageView main_switch;

    private Animation rotateAnim;
    private boolean danmuStart = false;

    public static MainFragment newInstance(){
        return new MainFragment();
    }

    @Override
    protected int initLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initView(){
        main_switch.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1){
                    if (!Settings.canDrawOverlays(getContext())) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getContext().getPackageName()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                    } else {
                        floatViewSwitch();
                    }
                }else {
                    floatViewSwitch();
                }
            }
        });
    }

    @Override
    protected void initData() {
        danmuStart = false;
        rotateAnim = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_image);
        LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
        rotateAnim.setInterpolator(lin);
        main_switch.setAnimation(rotateAnim);
        main_switch.setImageResource(R.mipmap.earth_gary);
        rotateAnim.cancel();

        ActivityManager manager = (ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.xyoye.danmuxposed.service.DanmuService".equals(service.service.getClassName())) {
                main_switch.setImageResource(R.mipmap.earth_colour);
                rotateAnim.start();
                danmuStart = true;
            }
        }
    }

    /**
     * 打开监听
     */
    private void floatViewSwitch(){
        new PermissionHelper().with(this).request(new PermissionHelper.OnSuccessListener() {
            @Override
            public void onPermissionSuccess() {
                if (!danmuStart){
                    danmuStart = true;
                    Intent intent = new Intent(getContext(), DanmuService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        getActivity().startForegroundService(intent);
                    }else {
                        getActivity().startService(intent);
                    }
                    main_switch.setImageResource(R.mipmap.earth_colour);
                    rotateAnim.start();
                    getActivity().finish();
                }else{
                    danmuStart = false;
                    Intent intent = new Intent(getContext(), DanmuService.class);
                    getActivity().stopService(intent);
                    main_switch.setImageResource(R.mipmap.earth_gary);
                    rotateAnim.cancel();
                }
            }
        },"android.permission.READ_EXTERNAL_STORAGE","android.permission.WRITE_EXTERNAL_STORAGE");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (danmuStart){
            main_switch.setAnimation(rotateAnim);
            rotateAnim.start();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(getContext())) {
                ToastUtil.showToast(getContext(),"请授予APP权限，否则程序将不能正常运行");
            }
        }
    }
}
