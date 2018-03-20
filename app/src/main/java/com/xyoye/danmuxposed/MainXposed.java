package com.xyoye.danmuxposed;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.xyoye.danmuxposed.listener.PlayListener;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by xyy on 2018-03-16 上午 10:59
 */

public class MainXposed implements IXposedHookLoadPackage {
    private PlayListener listener;

    public MainXposed(PlayListener listener){
        this.listener = listener;
    }
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        XposedBridge.log("DanmuXposed start...");
        if ("com.mxtech.videoplayer.pro".equals(loadPackageParam.packageName)){
            //new PackageHooker(loadPackageParam);
            XposedBridge.log("oh! mx_player is find...");
            try{
                //开始
                findAndHookMethod("com.mxtech.media.FFPlayer", loadPackageParam.classLoader, "_start", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("video start");
                        System.out.println("video start");
                        listener.start();
                    }
                });
                //暂停
                findAndHookMethod("com.mxtech.media.FFPlayer", loadPackageParam.classLoader, "_pause", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("video pause");
                        System.out.println("video pause");
                        listener.pause();
                    }
                });
                //总长度
                findAndHookMethod("com.mxtech.media.FFPlayer", loadPackageParam.classLoader, "duration", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("video duration："+param.getResult());
                        System.out.println("video duration："+param.getResult());
                        listener.duration((int)param.getResult());
                    }
                });
                //进度
                findAndHookMethod("com.mxtech.media.FFPlayer", loadPackageParam.classLoader, "_seekTo",int.class,int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("video seekTo："+param.args[0]+","+param.args[1]);
                        System.out.println("video seekTo："+param.args[0]+","+param.args[1]);
                        listener.seekTo((int)param.args[0]);
                    }
                });

                //设置播放速度
                findAndHookMethod("com.mxtech.media.FFPlayer", loadPackageParam.classLoader, "setSpeed_",double.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        System.out.println("video setSpeed："+param.args[0]);
                        XposedBridge.log("video setSpeed："+param.args[0]);
                        listener.setSpeed((int)param.args[0]);
                    }
                });

                //获取视频标题
                XposedHelpers.findAndHookMethod("com.mxtech.media.FFPlayer", loadPackageParam.classLoader, "setDataSource",Context.class,String.class,String.class,String.class,String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        System.out.println("video title："+param.args[1]);
                        XposedBridge.log("video title："+param.args[1]);
                        listener.setTitle((String)param.args[1]);
                    }
                });
            }catch (Exception e){
                XposedBridge.log("获取video信息出错"+e);
                System.out.println("获取video信息出错"+e);
            }
        }
    }
}
