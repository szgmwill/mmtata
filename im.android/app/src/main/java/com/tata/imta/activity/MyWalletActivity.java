package com.tata.imta.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.tata.imta.R;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.app.ContentView;
import com.tata.imta.app.ViewInject;
import com.tata.imta.bean.ResultObject;
import com.tata.imta.bean.status.ServerAPI;
import com.tata.imta.helper.LoadDataFromServer;
import com.tata.imta.helper.ServerAPIHelper;
import com.tata.imta.util.ViewInjectUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Will Zhang on 2015/6/27.
 * 我的钱包
 */
@ContentView(R.layout.ta_activity_my_wallet)
public class MyWalletActivity extends BaseActivity implements View.OnClickListener,LoadDataFromServer.DataCallBack {

    @ViewInject(R.id.ta_tv_topbar_title)
    private TextView mTitleTV;
    @ViewInject(R.id.ta_rl_topbar_back)
    private RelativeLayout mBackRL;
    @ViewInject(R.id.ta_rl_topbar_more)
    private RelativeLayout mMoreRL;

    @ViewInject(R.id.ta_tv_my_wallet_balance)
    private TextView mBalanceTV;

    @ViewInject(R.id.ta_tv_my_wallet_withdraw)
    private TextView mWithdrawTV;

    @ViewInject(R.id.ta_rl_my_wallet_order)
    private RelativeLayout mOrderRL;

    @ViewInject(R.id.ta_rl_my_wallet_go_withdraw)
    private RelativeLayout mWithdrawRL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //绑定视图初始化
        ViewInjectUtils.inject(this);

        initViews();

        initEvents();

        initContent();
    }

    @Override
    protected void initViews() {
        mTitleTV.setText("我的钱包");

        mMoreRL.setVisibility(View.GONE);
    }

    @Override
    protected void initEvents() {
        mBackRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mOrderRL.setOnClickListener(this);

        mWithdrawRL.setOnClickListener(this);
    }

    /**
     * 初始化个人账户信息
     */
    private void initContent() {
        if(loginUser != null) {
            //从后台读取我的账户信息
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("user_id", loginUser.getUserId());
            LoadDataFromServer getAcctInfoTask = new LoadDataFromServer(this, ServerAPI.SERVER_API_QUERY_MY_ACCT, paramMap);

            getAcctInfoTask.getData(this);

        }
    }

    /**
     * 当回到前台展示时,刷新一下钱的变化
     */
    @Override
    protected void onResume() {
        super.onResume();
        initContent();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ta_rl_my_wallet_order) {

            //跳转明细页面
            Intent i = new Intent();
            i.setClass(this, MyTradeActivity.class);
            startActivity(i);


        } else if(v.getId() == R.id.ta_rl_my_wallet_go_withdraw) {

            //跳转提现申请页
            startActivity(ApplyWithdrawActivity.class);
        }
    }

    @Override
    public void onDataCallBack(ResultObject result) {
        JSONObject data = ServerAPIHelper.handleServerResult(this, result);
        if(data == null) {
            showAlertDialog("操作提示", "读取我的账户信息失败");
        } else {
            BigDecimal my_balance = data.getBigDecimal("balance");
            if(my_balance != null) {
                mBalanceTV.setText(String.valueOf(my_balance));
            }
            BigDecimal my_withdraw = data.getBigDecimal("withdraw_amt");
            if(my_withdraw != null) {
                mWithdrawTV.setText(String.valueOf(my_withdraw));
            }
        }
    }
}
