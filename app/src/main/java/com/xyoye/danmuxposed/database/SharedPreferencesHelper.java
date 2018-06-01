package com.xyoye.danmuxposed.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.xyoye.danmuxposed.base.IApplication;

/**
 * Created by xyy on 2017/12/24.
 */

public class SharedPreferencesHelper {
    private static final String danmuxposed = "danmuxposed";
    private static SharedPreferences mSharedPreferences;

    private static class Instance {
        private static SharedPreferencesHelper preferencesHelper = new SharedPreferencesHelper();
    }

    private SharedPreferencesHelper() {
        mSharedPreferences = IApplication.getContextObject().getSharedPreferences(danmuxposed, Context.MODE_PRIVATE);
    }

    public static SharedPreferencesHelper getInstance() {
        return Instance.preferencesHelper;
    }

    /**
     * 保存int数据
     * @param key 键
     * @param value 值
     */
    public void saveInteger(String key, int value){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * 获取int数据
     * @param key 键
     * @return int
     */
    public int getInteger(String key, int defValue){
        return mSharedPreferences.getInt(key,defValue);
    }

    /**
     * 保存String数据
     * @param key 键
     * @param value 值
     */
    public void saveString(String key, String value){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 获取String数据
     * @param key 键
     * @return String
     */
    public String getString(String key, String defValue){
        return mSharedPreferences.getString(key,defValue);
    }

    /**
     * 保存Boolean数据
     * @param key 键
     * @param value 值
     */
    public void saveBoolean(String key, boolean value){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * 获取Boolean数据
     * @param key 键
     * @param defValue 默认值
     * @return boolean
     */
    public Boolean getBoolean(String key, boolean defValue){
        return mSharedPreferences.getBoolean(key,defValue);
    }
}
