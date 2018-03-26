package com.xyoye.danmuxposed.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xyy on 2018-03-23 上午 9:08
 */


public class DatabaseDao {
    private DatabaseHelper helper;
    private DatabaseHelper shieldHelper;

    public DatabaseDao(Context context) {
        helper = new DatabaseHelper(context, "danmu.db", null, 1);
        shieldHelper = new DatabaseHelper(context, "shield.db", null, 1);
    }

    public void insert(String videoPath, String danmuPath){
        if (query(videoPath).size() > 0){
            update(videoPath,danmuPath);
        }else {
            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("video_path",videoPath);
            values.put("danmu_path",danmuPath);
            values.put("time", String.valueOf(new Date()));
            db.insert("danmu", null, values);
            db.close();
        }
    }

    private void update(String videoPath, String danmuPath){
        SQLiteDatabase db = helper.getReadableDatabase();
        ContentValues updatedValues = new ContentValues();
        updatedValues.put("danmu_path", danmuPath);
        updatedValues.put("time", String.valueOf(new Date()));
        db.update("danmu",updatedValues,"video_path=?",new String[]{videoPath});
        db.close();
    }

    public List<String> query(String videoPath){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query("danmu",null,"video_path=?",new String[]{videoPath},null,null,"id DESC");
        List<String> list = new ArrayList<>();
        while (c.moveToNext()) {
            String result_path = c.getString(c.getColumnIndex("danmu_path"));
            list.add(result_path);
        }
        c.close();
        db.close();
        return list;
    }

    public void deleteAll(){
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "DELETE FROM danmu";
        db.execSQL(sql);
        db.close();
    }

    public boolean insertShield(String shieldText){
        if (!queryShield(shieldText)){
            SQLiteDatabase db = shieldHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("shield_text",shieldText);
            db.insert("shielding", null, values);
            db.close();
            return true;
        }else {
            return false;
        }

    }

    public void deleteShield(String shieldText){
        SQLiteDatabase db = shieldHelper.getWritableDatabase();
        db.delete("shielding", "shield_text=?" ,new String[]{shieldText});
        db.close();
    }

    public void deleteAllShield(){
        SQLiteDatabase db = shieldHelper.getReadableDatabase();
        String sql = "DELETE FROM shielding";
        db.execSQL(sql);
        db.close();
    }

    public List<String> queryAllShield(){
        SQLiteDatabase db = shieldHelper.getReadableDatabase();
        Cursor c = db.query("shielding",null,null,null,null,null,"id DESC");
        List<String> list = new ArrayList<>();
        while (c.moveToNext()) {
            String text = c.getString(c.getColumnIndex("shield_text"));
            list.add(text);
        }
        c.close();
        db.close();
        return list;
    }

    private boolean queryShield(String shieldText){
        SQLiteDatabase db = shieldHelper.getReadableDatabase();
        Cursor c = db.query("shielding",null,"shield_text=?",new String[]{shieldText},null,null,"id DESC");
        List<String> list = new ArrayList<>();
        while (c.moveToNext()) {
            String text = c.getString(c.getColumnIndex("shield_text"));
            list.add(text);
        }
        c.close();
        db.close();
        return list.size() > 0;
    }
}
