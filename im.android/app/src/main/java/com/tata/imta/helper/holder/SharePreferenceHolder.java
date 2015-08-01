package com.tata.imta.helper.holder;

import android.content.Context;
import android.content.SharedPreferences;

import com.tata.imta.app.BizInfoHolder;
import com.tata.imta.bean.User;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.helper.UserDBManager;
import com.tata.imta.helper.UserHelper;
import com.tata.imta.util.JsonUtils;

/**
 * Created by will on 2015/4/30.
 *
 * 缓存各种变量值
 * 本地xml缓存
 */
public class SharePreferenceHolder {

    private volatile static SharePreferenceHolder singleton;

    private Context context;

    /**
     * 用户信息
     */
    private final String USER_INFO = "saveUser";

    private String SHARED_KEY_SETTING_USER_INFO = "user_info_json";

    /**
     * 私有实例化
     */
    private SharePreferenceHolder(Context context) {
        this.context = context;
    }

    /**
     * 实例化单例
     */
    public static void init(Context context) {
        if (singleton == null) {
            synchronized (SharePreferenceHolder.class) {
                if (singleton == null) {
                    LogHelper.info(SharePreferenceHolder.class, "init SharePreferenceHolder ===> ");
                    singleton = new SharePreferenceHolder(context);
                }
            }
        }
        return;
    }

    public static SharePreferenceHolder getInstance() {
        return singleton;
    }

    public static void destroy() {
        if(singleton != null) {
            singleton = null;
        }
    }

    /**
     * 从本地缓存中取出登录用户信息
     */
    public User getLoginUserInfo() {

        SharedPreferences userPreferences = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
        String userJson = userPreferences.getString(SHARED_KEY_SETTING_USER_INFO, "");

        User loginUser = UserHelper.transferUserJson(userJson);

        return loginUser;
    }


    /**
     * 用户登录成功后,缓存用户信息
     */
    public boolean saveUserInfo2Local(User loginUser) {

        if(loginUser != null) {
            String jsonStr = JsonUtils.toJson(loginUser);
            if(jsonStr != null) {
                SharedPreferences userPreferences = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
                SharedPreferences.Editor userEditor = userPreferences.edit();
                userEditor.putString(SHARED_KEY_SETTING_USER_INFO, jsonStr);

                userEditor.commit();
            }

            //同时刷新全局登录用户信息
            BizInfoHolder.getInstance().refreshLoginUser();

            //同时更新数据库信息
            UserDBManager.getInstance().addUser(loginUser);
        }
        return true;
    }

    /**
     * 用户退出后清理用户信息
     */
    public boolean clearLoginUserInfo() {
        SharedPreferences userPreferences = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor userEditor = userPreferences.edit();
        userEditor.clear();

        userEditor.commit();
        return true;
    }
}
