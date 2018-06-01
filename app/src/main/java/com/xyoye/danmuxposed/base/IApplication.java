package com.xyoye.danmuxposed.base;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.xyoye.danmuxposed.utils.CrashHandler;

/**
 * Created by YE on 2018/5/31.
 */


public class IApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        //开启崩溃信息收集
        String crashPath = Environment.getExternalStorageDirectory() + "/DanmuXposed/crash_log/";
        CrashHandler catchHandler = CrashHandler.getInstance();
        catchHandler.init(getApplicationContext(), crashPath);
        context = getApplicationContext();
    }

    public static Context getContextObject(){
        return context;
    }
}
