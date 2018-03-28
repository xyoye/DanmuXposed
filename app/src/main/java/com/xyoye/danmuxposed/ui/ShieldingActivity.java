package com.xyoye.danmuxposed.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.donkingliang.labels.LabelsView;
import com.xyoye.danmuxposed.R;
import com.xyoye.danmuxposed.bean.Event;
import com.xyoye.danmuxposed.database.DatabaseDao;
import com.xyoye.danmuxposed.weight.SubmitButton;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xyy on 2018-03-22 上午 10:17
 */

public class ShieldingActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.shielding_labels)
    LabelsView shieldingLabels;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.input_shield_et)
    EditText shieldEt;
    @BindView(R.id.add_shield_bt)
    Button shieldBt;

    private DatabaseDao databaseDao;
    private List<String> shieldList;
    AlertDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shielding);
        ButterKnife.bind(this);

        title.setText("屏蔽列表");
        shieldBt.setOnClickListener(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar =  getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        databaseDao = new DatabaseDao(this);
        shieldList = databaseDao.queryAllShield();
        shieldingLabels.setLabels(shieldList);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.delete_shield_button:
                deleteShield();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shield, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_shield_bt:
                String text = shieldEt.getText().toString();
                if (!text.isEmpty()){
                    if (!databaseDao.insertShield(text)){
                        Toast.makeText(ShieldingActivity.this,"不能重复添加",Toast.LENGTH_SHORT).show();
                    }else {
                        shieldList.add(text);
                        shieldingLabels.setLabels(shieldList);
                        shieldEt.setText("");
                        EventBus.getDefault().post(new Event(Event.EVENT_DANMU_SHIELD_ADD,text));
                    }
                }
                break;
        }
    }

    private void deleteShield(){
        final AlertDialog.Builder builder_delete = new AlertDialog.Builder(ShieldingActivity.this);
        View dialogView = View.inflate(ShieldingActivity.this,R.layout.dialog_delete,null);
        final TextView deleteTv = dialogView.findViewById(R.id.delete_tv);
        final SubmitButton deleteBt = dialogView.findViewById(R.id.delete_bt);
        final List<String> selectData = shieldingLabels.getSelectLabelDatas();
        String tip = "即将删除："+selectData.size()+"个屏蔽";
        deleteTv.setText(tip);
        deleteBt.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                for (int i = 0; i < selectData.size(); i++) {
                    databaseDao.deleteShield(selectData.get(i));
                    EventBus.getDefault().post(new Event(Event.EVENT_DANMU_SHIELD_REMOVE,selectData.get(i)));
                    deleteBt.setProgress(i*100/selectData.size());
                    deleteTv.setText("成功删除："+(i+1));
                }
                deleteBt.doResult(true);
                shieldList.clear();
                shieldList = databaseDao.queryAllShield();
                shieldingLabels.setLabels(shieldList);
            }
        });
        builder_delete.setView(dialogView);
        if (selectData.size() > 0)
            dialog = builder_delete.show();
    }
}
