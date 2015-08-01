package com.tata.imta.helper;

import android.app.Activity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeUser;
import com.tata.imta.R;

/**
 * Created by Will Zhang on 2015/6/12.
 * 面板常用操作帮助类
 */
public class FragmentHelper {

    /**
     * 刷新面板的网络连接提示语
     * 根据当前亲加通迅云的连接状态判断
     */
    public static void refreshTopbarErrTips(Activity view, String showMsg) {
        if(view != null) {

            RelativeLayout errRL = (RelativeLayout) view.findViewById(R.id.ta_common_network_error_tips);
//            RelativeLayout errRL = (RelativeLayout) view.findViewById(R.id.ta_rl_error_tips);

            //面板里的提示语
            TextView errorMsg = (TextView) view.findViewById(R.id.tv_error_msg);

            boolean showErr = false;
            //调用下面的接口可以获知当前是否处于在线状态
            //GotyeUser.NETSTATE_BELOWLINE: 离线状态(网络恢复正常后会自动重连)
            //GotyeUser.NETSTATE_OFFLINE: 未登录或者已登出(调用了logout)
            //GotyeUser.NETSTATE_ONLINE: 在线
            //判断用户登录连线情况
            int state = GotyeAPI.getInstance().isOnline();
            if(state != GotyeUser.NETSTATE_ONLINE) {
                showErr = true;
            } else {
                //成功的
                showErr = false;
            }

            if(showErr || showMsg != null) {//只要是网络不通或者强制展示提示语的话
                errRL.setVisibility(View.VISIBLE);
                errorMsg.setText(showMsg == null? "网络异常" : showMsg);
            } else {
                //判断当前网络环境是否正常，给出提示语,一般情况下隐藏并且不占用空间
                errRL.setVisibility(View.GONE);
            }
        }
    }
}
