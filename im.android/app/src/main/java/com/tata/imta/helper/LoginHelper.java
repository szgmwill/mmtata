package com.tata.imta.helper;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeStatusCode;
import com.gotye.api.GotyeUser;
import com.tata.imta.activity.GuideActivity;
import com.tata.imta.activity.MainActivity;
import com.tata.imta.activity.UserTabSexActivity;
import com.tata.imta.app.ActivityManager;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.bean.ImgInfo;
import com.tata.imta.bean.ResultObject;
import com.tata.imta.bean.User;
import com.tata.imta.bean.UserExtend;
import com.tata.imta.bean.status.ServerAPI;
import com.tata.imta.bean.status.TabInfo;
import com.tata.imta.component.GotyeService;
import com.tata.imta.helper.holder.SharePreferenceHolder;
import com.tata.imta.util.JsonUtils;
import com.tata.imta.util.ToastUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Will Zhang on 2015/6/1.
 * 登录统一处理
 */
public class LoginHelper {
    /**
     * 统一用户登录入口
     * 全流程登录,一般只用于新注册用户或者本地没有登录信息的老用户
     */
    public static void loginServer(final BaseActivity activity, final User user) {
        if(!TextUtils.isEmpty(user.getWxOpenId())) {
            LogHelper.debug(activity, "loginServer userinfo :" + user.toString());

            //调用后台注册新用户,如果该用户已经注册过,将返回用户id
            //登录自己服务器后台
            LoadDataFromServer task = new LoadDataFromServer(activity, ServerAPI.SERVER_API_REGISTER, user);
            task.getData(new LoadDataFromServer.DataCallBack() {
                @Override
                public void onDataCallBack(ResultObject result) {

                    JSONObject data = ServerAPIHelper.handleServerResult(activity, result);
                    if(data != null) {
                        //得到注册用户id
                        long userId = data.getLong("user_id");
                        LogHelper.info(activity, "Server Register OK,userId:" + userId);
                        user.setUserId(userId);

                        //后台登录成功后,更新信息
                        resetLoginUser(activity, user);
                    } else {
                        activity.dismissProgressDialog();
                        //注册失败了
                        ToastUtils.showShortToast(activity, "注册新用户失败了");
                    }
                }
            });
        }
    }

    /**
     * 刷新登录用户详情信息
     * 即从后台业务服务器拉取最新的用户信息覆盖到本地
     */
    public static void resetLoginUser(final BaseActivity activity, final User loginUser) {

        //从后台拉取用户的详细信息
        Map<String, Object> reqServerMap = new HashMap<>();
        reqServerMap.put("user_id", loginUser.getUserId());
        LoadDataFromServer userDtlTask = new LoadDataFromServer(activity, ServerAPI.SERVER_API_USER_DETAIL, reqServerMap);

        /**
         * 处理本地用户信息和服务器端的用户信息
         * 正常情况下用户信息要以服务器端为准
         */
        userDtlTask.getData(new LoadDataFromServer.DataCallBack() {
            @Override
            public void onDataCallBack(ResultObject result) {

                JSONObject data = ServerAPIHelper.handleServerResult(activity, result);
                if(data == null) {
                    activity.showAlertDialog("操作提示", "后台失败");
                    return;
                }

                //开始保存相关用户信息
                JSONObject baseJson = data.getJSONObject("base");
                User user = null;
                if (baseJson != null) {
                    user = UserHelper.transferUserJson(baseJson.toJSONString());
                }
                if (user == null) {
                    user = loginUser;
                }

                //openid
                user.setWxOpenId(loginUser.getWxOpenId());
                LogHelper.debug(LoginHelper.class, "reset user wx openid:"+user.getWxOpenId());

                //tab list
                List<TabInfo> tablist = UserHelper.getTabList(data.getJSONArray("tab_list"));


                if (tablist != null && tablist.size() > 0) {
                    user.getTabList().addAll(tablist);
                }

                //读取其它信息保存

                //相册信息
                List<ImgInfo> imgList = UserHelper.getImgList(user.getUserId(), data.getJSONArray("img_list"));
                if (imgList.size() == 0) {
                    ImgInfo headImg = new ImgInfo();
                    headImg.setUrl(user.getHead());
                    headImg.setUserId(user.getUserId());
                    imgList.add(headImg);
                }
                user.getImgList().addAll(imgList);

                //用户扩展信息
                JSONObject extendJson = data.getJSONObject("extend");
                if(extendJson != null) {
                    LogHelper.debug(LoginHelper.class, "查询到用户[" + user.getUserId() + "]扩展信息:" + extendJson.toJSONString());

                    UserExtend extend = JsonUtils.json2Obj(extendJson.toString(), UserExtend.class);
                    if(extend != null) {
                        user.setExtend(extend);
                    }
                }

                //保存用户详细信息,后续所有操作都以这个用户信息为准
                SharePreferenceHolder.getInstance().saveUserInfo2Local(user);

                //亲加登录
                loginGotye(activity, user);
            }
        });
    }

    /**
     * 登录亲加服务器
     * 注意:登录前要判断一下用户是否已经登录过了,即是否在线,当不在线时才需要登录
     *
     */
    public static void loginGotye(final BaseActivity activity, final User loginUser) {
        //调用下面的接口可以获知当前是否处于在线状态
        //GotyeUser.NETSTATE_BELOWLINE: 离线状态(网络恢复正常后会自动重连)
        //GotyeUser.NETSTATE_OFFLINE: 未登录或者已登出(调用了logout)
        //GotyeUser.NETSTATE_ONLINE: 在线
        //判断用户登录连线情况
        int onlineCode = GotyeAPI.getInstance().isOnline();
        if(onlineCode == GotyeUser.NETSTATE_ONLINE) {
            //已经登录了,不需要再登
            LogHelper.debug(activity, "user [" + loginUser.getUserId() + "] is already ta_activity_mobile_login to Gotye");

            //登录后跳转
            loginJump(activity);

        } else {
            //需要重新登录的话,再调用后台登录登出记录一下
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("user_id", loginUser.getUserId());
            paramMap.put("status", "IN");
            LoadDataFromServer logoutTask = new LoadDataFromServer(activity, ServerAPI.SERVER_API_DO_LOGIN, paramMap);

            logoutTask.getData(new LoadDataFromServer.DataCallBack() {
                @Override
                public void onDataCallBack(ResultObject result) {
                    //通过service来实现登录
                    Intent login = new Intent(activity, GotyeService.class);
                    login.setAction(GotyeService.ACTION_LOGIN);//标记是登录操作
                    login.putExtra("user_id", String.valueOf(loginUser.getUserId()));
                    //发起第三方平台登录
                    activity.startService(login);

                    //这里不能finish,还要等待回调后
//                  activity.finish();
                }
            });
        }
    }

    /**
     * 处理登录成功后的跳转
     */
    public static void loginJump(BaseActivity activity) {
        User hasLoginUser = SharePreferenceHolder.getInstance().getLoginUserInfo();
        if(hasLoginUser != null && hasLoginUser.getUserId() > 0) {
            //已经有用户登录信息了

            //并且更新相关列表
            GotyeAPI.getInstance().reqFriendList();
            GotyeAPI.getInstance().reqBlockedList();

            //再判断一下用户是否第一次登录(需要进入标签设置),否则直接跳转主页面
            if(hasLoginUser.getTabList() != null && hasLoginUser.getTabList().size() > 0) {
                //直接主界面
                //登录成功后,根据用户标签是否设置过,判断用户需不需要走标签选择页,还是直接到主页
                Intent i = new Intent(activity, MainActivity.class);
                activity.startActivity(i);
                activity.finish();
            } else {
                //第一次进入
                Intent i = new Intent(activity, UserTabSexActivity.class);
                activity.startActivity(i);
                activity.finish();
            }
        }
    }

    /**
     * 处理亲加登录后回调统一操作
     */
    public static void onGotyeLoginCallback(BaseActivity activity, int code, GotyeUser currentLoginUser) {
        // 判断登陆是否成功
        if (code == GotyeStatusCode.CodeOK //0
                || code == GotyeStatusCode.CodeReloginOK //5
                || code == GotyeStatusCode.CodeOfflineLoginOK) {  //6

            //完全登录成功后,判断跳转方向
            //1.直接跳转主页;2.跳转用户标签设置页;
            loginJump(activity);

            if (code == GotyeStatusCode.CodeOfflineLoginOK) {
                ToastUtils.showLongToast(activity, "您当前处于离线状态");
            } else if (code == GotyeStatusCode.CodeOK) {
                ToastUtils.showShortToast(activity, "登录成功");
            }
        } else {
            // 失败,可根据code定位失败原因
            ToastUtils.showLongToast(activity, "登录失败 code=" + code);
        }
    }

    /**
     * 如果用户在微信取消或不授权的话,直接回到登录页
     */
    public static void cancelLogin(Activity activity) {
        Intent toIntent = new Intent();
        toIntent.putExtra("logout", "true");//标记是从退出登录过来的
        toIntent.setClass(activity, GuideActivity.class);
        //控制不能再回退
        toIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        activity.startActivity(toIntent);

        activity.finish();//按理退出后就不能再回退回来

        //关闭所有其它活动
        ActivityManager.getInstance().popAllActivityExceptOne(GuideActivity.class);
    }
}
