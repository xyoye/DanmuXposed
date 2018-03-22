package com.xyoye.danmuxposed;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.donkingliang.labels.LabelsView;

import java.util.ArrayList;

/**
 * Created by xyy on 2018-03-22 上午 10:17
 */

public class ShieldingActivity extends AppCompatActivity{
    LabelsView shieldingLabels;
    Toolbar toolbar;
    private TextView title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shielding);

        shieldingLabels = findViewById(R.id.shielding_labels);
        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.title);

        title.setText("屏蔽列表");
        setSupportActionBar(toolbar);
        ActionBar actionBar =  getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        ArrayList<String> label = new ArrayList<>();
        label.add("Android");
        label.add("IOS");
        label.add("前端");
        label.add("后台");
        label.add("微信开发游戏开发游戏开发游戏开发游戏开发游戏开发游戏开发游戏开发游戏开发游戏开发游戏开发游戏开发游戏开发");
        label.add("游戏开发");
        shieldingLabels.setLabels(label);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
