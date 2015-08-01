package com.tata.imta.helper.holder;

import android.content.Context;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeUser;
import com.tata.imta.helper.LogHelper;

import java.util.List;

/**
 * Created by Will Zhang on 2015/6/30.
 * 亲加通讯云各业务数据装载器
 */
public class GotyeDataHolder {

    private static volatile GotyeDataHolder instance = null;

    private Context context;

    private GotyeDataHolder(Context context) {
        LogHelper.debug(this, "init GotyeDataHolder ==> ");
        this.context = context;
    }

    public static void init(Context context) {
        if(instance == null) {
            synchronized (GotyeDataHolder.class) {
                if(instance == null) {
                    instance = new GotyeDataHolder(context);
                }
            }
        }

        if(instance.friendList == null) {
            instance.friendList = GotyeAPI.getInstance().getLocalFriendList();
            if(instance.friendList != null) {
                LogHelper.debug(instance, "init friends list done:"+instance.friendList.size());
            }
        }
        if(instance.blockedList == null) {
            instance.blockedList = GotyeAPI.getInstance().getLocalBlockedList();
            if(instance.blockedList != null) {
                LogHelper.debug(instance, "init block list done:"+instance.blockedList.size());
            }
        }
    }

    public static GotyeDataHolder getInstance() {
        return instance;
    }

    public static void destroy() {
        if(instance != null) {
            instance = null;
        }
    }

    /**
     * 缓存我的好友/关注列表
     * 与亲加列表保持一致
     * 如需向服务器请求更新该列表，可以调用以下接口：
     * gotyeApi.reqFriendList(); ///< 对应回调GotyeDelegate  onGetFriendList
     * 成功获取后，通过getLocalFriendList接口获取到的本地好友列表也会被更新，因此收到回调即可刷新UI(如果需要的话)。
     * 回调原型：
     * void onGetFriendList(int code, List<GotyeUser> friendlist);
     */
    public List<GotyeUser> friendList = null;

    /**
     * 缓存我的黑名单
     * 请求更新：
     * gotyeApi.reqBlockedList(); ///< 对应回调GotyeDelegate onGetBlockedList
     * 回调原型：
     * void onGetBlockedList(int code, List<GotyeUser> blockedlist);
     */
    public List<GotyeUser> blockedList = null;


    public List<GotyeUser> getFriendList() {
        return friendList;
    }

    public List<GotyeUser> getBlockedList() {
        return blockedList;
    }

    /**
     * 校验用户是否是我的好友/关注
     */
    public boolean isMyFriend(long target_id) {
        if(target_id > 0) {
            if(friendList != null && friendList.size() > 0) {
                for(GotyeUser user : friendList) {
                    if(String.valueOf(target_id).equals(user.getName())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * 校验用户是否我的黑名单
     */
    public boolean isMyBlock(long target_id) {
        if(target_id > 0) {
            if(blockedList != null && blockedList.size() > 0) {
                for(GotyeUser user : blockedList) {
                    if(String.valueOf(target_id).equals(user.getName())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
