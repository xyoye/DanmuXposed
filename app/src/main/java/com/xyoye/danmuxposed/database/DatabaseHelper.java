package com.xyoye.danmuxposed.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by xyy on 2018-03-23 上午 9:08
 */


public class DatabaseHelper extends SQLiteOpenHelper {
    private String name;

    DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.name = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if ("danmu.db".equals(name)){
            String sql = "create table danmu(id integer primary key autoincrement, " +
                    "video_path text, danmu_path text, time text)";
            db.execSQL(sql);
        }else if ("shield.db".equals(name)){
            String sql = "create table shielding(id integer primary key autoincrement, shield_text text)";
            db.execSQL(sql);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
