package com.xyoye.danmuxposed.xposed;

import android.content.Context;
import android.content.Intent;

import com.xyoye.danmuxposed.bean.Event;

import org.greenrobot.eventbus.EventBus;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * Created by xyy on 2018-03-16 上午 10:59
 */

public class MainXposed implements IXposedHookLoadPackage {
    private Context context;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if ("com.mxtech.videoplayer.pro".equals(loadPackageParam.packageName) ||
                "com.mxtech.videoplayer".equals(loadPackageParam.packageName) ){
            onMXStart(loadPackageParam);
            try{
                //开始
                findAndHookMethod("com.mxtech.media.FFPlayer", loadPackageParam.classLoader, "_start", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("mx video start");
                        System.out.println("mx video start");

                        Intent intent = new Intent();
                        intent.setAction(Event.EVENT_START);
                        context.sendBroadcast(intent);
                    }
                });
                //暂停
                findAndHookMethod("com.mxtech.media.FFPlayer", loadPackageParam.classLoader, "_pause", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("video pause");
                        System.out.println("video pause");

                        Intent intent = new Intent();
                        intent.setAction(Event.EVENT_PAUSE);
                        context.sendBroadcast(intent);
                    }
                });
                //总长度
                findAndHookMethod("com.mxtech.media.FFPlayer", loadPackageParam.classLoader, "duration", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("video duration："+param.getResult());
                        System.out.println("video duration："+param.getResult());
                        int duration = (int)param.getResult();

                        Intent intent = new Intent();
                        intent.putExtra(Event.EVENT_DURATION,duration);
                        intent.setAction(Event.EVENT_DURATION);
                        context.sendBroadcast(intent);
                    }
                });
                //进度
                findAndHookMethod("com.mxtech.media.FFPlayer", loadPackageParam.classLoader, "_seekTo",int.class,int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("video seekTo："+param.args[0]+","+param.args[1]);
                        System.out.println("video seekTo："+param.args[0]+","+param.args[1]);

                        int progress = (int)param.args[0];
                        Intent intent = new Intent();
                        intent.putExtra(Event.EVENT_PROGRESS,progress);
                        intent.setAction(Event.EVENT_PROGRESS);
                        context.sendBroadcast(intent);
                    }
                });

                //设置播放速度
                findAndHookMethod("com.mxtech.media.FFPlayer", loadPackageParam.classLoader, "setSpeed_",double.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        System.out.println("video setSpeed："+param.args[0]);
                        XposedBridge.log("video setSpeed："+param.args[0]);

                        int speed = (int)param.args[0];
                        Intent intent = new Intent();
                        intent.putExtra(Event.EVENT_SPEED,speed);
                        intent.setAction(Event.EVENT_SPEED);
                        context.sendBroadcast(intent);
                    }
                });

                //获取视频标题
                XposedHelpers.findAndHookMethod("com.mxtech.media.FFPlayer", loadPackageParam.classLoader, "setDataSource",Context.class,String.class,String.class,String.class,String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        System.out.println("video title："+param.args[1]);
                        XposedBridge.log("video title："+param.args[1]);

                        String title = (String)param.args[1];
                        Intent intent = new Intent();
                        intent.putExtra(Event.EVENT_TITLE,title);
                        intent.setAction(Event.EVENT_TITLE);
                        context.sendBroadcast(intent);
                    }
                });
            }catch (Exception e){
                XposedBridge.log("----------获取video信息出错----------"+e);
                XposedBridge.log(e);
                System.out.println("----------获取video信息出错----------"+e);
                e.printStackTrace();
            }
        }
    }

    private void onMXStart(XC_LoadPackage.LoadPackageParam loadPackageParam){
        EventBus.getDefault().post(new Event(Event.EVENT_MX_START,true));
        XposedBridge.log("----------MxPlayer切换至前台----------");
        System.out.println("----------MxPlayer切换至前台----------");

        try  {
            Class<?>  ContextClass  =  findClass("android.content.ContextWrapper",  loadPackageParam.classLoader);
            findAndHookMethod(ContextClass,  "getApplicationContext",  new  XC_MethodHook()  {
                @Override
                protected  void  afterHookedMethod(MethodHookParam  param)  throws  Throwable  {
                    super.afterHookedMethod(param);
                    if (context  !=  null)
                        return;
                    context  =  (Context)  param.getResult();
                    XposedBridge.log("----------获取MxPlayer上下文成功----------");
                    System.out.println("----------获取MxPlayer上下文成功----------");

                    Intent intent = new Intent();
                    intent.setAction(Event.EVENT_MX_START);
                    context.sendBroadcast(intent);
                }
            });
        }  catch  (Throwable  t)  {
            XposedBridge.log("----------获取MxPlayer上下文出错----------");
            XposedBridge.log(t);
            System.out.println("----------获取MxPlayer上下文出错----------");
            t.printStackTrace();
        }
    }
}