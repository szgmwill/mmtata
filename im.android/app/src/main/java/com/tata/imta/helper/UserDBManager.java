package com.tata.imta.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.tata.imta.bean.User;
import com.tata.imta.util.JsonUtils;

import java.util.List;

/**
 * Created by Will Zhang on 2015/6/2.
 * 针对用户详细信息的本地数据库操作
 * 单例,注意多线程读写数据库是否有安全问题
 */
public class UserDBManager {
    private volatile static UserDBManager singleton;

    private DBHelper helper;
    private SQLiteDatabase db;

    /**
     * 私有实例化
     */
    private UserDBManager(Context context) {
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    /**
     * 实例化单例
     */
    public static void init(Context context) {
        if (singleton == null) {
            synchronized (UserDBManager.class) {
                if (singleton == null) {
                    LogHelper.info(UserDBManager.class, "init UserDBManager ===> ");
                    singleton = new UserDBManager(context);
                }
            }
        }
    }

    public static UserDBManager getInstance() {
        return singleton;
    }

    /**
     * 一般只在程序退出时才调用
     */
    public static void destroy() {
        if(singleton != null) {

            singleton.db.close();
            singleton.helper = null;
            singleton = null;
        }
    }

    /**
     * add user list
     *
     */
    public void addUserList(List<User> users) {
        if(users != null && users.size() > 0) {
            db.beginTransaction();  //开始事务
            try {
                for (User person : users) {

                    //将用户信息整体序列化存储
                    String userJson = JsonUtils.toJson(person);

                    db.execSQL("INSERT INTO user VALUES(?, ?, ?)", new Object[]{person.getUserId(), userJson, String.valueOf(System.currentTimeMillis())});
                }
                db.setTransactionSuccessful();  //设置事务成功完成
            } finally {
                db.endTransaction();    //结束事务
            }
        }
    }

    /**
     * add user
     */
    public void addUser(User user) {
        try {

            if(user != null && user.getUserId() > 0) {
                LogHelper.debug(this, "add new user["+user.getUserId()+"] to local DB");
                //将用户信息整体序列化存储
                String userJson = JsonUtils.toJson(user);
                //insert or replace 防止主键冲突
                db.execSQL("INSERT OR REPLACE INTO user VALUES(?, ?, ?)", new Object[]{user.getUserId(), userJson, String.valueOf(System.currentTimeMillis())});
            }
        } catch (Exception e) {
            LogHelper.error(this, "新增用户["+user.getUserId()+"]入本地数据库异常", e);
        }
        LogHelper.debug(this, "新增用户["+user.getUserId()+"]入库完成!");
    }

    /**
     * update person's age
     */
    public void updateInfo(User person) {
        if(person != null && person.getUserId() > 0) {
            //将用户信息整体序列化存储
            String userJson = JsonUtils.toJson(person);

            String sql = String.format("update user set info = %s, timestamp = %s where user_id = %s", userJson, String.valueOf(System.currentTimeMillis()), person.getUserId());//修改的SQL语句

            db.execSQL(sql);
        }
    }

    /**
     * delete old person
     */
    public void deleteOldUser(User person) {

        if(person != null && person.getUserId() > 0) {

            String sql = String.format("delete from user where user_id = %s", person.getUserId());//修改的SQL语句

            db.execSQL(sql);
        }
    }

    /**
     * 根据用户id查询用户信息
     */
    public User getUserInfoById(Long userId) {
        LogHelper.debug(this, "在本地数据库查询用户["+userId+"]详情");
        if(userId != null && userId > 0) {
            Cursor cursor = db.rawQuery("select * from user where user_id = ?", new String[]{String.valueOf(userId)});
            if (cursor.moveToFirst()) {
                String info = cursor.getString(cursor.getColumnIndex("info"));
                String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));
                LogHelper.debug(this, "user timestamp:"+timestamp);
                if (!TextUtils.isEmpty(info)) {
                    User user = UserHelper.transferUserJson(info);
                    LogHelper.debug(this, "select User["+userId+"] from DB:"+info);
//                    User user = JsonUtils.json2Obj(info, User.class);
                    if(user != null) {
                        user.setDbTimeStamp(timestamp);
                        LogHelper.debug(this, "db user:" + user.toString());
                        return user;
                    }

                }
            }
        }
        return null;
    }
}
