package com.tata.imta.helper.holder;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeUser;

import java.util.List;

/**
 * Created by Will Zhang on 2015/6/30.
 * 亲加相关操作工具类
 */
public class GotyeHelper {

    /**
     * 校验用户是否是我的好友/关注
     */
    public static boolean isMyFriend(long target_id) {
        if(target_id > 0) {
            List<GotyeUser> friendList = GotyeAPI.getInstance().getLocalFriendList();
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
    public static boolean isMyBlock(long target_id) {
        if(target_id > 0) {
            List<GotyeUser> blockedList = GotyeAPI.getInstance().getLocalBlockedList();
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
