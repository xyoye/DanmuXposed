package com.xyoye.danmuxposed;

import android.util.Log;
import android.widget.TextView;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by xyy on 2018-03-16 上午 10:59
 */

public class Main implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("----------------------myXposed");
        XposedBridge.log("开始执行danmuXposed");
        if (lpparam.packageName.equals("com.example.administrator.testxposed")){
            XposedHelpers.findAndHookMethod(TextView.class,"setText",CharSequence.class,
                    new XC_MethodHook(){
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log("开始处理setText");
                            param.args[0] = "xposed修改后的内容";
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log("处理完成setText");
                        }
                    }

            );
        }
        if (lpparam.packageName.equals("com.mxtech.videoplayer.pro")){
            try {
                XposedBridge.log(lpparam.packageName);
                XposedBridge.log(lpparam.appInfo.className);
                XposedBridge.log(lpparam.toString());
            }catch (Exception e){
                XposedBridge.log(e);
            }
            Class<?>[] classes = XposedHelpers.getClassesAsArray();
            for (Class<?> c:classes){
                XposedBridge.log(c.getName());
            }

            Class a = XposedHelpers.findClass("PlayService", new ClassLoader() {
                @Override
                public Class<?> loadClass(String name) throws ClassNotFoundException {
                    return super.loadClass(name);
                }
            });
            XposedHelpers.findAndHookMethod(Log.class,"onStartCommand",null,
                    new XC_MethodHook(){
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            for (Object obj:param.args){
                                XposedBridge.log(obj.toString());
                            }
                            XposedBridge.log(param.method.getName());
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            XposedBridge.log("处理完成setText");
                        }
                    }

            );
        }
    }
}
