package com.xyoye.danmuxposed.xposed;

/**
 * Created by xyy on 2018-03-16 下午 3:52
 */


import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * @author DX
 * 注意：该类不要自己写构造方法，否者可能会hook不成功
 * 开发Xposed模块完成以后，建议修改xposed_init文件，并将起指向这个类,以提升性能
 * 所以这个类需要implements IXposedHookLoadPackage,以防修改xposed_init文件后忘记
 * Created by DX on 2017/10/4.
 */

public class HookLogic implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        XposedBridge.log("----------DanmuXposed start");
        if ("com.mxtech.videoplayer.pro".equals(loadPackageParam.packageName)){
            XposedBridge.log("----------oh! mxplayer was find\n\n\n");
            try{
                //开始
                findAndHookMethod("com.mxtech.media.FFPlayer", loadPackageParam.classLoader, "_start", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("video start");
                        System.out.println("video start");
                    }
                });
                //暂停
                findAndHookMethod("com.mxtech.media.FFPlayer", loadPackageParam.classLoader, "_pause", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("video pause");
                        System.out.println("video pause");
                    }
                });
                //进度
                findAndHookMethod("com.mxtech.media.FFPlayer", loadPackageParam.classLoader, "duration", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("video duration："+param.getResult());
                        System.out.println("video duration："+param.getResult());
                    }
                });
                //跳转至
                findAndHookMethod("com.mxtech.media.FFPlayer", loadPackageParam.classLoader, "_seekTo",int.class,int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("video seekTo："+param.args[0]+","+param.args[1]);
                        System.out.println("video seekTo："+param.args[0]+","+param.args[1]);
                    }
                });
                //设置播放速度
                findAndHookMethod("com.mxtech.media.FFPlayer", loadPackageParam.classLoader, "setSpeed_",double.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        System.out.println("video setSpeed："+param.args[0]);
                        XposedBridge.log("video setSpeed："+param.args[0]);
                    }
                });
            }catch (Exception e){
                XposedBridge.log("获取video信息出错"+e);
                System.out.println("获取video信息出错"+e);
            }
        }
    }
}