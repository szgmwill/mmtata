package com.tata.imta.helper;

import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tata.imta.activity.SingleChatActivity;
import com.tata.imta.activity.UserDetailActivity;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.app.BizInfoHolder;
import com.tata.imta.bean.ImgInfo;
import com.tata.imta.bean.User;
import com.tata.imta.bean.status.TabInfo;
import com.tata.imta.helper.holder.SharePreferenceHolder;
import com.tata.imta.task.GetUserDetailTask;
import com.tata.imta.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Will Zhang on 2015/6/5.
 * 用户所有操作工具类
 */
public class UserHelper {

    public static List<TabInfo> getTabList(JSONArray array) {
        List<TabInfo> retList = new ArrayList<>();
        if(array != null && array.size() > 0) {
            for(Object obj : array) {
                JSONObject json = (JSONObject) obj;
                String tabName = json.getString("tab_name");
                int tabType = json.getIntValue("tab_type");
                TabInfo tab = new TabInfo();
                tab.setTabName(tabName);
                tab.setTabType(tabType);
                retList.add(tab);
            }
        }

        return retList;
    }

    public static List<ImgInfo> getImgList(long userId, JSONArray array) {
        List<ImgInfo> retList = new ArrayList<>();
        if(array != null && array.size() > 0) {
            for(Object obj : array) {
                JSONObject json = (JSONObject) obj;
                int index = json.getInteger("index");
                String url = json.getString("url");
                ImgInfo img = new ImgInfo();
                img.setUrl(url);
                img.setUserId(userId);
                retList.add(img);
            }
        }

        return retList;
    }

    /**
     * 将序列化的用户信息反序例化成对象
     */
    public static User transferUserJson(String jsonStr) {
        if(!TextUtils.isEmpty(jsonStr)) {
            JSONObject json = JsonUtils.formatJson(jsonStr);
            if(json != null) {
                User user = JSON.parseObject(jsonStr, User.class);

                return user;
            }
        }

        return null;
    }

    /**
     * 查询用户详情
     * 1.先查本地数据库；
     * 2.如果查不到则查后台服务器端；
     * 3.查询完后再更新本地数据库
     * 4.如果查询有结果但过期了(4小时),同样执行3
     */
    public static User queryUserDetailFromId(long userId) {
        LogHelper.debug(UserHelper.class, "查询用户["+userId+"]详细信息");

        //用户信息在本地的有效时间(即缓存时间)(暂定10分种)
        long expireTime = 10*60*1000;
        User targetUser = null;
        boolean isUpdateLocal = false;
        //查DBs
        targetUser = UserDBManager.getInstance().getUserInfoById(userId);
        if(targetUser == null) {
            isUpdateLocal = true;
            //查后台
            targetUser = ServerAPIHelper.getUserDetailFromServer(userId);
        } else {
            String timestamp = targetUser.getDbTimeStamp();
            LogHelper.debug(UserHelper.class, "user["+userId+"],timestamp["+timestamp+"]");
            if(timestamp != null && (System.currentTimeMillis() - Long.parseLong(timestamp) > expireTime)) {
                LogHelper.debug(UserHelper.class, "user expire");
                isUpdateLocal = true;
                //如果已经过期,也要查后台
                targetUser = ServerAPIHelper.getUserDetailFromServer(userId);
            } else {
                return targetUser;
            }
        }
        if(targetUser != null) {
            LogHelper.debug(UserHelper.class, "查询用户[" + userId + "]详情结果:" + targetUser.toString());
            if(isUpdateLocal) {
                LogHelper.debug(UserHelper.class, "更新用户["+userId+"]本地缓存");
                //更新本地相关缓存
                //更新本地数据库
                UserDBManager.getInstance().addUser(targetUser);
                //更新sp
                if(BizInfoHolder.getInstance().getLoginUser() != null &&
                        BizInfoHolder.getInstance().getLoginUser().getUserId() == targetUser.getUserId()) {
                    //更新本地share和内存缓存
                    SharePreferenceHolder.getInstance().clearLoginUserInfo();
                    SharePreferenceHolder.getInstance().saveUserInfo2Local(targetUser);
                }
            }
        }
        return targetUser;
    }

    /**
     * 跳转用户详情页
     *
     */
    public static void goUserDetail(final BaseActivity context, final long userId) {
        if(userId > 0) {
            GetUserDetailTask task = new GetUserDetailTask(new GetUserDetailTask.GetUserDetailCallBack() {
                @Override
                public void onCallBack(Map<Long, User> resultMap) {
                    User senderUser = resultMap.get(userId);
                    if(senderUser == null) {
                        context.showAlertDialog("错误提示", "找不到用户信息");
                    } else {
                        Intent i = new Intent(context, UserDetailActivity.class);
                        i.putExtra("user", senderUser);
                        context.startActivity(i);
                    }
                }
            });

            List<Long> userIdList = new ArrayList<Long>();
            userIdList.add(userId);
            task.execute(userIdList);
        }


    }

    /**
     * 进入聊天页
     */
    public static void goChat(final BaseActivity activity, long userId) {
        //进入聊天页面
        User targetUser = null;

        if(userId == 0) {
            targetUser = new User();
            targetUser.setUserId(userId);
            targetUser.setNick("admin");
        } else {
            targetUser = queryUserDetailFromId(userId);
        }

        if(targetUser != null) {
            //跳转到聊天页面
            Intent toChat = new Intent(activity,
                    SingleChatActivity.class);

            toChat.putExtra("user", targetUser);

            activity.startActivity(toChat);
        }
    }

    /**
     * 判断指定用户是否当前登录用户自身
     */
    public static boolean isLoginUser(long targetId) {
        User curLoginUser = BizInfoHolder.getInstance().getLoginUser();
        if(curLoginUser != null) {
            return (curLoginUser.getUserId() == targetId);
        }

        return false;
    }
}
