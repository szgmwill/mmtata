package com.tata.imta.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tata.imta.R;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.app.ContentView;
import com.tata.imta.app.ViewInject;
import com.tata.imta.bean.ResultObject;
import com.tata.imta.bean.UserExtend;
import com.tata.imta.bean.status.ServerAPI;
import com.tata.imta.constant.RequestCode;
import com.tata.imta.helper.LoadDataFromServer;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.helper.holder.SharePreferenceHolder;
import com.tata.imta.util.JsonUtils;
import com.tata.imta.util.ViewInjectUtils;

/**
 * Created by Will Zhang on 2015/7/8.
 * 提现设置页
 */
@ContentView(R.layout.ta_activity_withdraw_setting)
public class WithdrawSettingActivity extends BaseActivity {

    @ViewInject(R.id.ta_tv_topbar_title)
    private TextView mTitleTV;
    @ViewInject(R.id.ta_rl_topbar_back)
    private RelativeLayout mBackRL;
    @ViewInject(R.id.ta_rl_topbar_more)
    private RelativeLayout mMoreRL;

    //支付宝账户
    @ViewInject(R.id.ta_et_withdraw_zfb_account)
    private EditText mZFBAcctET;

    //支付宝昵称
    @ViewInject(R.id.ta_et_withdraw_zfb_nick)
    private EditText mZFBNickET;

    //微信账户
    @ViewInject(R.id.ta_tv_withdraw_setting_wx)
    private EditText mWXAcctTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewInjectUtils.inject(this);

        initViews();
        initEvents();
    }

    @Override
    protected void initViews() {
        mTitleTV.setText("提现设置");

        //显示保存按钮
        TextView saveTV = (TextView) mMoreRL.findViewById(R.id.ta_tv_topbar_more);
        saveTV.setVisibility(View.VISIBLE);
        saveTV.setText("保存");
        mMoreRL.findViewById(R.id.ta_iv_topbar_more).setVisibility(View.GONE);


        //初始化内容
        UserExtend extend = loginUser.getExtend();
        if(extend != null) {

            //支付宝
            String zfb_acct = extend.getZfb_acct();
            String zfb_nick = extend.getZfb_nick();
            if(!TextUtils.isEmpty(zfb_acct)) {
                mZFBAcctET.setText(zfb_acct);
            }
            if(!TextUtils.isEmpty(zfb_nick)) {
                mZFBNickET.setText(zfb_nick);
            }

            //微信
            String wx_openid = loginUser.getWxOpenId();
            String wx_acct = loginUser.getExtend().getWx_acct();
            if(!TextUtils.isEmpty(wx_acct)) {
                mWXAcctTV.setText(wx_acct);
            } else if(!TextUtils.isEmpty(wx_openid)) {
                mWXAcctTV.setText(wx_openid);
            }

        }
    }

    @Override
    protected void initEvents() {
        mBackRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mMoreRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存设置,调用后台
                String zfb_acct = mZFBAcctET.getText().toString();
                String zfb_nick = mZFBNickET.getText().toString();
                String wx_acct = mWXAcctTV.getText().toString();
                if(TextUtils.isEmpty(zfb_acct) || TextUtils.isEmpty(zfb_nick)) {
                    showAlertDialog("操作提示", "请填写相关信息后保存");
                } else {
                    UserExtend extend = loginUser.getExtend();
                    if(extend == null) {
                        extend = new UserExtend();
                        extend.setUser_id(loginUser.getUserId());
                    }

                    extend.setZfb_acct(zfb_acct);
                    extend.setZfb_nick(zfb_nick);
                    extend.setWx_acct(wx_acct);

                    //调用后台更新
                    LogHelper.debug(this, "更新请求参数:" + JsonUtils.toJson(extend));
                    LoadDataFromServer updateExtend = new LoadDataFromServer(WithdrawSettingActivity.this,
                            ServerAPI.SERVER_API_UPDATE_USER_EXTEND, extend);

                    updateExtend.getData(new LoadDataFromServer.DataCallBack() {
                        @Override
                        public void onDataCallBack(ResultObject ro) {
                            showAlertDialog("提现设置", "设置成功");
                            //同时更新本地
                            SharePreferenceHolder.getInstance().saveUserInfo2Local(loginUser);
                        }
                    });
                }
            }
        });
    }

    /**
     * 当关闭时将填写内容回传回去
     */
    @Override
    public void finish() {
        UserExtend extend = loginUser.getExtend();

        Intent toIntent = new Intent();
        toIntent.putExtra("extend", extend);
        setResult(RequestCode.Request_Code_Withdraw_Setting, toIntent);
        super.finish();
    }
}
