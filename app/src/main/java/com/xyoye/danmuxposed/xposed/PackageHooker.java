package com.xyoye.danmuxposed.xposed;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import dalvik.system.DexFile;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


/**
 * Created by YE on 2018/3/17.
 */


public class PackageHooker {


    private final XC_LoadPackage.LoadPackageParam loadPackageParam;


    public PackageHooker(XC_LoadPackage.LoadPackageParam param) {
        loadPackageParam = param;
        try {
            //hook();
            Class clazz = Class.forName("com.mxtech.media.FFPlayer", false, loadPackageParam.classLoader);
            dumpClass(clazz);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void hook() throws IOException, ClassNotFoundException {
        XposedBridge.log("hook all class start");
        DexFile dexFile = new DexFile(loadPackageParam.appInfo.sourceDir);
        Enumeration<String> classNames = dexFile.entries();
        while (classNames.hasMoreElements()) {
            String className = classNames.nextElement();


            if (isClassNameValid(className)) {
                final Class clazz = Class.forName(className, false, loadPackageParam.classLoader);
                XposedBridge.log("className"+className);

                for (Method method: clazz.getDeclaredMethods()) {
                    if (!Modifier.isAbstract(method.getModifiers())) {
                        XposedBridge.hookMethod(method, new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                log("HOOKED: " + clazz.getName() + "\\" + param.method.getName());
                            }
                        });
                    }
                }
            }
        }
    }


    public void log(Object str) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        XposedBridge.log("[" + df.format(new Date()) + "]:  "
                + str.toString());
    }

    public boolean isClassNameValid(String className) {
        return className.startsWith(loadPackageParam.packageName)
                && !className.contains("$")
                && !className.contains("BuildConfig")
                && !className.equals(loadPackageParam.packageName + ".R");
    }
    private void dumpClass(Class actions) {
        XposedBridge.log("Dump class " + actions.getName());

        XposedBridge.log("Methods");
        Method[] m = actions.getDeclaredMethods();
        for (int i = 0; i < m.length; i++) {
            XposedBridge.log(m[i].toString());
        }
        XposedBridge.log("Fields");
        Field[] f = actions.getDeclaredFields();
        for (int j = 0; j < f.length; j++) {
            XposedBridge.log(f[j].toString());
        }
        XposedBridge.log("Classes");
        Class[] c = actions.getDeclaredClasses();
        for (int k = 0; k < c.length; k++) {
            XposedBridge.log(c[k].toString());
        }
    }
}