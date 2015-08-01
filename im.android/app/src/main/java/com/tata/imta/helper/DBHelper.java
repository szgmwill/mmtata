package com.tata.imta.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Will Zhang on 2015/6/2.
 * 本地数据库封装类
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mmtata.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LogHelper.debug(this, "init local DB");

        try {
//            db.execSQL("drop table user");

            //创建数据库
            String sql = "CREATE TABLE IF NOT EXISTS user(user_id INTEGER PRIMARY KEY, info TEXT, timestamp varchar(30));";
            db.execSQL(sql);
        } catch (Exception e) {
            LogHelper.error(this, "创建本地数据库失败", e);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //数据库或表有更新时调用
        //表结构更新等操作
        //TO DO
    }
}
