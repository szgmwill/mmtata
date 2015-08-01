package com.tata.imta.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.tata.imta.R;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.app.ContentView;
import com.tata.imta.app.ViewInject;
import com.tata.imta.bean.ResultObject;
import com.tata.imta.bean.UserExtend;
import com.tata.imta.bean.status.PayChannelType;
import com.tata.imta.bean.status.ServerAPI;
import com.tata.imta.constant.RequestCode;
import com.tata.imta.helper.LoadDataFromServer;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.helper.ServerAPIHelper;
import com.tata.imta.util.PriceUtils;
import com.tata.imta.util.ViewInjectUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Will Zhang on 2015/6/27.
 * 提现申请
 */
@ContentView(R.layout.ta_activity_go_withdraw)
public class ApplyWithdrawActivity extends BaseActivity {

    @ViewInject(R.id.ta_tv_topbar_title)
    private TextView mTitleTV;
    @ViewInject(R.id.ta_rl_topbar_back)
    private RelativeLayout mBackRL;
    @ViewInject(R.id.ta_rl_topbar_more)
    private RelativeLayout mMoreRL;

    //支付宝账户
    @ViewInject(R.id.ta_rl_go_withdraw_zfb)
    private RelativeLayout mZfbRL;
    @ViewInject(R.id.ta_tv_go_withdraw_zfb)
    private TextView mZfbTV;

    //微信账户
    @ViewInject(R.id.ta_rl_go_withdraw_wx)
    private RelativeLayout mWxRL;
    @ViewInject(R.id.ta_tv_go_withdraw_wx)
    private TextView mWxTV;

    //当前账户余额
    @ViewInject(R.id.ta_tv_go_withdraw_balance)
    private TextView mBalanceTV;

    //当前余额
    private BigDecimal myBalance;

    //本次提现金额
    @ViewInject(R.id.ta_et_go_withdraw_amount)
    private EditText mAmountET;

    //提交
    @ViewInject(R.id.ta_btn_go_withdraw_submit)
    private Button mBtnSubmit;

    //是否提交提现申请,主要控制用户重复点击
    private boolean hasSubmitted = false;

    //当前用户的扩展信息
    private UserExtend extend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewInjectUtils.inject(this);

        initViews();

        initEvents();
    }

    @Override
    protected void initViews() {
        mTitleTV.setText("提现申请");

        mMoreRL.setVisibility(View.GONE);

        //提取用户的扩展信息
        extend = loginUser.getExtend();
        if(extend == null || TextUtils.isEmpty(extend.getZfb_acct())) {
            //没有支付宝账户,提醒设置
            mZfbTV.setText("请先设置好账户");

            mZfbRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳到提现设置页
                    Intent goIntent = new Intent(ApplyWithdrawActivity.this, WithdrawSettingActivity.class);
                    startActivityForResult(goIntent, RequestCode.Request_Code_Withdraw_Setting);
                }
            });

        } else {
            mZfbTV.setText(extend.getZfb_acct() + "/" + extend.getZfb_nick());
        }

        if(extend == null || TextUtils.isEmpty(extend.getWx_acct())) {
            //没有微信账户,提醒设置
            mWxTV.setText("请先设置好账户");

            mWxRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳到提现设置页
                    Intent goIntent = new Intent(ApplyWithdrawActivity.this, WithdrawSettingActivity.class);
                    startActivityForResult(goIntent, RequestCode.Request_Code_Withdraw_Setting);
                }
            });
        } else if(TextUtils.isEmpty(loginUser.getWxOpenId())) {
            mWxTV.setText(loginUser.getWxOpenId());
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

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (hasSubmitted) {
                    showAlertDialog("操作提示", "您刚提交申请过,请退出后重新申请一笔");
                    return;
                }

                //只有当前余额大于等于提现金额,并且已经设置了支付宝账户才可以提现
                //TO DO
                String amount = mAmountET.getText().toString();

                final BigDecimal amt = PriceUtils.getValidPrice(amount.trim());

                if (amt == null) {
                    showAlertDialog("操作提示", "请输入合法提现金额[" + amount + "]");
                    return;
                }

                if (myBalance == null || myBalance.compareTo(amt) < 0 || amt.compareTo(new BigDecimal(20)) < 0) {
                    showAlertDialog("操作提示", "提现金额必须大于20元且小于当前余额");
                } else {
                    //发起提现,这里将直接调用支付宝转账接口实现商户转户转账 -> 用户转户
                    //调用后台处理提现相关
                    Map<String, Object> paramMap = new HashMap<String, Object>();
                    /**
                     *  user_id	long	是	用户id
                     *  target_acct	string	是	提现到目标账号类型：wx-微信；zfb-支付宝；
                     *  amount	decimal	是	提现金额
                     *  password	string	是	提现支付密码	基于安全考虑,提现操作最好设置二次密码
                     */
                    paramMap.put("user_id", loginUser.getUserId());
                    paramMap.put("target_acct", PayChannelType.ZFB);//目前只能是支付宝
                    paramMap.put("amount", amt);
//                    paramMap.put("password", "");

                    LoadDataFromServer applyWithdrawTask = new LoadDataFromServer(ApplyWithdrawActivity.this, ServerAPI.SERVER_API_APPLY_WITHDRAW,
                            paramMap);

                    applyWithdrawTask.getData(new LoadDataFromServer.DataCallBack() {
                        @Override
                        public void onDataCallBack(ResultObject result) {
                            JSONObject dataJson = ServerAPIHelper.handleServerResult(ApplyWithdrawActivity.this, result);
                            if (dataJson == null || dataJson.getLong("withdraw_id") == null) {
                                showAlertDialog("操作提示", "申请提现后台操作失败:" + result.getMsg());
                            } else {
                                //标记刚提交过
                                hasSubmitted = true;

                                //展示金额修改
                                if (myBalance != null) {
                                    mBalanceTV.setText(String.valueOf(myBalance.subtract(amt)));
                                }

                                showAutoFinishAlertDialog("申请提现", "提现成功,请查看您的账户金额变化", 2000);
                            }
                        }
                    });
                }
            }
        });

        //查询一下当前用户的账户信息
        //从后台读取我的账户信息
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("user_id", loginUser.getUserId());
        LoadDataFromServer getAcctInfoTask = new LoadDataFromServer(this, ServerAPI.SERVER_API_QUERY_MY_ACCT, paramMap);

        getAcctInfoTask.getData(new LoadDataFromServer.DataCallBack() {
            @Override
            public void onDataCallBack(ResultObject result) {
                JSONObject data = ServerAPIHelper.handleServerResult(ApplyWithdrawActivity.this, result);
                if (data == null) {
                    showAlertDialog("操作提示", "查询当前用户余额失败");
                } else {
                    BigDecimal my_balance = data.getBigDecimal("balance");
                    if (my_balance != null && my_balance.compareTo(new BigDecimal(0)) > 0) {
                        myBalance = my_balance;
                        mBalanceTV.setText(String.valueOf(my_balance));


                    }

                }
                resetSubmit();
            }
        });
    }

    /**
     * 处理提现设置的回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RequestCode.Request_Code_Withdraw_Setting) {

            if(data != null) {
                UserExtend extend = (UserExtend) data.getSerializableExtra("extend");
                if(extend != null) {
                    this.extend = extend;

                    if(TextUtils.isEmpty(extend.getZfb_acct()) || TextUtils.isEmpty(extend.getZfb_nick())) {
//                        mZfbTV.setText("请先设置好账户");
                    } else {
                        mZfbTV.setText(extend.getZfb_acct()+"/"+extend.getZfb_nick());
                    }

                    if(!TextUtils.isEmpty(extend.getWx_acct())) {
                        mWxTV.setText(extend.getWx_acct());
                    }

                    resetSubmit();
                }
            }
        }
    }

    /**
     * 检查提交条件是否达到
     *
     */
    private void resetSubmit() {
        //条件:余额大于0,支付宝账号已经设置好

        if(myBalance == null || myBalance.compareTo(new BigDecimal(0)) <= 0
                || extend == null || TextUtils.isEmpty(extend.getZfb_acct())) {
            LogHelper.debug(this, "resetSubmit ==> myBalance["+myBalance+"]");
            //不能提交
            mBtnSubmit.setClickable(false);
            mBtnSubmit.setBackgroundResource(R.drawable.ta_shape_yuanjiao_cancel);

        } else {
            //激话按钮
            mBtnSubmit.setClickable(true);
            mBtnSubmit.setBackgroundResource(R.drawable.ta_btn_yuanjiao_pink_selector);
        }
    }
}
