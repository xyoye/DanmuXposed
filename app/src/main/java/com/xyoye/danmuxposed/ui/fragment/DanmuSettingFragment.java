package com.xyoye.danmuxposed.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xyoye.danmuxposed.R;
import com.xyoye.danmuxposed.bean.Event;
import com.xyoye.danmuxposed.database.DatabaseDao;
import com.xyoye.danmuxposed.database.SharedPreferencesHelper;
import com.xyoye.danmuxposed.ui.activities.ShieldingActivity;
import com.xyoye.danmuxposed.utils.ToastUtil;
import com.xyoye.danmuxposed.ui.weight.AmountView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

import static com.xyoye.danmuxposed.utils.DanmuConfig.BUTTON_DANMU_KEY;
import static com.xyoye.danmuxposed.utils.DanmuConfig.DANMU_FONT_SIZE_KEY;
import static com.xyoye.danmuxposed.utils.DanmuConfig.DANMU_SPEED_KEY;
import static com.xyoye.danmuxposed.utils.DanmuConfig.MOBILE_DANMU_KEY;
import static com.xyoye.danmuxposed.utils.DanmuConfig.TOP_DANMU_KEY;

/**
 * Created by YE on 2018/5/30.
 */


public class DanmuSettingFragment extends BaseFragment implements View.OnClickListener{
    @BindView(R.id.danmu_speed_input)
    AmountView danmuSpeedInput;
    @BindView(R.id.font_size_input)
    AmountView fontSizeInput;
    @BindView(R.id.mobile_danmu_iv)
    ImageView mobileDanmuIv;
    @BindView(R.id.button_danmu_iv)
    ImageView buttonDanmuIv;
    @BindView(R.id.top_danmu_iv)
    ImageView topDanmuIv;
    @BindView(R.id.default_font_size)
    TextView defaultFontSize;
    @BindView(R.id.default_speed)
    TextView defaultSpeed;
    @BindView(R.id.shielding_activity_bt)
    Button shieldActivityBt;
    @BindView(R.id.danmu_setting_confirm)
    Button danmuSettingConfirmBt;
    @BindView(R.id.shield_number)
    TextView shieldNumberTv;

    private float danmu_speed;
    private float font_size;
    private boolean mobile_danmu;
    private boolean top_danmu;
    private boolean button_danmu;

    private SharedPreferencesHelper preferencesHelper;
    private DatabaseDao databaseDao;

    public static DanmuSettingFragment newInstance(){
        return new DanmuSettingFragment();
    }

    @Override
    protected int initLayoutId() {
        return R.layout.fragment_danmu_setting;
    }

    @Override
    protected void initData() {
        databaseDao = new DatabaseDao(getActivity());
        preferencesHelper = SharedPreferencesHelper.getInstance();
    }

    @Override
    protected void initView() {
        font_size = Float.parseFloat(preferencesHelper.getString(DANMU_FONT_SIZE_KEY,"1.0"));
        danmu_speed = Float.parseFloat(preferencesHelper.getString(DANMU_SPEED_KEY,"1.0"));
        mobile_danmu = preferencesHelper.getBoolean(MOBILE_DANMU_KEY,false);
        top_danmu = preferencesHelper.getBoolean(TOP_DANMU_KEY,false);
        button_danmu = preferencesHelper.getBoolean(BUTTON_DANMU_KEY,false);

        fontSizeInput.setValue(font_size);
        danmuSpeedInput.setValue(danmu_speed);

        int resId = mobile_danmu ? R.mipmap.moblie_danmu_checked : R.mipmap.moblie_danmu_unchecked;
        mobileDanmuIv.setImageResource(resId);
        resId = button_danmu ? R.mipmap.bottom_danmu_checked : R.mipmap.bottom_danmu_unchecked;
        buttonDanmuIv.setImageResource(resId);
        resId = top_danmu ? R.mipmap.top_danmu_checked : R.mipmap.top_danmu_unchecked;
        topDanmuIv.setImageResource(resId);

        String shieldN = databaseDao.queryAllShield().size()+"";
        shieldNumberTv.setText(shieldN);
    }

    @Override
    protected void initListener() {
        mobileDanmuIv.setOnClickListener(this);
        buttonDanmuIv.setOnClickListener(this);
        topDanmuIv.setOnClickListener(this);
        defaultFontSize.setOnClickListener(this);
        defaultSpeed.setOnClickListener(this);
        shieldActivityBt.setOnClickListener(this);
        danmuSettingConfirmBt.setOnClickListener(this);

        danmuSpeedInput.setOnAmountChangeListener(new AmountView.OnAmountChangeListener() {
            @Override
            public void onAmountChange(View view, float value) {
                danmu_speed = value;
                EventBus.getDefault().post(new Event(Event.EVENT_DANMU_SPEED,danmu_speed));
            }
        });

        fontSizeInput.setOnAmountChangeListener(new AmountView.OnAmountChangeListener() {
            @Override
            public void onAmountChange(View view, float value) {
                font_size = value;
                EventBus.getDefault().post(new Event(Event.EVENT_DANMU_SIZE,font_size));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.default_font_size:
                fontSizeInput.setValue(1.0f);
                preferencesHelper.saveString(DANMU_FONT_SIZE_KEY,"1.0");
                break;
            case R.id.default_speed:
                danmuSpeedInput.setValue(1.0f);
                preferencesHelper.saveString(DANMU_SPEED_KEY,"1.0");
                break;
            case R.id.mobile_danmu_iv:
                mobile_danmu = !mobile_danmu;
                int resId = mobile_danmu ? R.mipmap.moblie_danmu_checked : R.mipmap.moblie_danmu_unchecked;
                EventBus.getDefault().post(new Event(Event.EVENT_DANMU_MOBILE,mobile_danmu));
                mobileDanmuIv.setImageResource(resId);
                break;
            case R.id.button_danmu_iv:
                button_danmu = !button_danmu;
                resId = button_danmu ? R.mipmap.bottom_danmu_checked : R.mipmap.bottom_danmu_unchecked;
                EventBus.getDefault().post(new Event(Event.EVENT_DANMU_BUTTON,button_danmu));
                buttonDanmuIv.setImageResource(resId);
                break;
            case R.id.top_danmu_iv:
                top_danmu = !top_danmu;
                resId = top_danmu ? R.mipmap.top_danmu_checked : R.mipmap.top_danmu_unchecked;
                EventBus.getDefault().post(new Event(Event.EVENT_DANMU_TOP,top_danmu));
                topDanmuIv.setImageResource(resId);
                break;
            case R.id.shielding_activity_bt:
                Intent intent = new Intent(getActivity(),ShieldingActivity.class);
                startActivity(intent);
                break;
            case R.id.danmu_setting_confirm:
                saveInfo();
                break;
        }
    }

    public void saveInfo(){
        preferencesHelper.saveString(DANMU_FONT_SIZE_KEY,String.valueOf(font_size));
        preferencesHelper.saveString(DANMU_SPEED_KEY,String.valueOf(danmu_speed));
        preferencesHelper.saveBoolean(MOBILE_DANMU_KEY,mobile_danmu);
        preferencesHelper.saveBoolean(BUTTON_DANMU_KEY,button_danmu);
        preferencesHelper.saveBoolean(TOP_DANMU_KEY,top_danmu);
        ToastUtil.showToast(getContext(),"保存成功！");

        View view = getActivity().getCurrentFocus();
        if (view != null){
            view.clearFocus();
            InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert manager != null;
            manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String shieldN = databaseDao.queryAllShield().size()+"";
        shieldNumberTv.setText(shieldN);
    }

}
