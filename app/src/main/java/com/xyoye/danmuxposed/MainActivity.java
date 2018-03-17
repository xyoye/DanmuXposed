package com.xyoye.danmuxposed;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取启动按钮
        Button start = (Button)findViewById(R.id.start_id);
        //获取移除按钮
        Button remove = (Button)findViewById(R.id.remove_id);
        //绑定监听
        start.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this, DanmuService.class);
                //启动FxService
                startService(intent);
                finish();
            }
        });

        remove.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                //uninstallApp("com.phicomm.hu");
                Intent intent = new Intent(MainActivity.this, DanmuService.class);
                //终止FxService
                stopService(intent);
            }
        });

    }
}
