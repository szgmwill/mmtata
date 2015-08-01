package com.tata.imta.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gotye.api.GotyeDelegate;
import com.gotye.api.GotyeStatusCode;
import com.tata.imta.R;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.app.ContentView;
import com.tata.imta.app.ViewInject;
import com.tata.imta.bean.ResultObject;
import com.tata.imta.bean.status.ServerAPI;
import com.tata.imta.helper.LoadDataFromServer;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.helper.LoginHelper;
import com.tata.imta.helper.holder.SharePreferenceHolder;
import com.tata.imta.util.ToastUtils;
import com.tata.imta.util.ViewInjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Will Zhang on 2015/6/11.
 * 主设置页面
 */
@ContentView(R.layout.ta_activity_setting)
public class SettingsMainActivity extends BaseActivity implements View.OnClickListener,
        LoadDataFromServer.DataCallBack, DialogInterface.OnClickListener  {

    @ViewInject(R.id.ta_tv_topbar_title)
    private TextView mTitleTV;
    @ViewInject(R.id.ta_rl_topbar_back)
    private RelativeLayout mBackRL;
    @ViewInject(R.id.ta_rl_topbar_more)
    private RelativeLayout mMoreRL;

    //视图控件================start===============
    @ViewInject(value = R.id.ta_rl_setting_msg)
    private RelativeLayout mMsgRL;//消息设置

    @ViewInject(value = R.id.ta_rl_setting_bindmobile)
    private RelativeLayout mMobileRL;//绑定手机号

    @ViewInject(value = R.id.ta_rl_setting_password)
    private RelativeLayout mPasswordRL;//修改密码

    @ViewInject(value = R.id.ta_rl_setting_blacklist)
    private RelativeLayout mBlacklistRL;//黑名单设置

    @ViewInject(value = R.id.ta_rl_setting_withdraw)
    private RelativeLayout mWithdrawRL;//提现设置

    //退出按钮
    @ViewInject(value = R.id.ta_btn_setting_logout)
    private Button mBtnLogout;//退出登录

    //视图控件================end===============

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ta_activity_setting);

        gotyeAPI.addListener(delegate);

        //自动注入IOC
        ViewInjectUtils.inject(this);

        initViews();

        initEvents();
    }

    @Override
    protected void onDestroy() {
        gotyeAPI.removeListener(delegate);
        super.onDestroy();
    }

    @Override
    protected void initViews() {

        mTitleTV.setText("设置");
        mMoreRL.setVisibility(View.GONE);


    }

    @Override
    protected void initEvents() {

        mBackRL.setOnClickListener(this);

        mBtnLogout.setOnClickListener(this);

        //提现设置页
        mWithdrawRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(WithdrawSettingActivity.class);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ta_rl_topbar_back :

                finish();

                break;

            case R.id.ta_btn_setting_logout :

                //处理退出登录
                showConfirmDialog("确认退出吗?", this);

                break;

            case R.id.ta_rl_setting_withdraw :

                //跳转提现设置
                startActivity(WithdrawSettingActivity.class);

                break;
        }


    }

    @Override
    public void onDataCallBack(ResultObject result) {
        //业务服务器退出成功

        //2.后从亲加第三方退出
        gotyeAPI.logout();
    }

    /**
     * 确认退出
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        //1.先从后台退出
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("user_id", loginUser.getUserId());
        paramMap.put("status", "OUT");
        LoadDataFromServer logoutTask = new LoadDataFromServer(SettingsMainActivity.this, ServerAPI.SERVER_API_DO_LOGIN, paramMap);

        logoutTask.getData(SettingsMainActivity.this);
    }

    //亲加服务回调统一处理,只重写你要处理的回调方法
    private GotyeDelegate delegate = new GotyeDelegate() {
        /**
         * 退出亲加通迅云
         * 登出回调（onLogout）中的状态码可能有以下三种情况：
         CodeOK：正常登出成功
         CodeNetworkDisConnected: 网络异常，转换成离线状态, 这种情况下API会自动重连
         CodeForceLogout：账号在其他设备登录，被强制下线
         */
        @Override
        public void onLogout(int code) {
            LogHelper.debug(this, "onLogout ==> code[" + code + "]");

            if(code == GotyeStatusCode.CodeOK) {

                //退出成功,将相关用户信息置空,并且导到登录页去
                SharePreferenceHolder.getInstance().clearLoginUserInfo();

                ToastUtils.showShortToast(SettingsMainActivity.this, "退出成功");

                LoginHelper.cancelLogin(SettingsMainActivity.this);
            } else {
                //退出失败
                ToastUtils.showShortToast(SettingsMainActivity.this, "退出失败:"+code);
            }
        }
    };
}
