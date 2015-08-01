package com.tata.imta.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.pingplusplus.android.PaymentActivity;
import com.tata.imta.R;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.bean.AcctOrder;
import com.tata.imta.bean.ResultObject;
import com.tata.imta.bean.User;
import com.tata.imta.bean.UserExtend;
import com.tata.imta.bean.status.OrderStatus;
import com.tata.imta.bean.status.PayChannelType;
import com.tata.imta.bean.status.ServerAPI;
import com.tata.imta.helper.LoadDataFromServer;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.helper.ServerAPIHelper;
import com.tata.imta.helper.UserHelper;
import com.tata.imta.util.ShowUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Will Zhang on 2015/6/25.
 * 购买聊天服务支付页
 */
public class OrderPayActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_PAYMENT = 4;
    private static final String CHANNEL_ALIPAY = "alipay";
    private static final String CHANNEL_WECHAT = "wx";


    private TextView mTitleTV;
    private RelativeLayout mBackRL;
    private RelativeLayout mMoreRL;

    //订单信息视图
    private TextView mNickTV;
    //聊天服务费
    private TextView mPriceTV;
    private TextView mUnitTV;

    private TextView mTotalAmtTV;
    private TextView mAcctBlanceTV;
    private TextView mOrderIdTV;

    //支付类型选择
    private RelativeLayout mPayRL;
    private Button mBtnPayWX;
    private Button mBtnPayZFB;
    private Button mBtnPayAcct;

    //购买对象用户信息
    private User targetUser;

    //支付订单信息
    private AcctOrder order;

    //支付凭证信息
    private String payReqCharge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ta_activity_order_pay);

        initViews();

        initEvents();

        initData();
    }

    @Override
    protected void initViews() {
        //重置标题栏内容
        mTitleTV = (TextView) findViewById(R.id.ta_tv_topbar_title);
        mTitleTV.setText("订单确认");
        mBackRL = (RelativeLayout) findViewById(R.id.ta_rl_topbar_back);
        mMoreRL = (RelativeLayout) findViewById(R.id.ta_rl_topbar_more);
        mMoreRL.setVisibility(View.GONE);
//        editTV = (TextView) mMoreRL.findViewById(R.id.ta_tv_topbar_more);


        mNickTV = (TextView) findViewById(R.id.ta_tv_order_pay_nick);
        //聊天费率等
        mPriceTV = (TextView) findViewById(R.id.ta_item_unit_price_tv_price);
        mUnitTV = (TextView) findViewById(R.id.ta_item_unit_price_tv_unit);

        mTotalAmtTV = (TextView) findViewById(R.id.ta_tv_order_pay_totalamt);
        mAcctBlanceTV = (TextView) findViewById(R.id.ta_tv_order_pay_balance);

        mOrderIdTV = (TextView) findViewById(R.id.ta_tv_order_pay_order_id);

        mPayRL = (RelativeLayout) findViewById(R.id.ta_rl_pay_choice);
        mBtnPayWX = (Button) findViewById(R.id.ta_btn_pay_wx);
        mBtnPayZFB = (Button) findViewById(R.id.ta_btn_pay_zfb);
        mBtnPayAcct = (Button) findViewById(R.id.ta_btn_pay_acct);
    }

    @Override
    protected void initEvents() {
        mBackRL.setOnClickListener(this);
        mBtnPayZFB.setOnClickListener(this);
        mBtnPayWX.setOnClickListener(this);
        mBtnPayAcct.setOnClickListener(this);
    }

    private void initData() {
        Intent from = getIntent();
        targetUser = (User) from.getSerializableExtra("user");
        order = (AcctOrder) from.getSerializableExtra("order");
        String acct_balance = from.getStringExtra("acct_balance");

        mNickTV.setText(targetUser.getNick());

        //聊天资费
        UserExtend extend = targetUser.getExtend();
        if(extend != null) {
            BigDecimal priceBD = extend.getFee();
            if(priceBD != null) {
                findViewById(R.id.ta_rl_show_free).setVisibility(View.GONE);
                findViewById(R.id.ta_rl_show_price).setVisibility(View.VISIBLE);
                mPriceTV.setText(String.valueOf(priceBD));
                mUnitTV.setText(ShowUtils.showUnit(extend));
            }
        }

        mTotalAmtTV.setText(String.valueOf(order.getTotal_amt()));
        mAcctBlanceTV.setText(String.valueOf(new BigDecimal(acct_balance)));

        mOrderIdTV.setText(""+order.getOrder_id());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ta_rl_topbar_back:

                finish();

                break;
            case R.id.ta_btn_pay_acct:

                //余额支付

                break;
            case R.id.ta_btn_pay_zfb:
                //支付宝支付
//                String token = ServerAPIHelper.getQiniuUpToken(detailUser.getUserId());
//                showAlertDialog("测试七牛", token);

//                QiniuCloudLoader.testToken(detailUser.getUserId(), UserDetailActivity.this);

                break;
            case R.id.ta_btn_pay_wx:

                //启动微信支付
//                new PaymentTask(this, PayChannelType.WX).execute(order);


                //启动第三方支付ping++
//                new PaymentTask().execute(new PaymentRequest(CHANNEL_WECHAT, 100));

                startPay(payReqCharge, PayChannelType.WX);

                break;
        }
    }

    /**
     * 发起第三方支付
     */
    private void startPay(String charge, PayChannelType type) {
        LogHelper.debug(this, "startPay payReqCharge:" + charge);

        //首先,要先从后台获取支付凭证
        Map<String, Object>  paramMap = new HashMap<>();
        paramMap.put("order_id", order.getOrder_id());
        paramMap.put("pay_type", type.name());
        LoadDataFromServer getChargeTask = new LoadDataFromServer(this, ServerAPI.SERVER_API_GEN_CHARGE, paramMap);
        getChargeTask.getData(new LoadDataFromServer.DataCallBack() {
            @Override
            public void onDataCallBack(ResultObject ro) {
                JSONObject data = ServerAPIHelper.handleServerResult(OrderPayActivity.this, ro);
                if(data != null) {
                    //支付凭证
                    String sendCharge = data.getString("charge");
                    if (sendCharge == null) {
                        showAlertDialog("操作提示", "生成支付凭证失败");
                        return;
                    }

                    //调用ping++
                    Intent intent = new Intent();
                    String packageName = getPackageName();
                    ComponentName componentName = new ComponentName(packageName, packageName + ".wxapi.WXPayEntryActivity");
                    intent.setComponent(componentName);
                    intent.putExtra(PaymentActivity.EXTRA_CHARGE, sendCharge);

                    showProgressDialog("支付跳转中...");
                    startActivityForResult(intent, REQUEST_CODE_PAYMENT);
                }

            }
        });
    }

     /**
     * 支付处理的各种回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogHelper.debug(this, "onActivityResult : requestCode[" + requestCode + "],resultCode[" + resultCode + "], " +
                "intend data:" + data);

        switch (requestCode) {
            case REQUEST_CODE_PAYMENT:
//                dismissProgressDialog();

                //支付结果回调
                if (resultCode == Activity.RESULT_OK) {

                    String result = data.getExtras().getString("pay_result");
                    /* 处理返回值
                     * "success" - payment succeed
                     * "fail"    - payment failed
                     * "cancel"  - user canceld
                     * "invalid" - payment plugin not installed
                     */
                    if("success".equalsIgnoreCase(result)) {

                        LogHelper.debug(this, "pay success!!!");
                        //同步到后台
                        order.setStatus(OrderStatus.PAID);

                    } else {
                        //支付失败
                        order.setStatus(OrderStatus.FAIL);
                        String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                        LogHelper.debug(this, "pay failed...result:"+result+",errorMsg:"+errorMsg);
                    }

                    //同步的支付结果信息
//                    order_id	long	是	订单号id
//                    pay_trade_no	string	否	支付成功后的交易流水号，第三方给出
//                    status	string	是	支付成功状态码PAID-支付成功；FAIL-支付失败；
                    order.setPay_trade_no("");

                    LoadDataFromServer syncOrderStatusTask = new LoadDataFromServer(this, ServerAPI.SERVER_API_SYNC_PAY_RESULT, order);

                    syncOrderStatusTask.getData(new LoadDataFromServer.DataCallBack() {
                        @Override
                        public void onDataCallBack(ResultObject ro) {
                            dismissProgressDialog();
                            if(order.getStatus() == OrderStatus.PAID) {
                                //成功
                                showConfirmDialog("支付成功,开始聊天?", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        //调转聊天页面
                                        UserHelper.goChat(OrderPayActivity.this, targetUser.getUserId());
                                        finish();
                                    }
                                });
                            } else {
                                showAlertDialog("操作提示", "支付失败");
                            }


                        }
                    });

                } else if (resultCode == Activity.RESULT_CANCELED) {
                    showAlertDialog("支付结果", "User canceled");
                } else {
                    showAlertDialog("失败", "123");
                }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
