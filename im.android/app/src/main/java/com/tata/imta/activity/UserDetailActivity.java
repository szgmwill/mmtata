package com.tata.imta.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gotye.api.GotyeDelegate;
import com.gotye.api.GotyeMessage;
import com.gotye.api.GotyeStatusCode;
import com.gotye.api.GotyeUser;
import com.pingplusplus.android.PaymentActivity;
import com.tata.imta.R;
import com.tata.imta.adapter.UserDetailTabAdapter;
import com.tata.imta.adapter.UserImgAdapter;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.bean.AcctOrder;
import com.tata.imta.bean.ImgInfo;
import com.tata.imta.bean.ResultObject;
import com.tata.imta.bean.User;
import com.tata.imta.bean.UserExtend;
import com.tata.imta.bean.status.OrderStatus;
import com.tata.imta.bean.status.PayChannelType;
import com.tata.imta.bean.status.ServerAPI;
import com.tata.imta.bean.status.TabInfo;
import com.tata.imta.constant.RequestCode;
import com.tata.imta.constant.ServerErrorCode;
import com.tata.imta.helper.ChatHelper;
import com.tata.imta.helper.LoadDataFromServer;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.helper.MyViewHelper;
import com.tata.imta.helper.ServerAPIHelper;
import com.tata.imta.helper.UserHelper;
import com.tata.imta.helper.WeixinSDKHelper;
import com.tata.imta.helper.holder.GotyeHelper;
import com.tata.imta.util.DateUtils;
import com.tata.imta.util.JsonUtils;
import com.tata.imta.util.ToastUtils;
import com.tata.imta.view.PopWindowMore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Will Zhang on 2015/6/5.
 * 用户的个人信息详情页
 */
public class UserDetailActivity extends BaseActivity implements View.OnClickListener{

    /**
     * 当前处理的头像图片索引
     */
//    private int headIndex = 0;

    /**
     * 当前显示的用户
     */
    private User detailUser;

    /**
     * 当前是否自己
     */
    private boolean isMe = false;

    //读出用户头像列表
    List<ImgInfo> headList = new ArrayList<>();

    /**
     * ================视图控件====start=============
     */
    private TextView mTitleTV;
    private RelativeLayout mBackRL;
    private RelativeLayout mMoreRL;
    private TextView editTV;

    private TextView mXinzuoTV;
    private TextView mLocationTV;

    //聊天服务费
    private RelativeLayout mFeeRL;

    //个性签名
    private TextView mSignTV;
    //职业
    private TextView mCareerTV;

    //性别年龄
    private RelativeLayout mSexAgeRL;

    //关注和购买按钮
    private RelativeLayout mFollowRL;
    private RelativeLayout mBuyRL;

    //关注和购买按钮文字
    private TextView mFollowTV;
    private TextView mBuyTV;

    //支付类型选择
    private RelativeLayout mPayRL;
    private Button mBtnPayWX;
    private Button mBtnPayZFB;
    private Button mBtnPayAcct;
    private Button mBtnPayCancel;

    //网络视图
    private GridView mHeadGV;
    private GridView mAbilityGV;
    private GridView mLanGV;

    //评价
    private RatingBar mPingjiaRB;


    //adapter
    private UserImgAdapter imgAdapter;
    private UserDetailTabAdapter abilityAdapter;
    private UserDetailTabAdapter lanAdapter;
    //=================视图控件=====end=============

    //当前正在处理的订单id
    private long order_id;
    private AcctOrder order;


    //当前是否进行支付操作
    private boolean isPayAction = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ta_activity_user_detail);

        gotyeAPI.addListener(delegate);

        //初始化视图
        initViews();

        //添加动作事件
        initEvents();

        //内容初始化
        initContent();
    }

    @Override
    protected void onDestroy() {
        gotyeAPI.removeListener(delegate);
        super.onDestroy();
    }

    @Override
    protected void initViews() {
        //重置标题栏内容
        mTitleTV = (TextView) findViewById(R.id.ta_tv_topbar_title);

        mBackRL = (RelativeLayout) findViewById(R.id.ta_rl_topbar_back);
        mMoreRL = (RelativeLayout) findViewById(R.id.ta_rl_topbar_more);
        editTV = (TextView) mMoreRL.findViewById(R.id.ta_tv_topbar_more);

        //年龄星座等
        mSexAgeRL = (RelativeLayout) findViewById(R.id.ta_item_sex_age_parent);

        mXinzuoTV = (TextView) findViewById(R.id.ta_user_detail_tv_xinzuo);
        mLocationTV = (TextView) findViewById(R.id.ta_user_detail_tv_location);

        mPingjiaRB = (RatingBar) findViewById(R.id.ta_rb_user_detail_pingjia);

        //聊天费率等
        mFeeRL = (RelativeLayout) findViewById(R.id.ta_item_unit_price_parent);

        mSignTV = (TextView) findViewById(R.id.ta_user_detail_tv_sign_content);
        mCareerTV = (TextView) findViewById(R.id.ta_user_detail_tv_sign_career);

        mFollowRL = (RelativeLayout) findViewById(R.id.ta_user_detail_btn_follow);
        mBuyRL = (RelativeLayout) findViewById(R.id.ta_user_detail_btn_buy);

        mFollowTV = (TextView) findViewById(R.id.ta_tv_user_detail_follow);
        mBuyTV = (TextView) findViewById(R.id.ta_tv_user_detail_buy);

        mPayRL = (RelativeLayout) findViewById(R.id.ta_rl_pay_choice);
        mBtnPayWX = (Button) findViewById(R.id.ta_btn_pay_wx);
        mBtnPayZFB = (Button) findViewById(R.id.ta_btn_pay_zfb);
        mBtnPayAcct = (Button) findViewById(R.id.ta_btn_pay_acct);
        mBtnPayCancel = (Button) findViewById(R.id.ta_btn_pay_cancel);

        //头像网络
        mHeadGV = (GridView) findViewById(R.id.ta_gv_user_detail_img);
        //我有能力网格
        mAbilityGV = (GridView) findViewById(R.id.ta_gv_user_detail_ability);
        //我懂的语言
        mLanGV = (GridView) findViewById(R.id.ta_gv_user_detail_lan);

    }

    @Override
    protected void initEvents() {
        //实现回退上一步
        mBackRL.setOnClickListener(this);
        mMoreRL.setOnClickListener(this);

        mFollowRL.setOnClickListener(this);
        mBuyRL.setOnClickListener(this);

        mBtnPayZFB.setOnClickListener(this);
        mBtnPayWX.setOnClickListener(this);
        mBtnPayAcct.setOnClickListener(this);
        mBtnPayCancel.setOnClickListener(this);
    }

    /**
     * 用户详情信息补充
     */
    private void initContent() {
        //读取传递过来的用户信息
        detailUser = (User) getIntent().getSerializableExtra("user");
        if(detailUser != null) {
            LogHelper.debug(this, "detailUser:"+ JsonUtils.toJson(detailUser));
            mTitleTV.setText(detailUser.getNick());

            //根据用户id,判断是否是当前登录用户,如果是的话,标题栏右上角可以直接跳转编辑页
            if(loginUser != null && loginUser.getUserId() == detailUser.getUserId()) {
                isMe = true;
                //显示保存按钮
                editTV.setVisibility(View.VISIBLE);
                editTV.setText("编辑");
                mMoreRL.findViewById(R.id.ta_iv_topbar_more).setVisibility(View.GONE);
            } else {
                editTV.setVisibility(View.GONE);
                mMoreRL.findViewById(R.id.ta_iv_topbar_more).setVisibility(View.VISIBLE);
            }

            //展示年龄性别
            MyViewHelper.showSexAgeView(mSexAgeRL, detailUser.getSex(), DateUtils.getAgeFromDate(detailUser.getBirth()));

            //星座
            mXinzuoTV.setText(DateUtils.star(detailUser.getBirth()));
            //城市
            mLocationTV.setText(detailUser.getLocation());

            //聊天资费
            UserExtend extend = detailUser.getExtend();
            if(extend != null) {
                //展示聊天资费
                MyViewHelper.showUnitPrice(mFeeRL, extend.getFee(), extend.getFee_unit(), extend.getFee_duration());
            }

            //职业个性签名
            if(!TextUtils.isEmpty(detailUser.getSign())) {
                mSignTV.setText(detailUser.getSign());
            }
            if(!TextUtils.isEmpty(detailUser.getCareer())) {
                mCareerTV.setText(detailUser.getCareer());
            }

            if(detailUser.getImgList() != null && detailUser.getImgList().size() > 0) {
                headList.clear();
                headList.addAll(detailUser.getImgList());
            }

//            currImgName = headList.get(headIndex).getUrl();

            if(detailUser.getTabList() != null && detailUser.getTabList().size() > 0) {
                mAbilityGV.setVisibility(View.VISIBLE);
                abilityAdapter = new UserDetailTabAdapter(this, detailUser.getTabList(), TabInfo.TAB_TYPE);
                mAbilityGV.setAdapter(abilityAdapter);
            }

            if(headList.size() > 0) {
                mHeadGV.setVisibility(View.VISIBLE);
                imgAdapter = new UserImgAdapter(this, headList);
                mHeadGV.setAdapter(imgAdapter);
            }

            if(detailUser.getTabList() != null && detailUser.getTabList().size() > 0) {
                mLanGV.setVisibility(View.VISIBLE);
                lanAdapter = new UserDetailTabAdapter(this, detailUser.getTabList(), TabInfo.TAB_LAN);
                mLanGV.setAdapter(lanAdapter);
            }

            //展示评价分数
            if(detailUser.getExtend() != null) {
                Float feedback = detailUser.getExtend().getFeedback();
                if(feedback == null) {
                    feedback = 5f;
                }
                mPingjiaRB.setRating(feedback);
            }
        }
        if(isMe) {
            //如果当前是我自己,不应该出现关注和购买按钮
            mFollowRL.setVisibility(View.GONE);
            mBuyRL.setVisibility(View.GONE);
        } else {
            //判断是否关注和是否已经购买过服务了
            if(detailUser != null) {
                if(GotyeHelper.isMyFriend(detailUser.getUserId())) {
                    mFollowTV.setText("已关注");
                    //不可点击
                    mFollowRL.setClickable(false);
                    mFollowRL.setBackgroundResource(R.drawable.ta_shape_yuanjiao_cancel);
                }

                //判断是否有关联未完成订单
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("user_id", loginUser.getUserId());
                paramMap.put("target_id", detailUser.getUserId());
                paramMap.put("status", OrderStatus.PAID);

                LoadDataFromServer queryPaidOrderTask = new LoadDataFromServer(UserDetailActivity.this,
                        ServerAPI.SERVER_API_QUERY_TARGET_ORDER_LIST, paramMap);

                queryPaidOrderTask.getData(new LoadDataFromServer.DataCallBack() {
                    @Override
                    public void onDataCallBack(ResultObject result) {
                        LogHelper.debug(UserDetailActivity.this, "queryPaidOrderTask callback ==>:"+JsonUtils.toJson(result));
                        JSONObject dataJson = ServerAPIHelper.handleServerResult(UserDetailActivity.this, result);

                        if(dataJson != null) {

                            JSONArray array = dataJson.getJSONArray("data_list");
                            if(array != null && array.size() > 0) {
                                LogHelper.debug(this, "server side ret data_list :"+array.size());
                                //我与对方有已经付款但没有确认完成的订单
                                //购买按钮不能购买

                                mBuyTV.setText("已购买");
                                //不可点击
                                mBuyRL.setClickable(false);
                                mBuyRL.setBackgroundResource(R.drawable.ta_shape_yuanjiao_cancel);
                            }
                        }

                    }
                });

            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ta_rl_topbar_back:
                this.back(v);
                break;
            case R.id.ta_rl_topbar_more:
                //判断是否编辑或者弹窗
                if(editTV.getVisibility() == View.VISIBLE) {

                    //去编辑页
                    startActivity(MyDetailActivity.class);
                    //同时关闭自身
                    finish();
                } else {
                    //弹窗更多选择
                    PopWindowMore popMore = new PopWindowMore(UserDetailActivity.this, detailUser.getUserId());
                    popMore.showPopupWindow(mMoreRL);
                }
                break;
            case R.id.ta_user_detail_btn_buy:

                UserExtend extend = detailUser.getExtend();

                if(extend == null) {
                    showAlertDialog("操作提示", "该用户是免费用户,不能购买服务");
                    return;
                }

                //按键点击之后的禁用，防止重复点击
//              mBuyRL.setClickable(false);

//                showProgressDialog("正在生成支付订单...");

                //调用后台生成预支付订单
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("user_id", loginUser.getUserId());
                params.put("target_id", detailUser.getUserId());
                params.put("buy_num", "1");
                params.put("fee", String.valueOf(extend.getFee()));
                params.put("fee_unit", extend.getFee_unit());
                params.put("fee_duration", extend.getFee_duration());
//                params.put("pay_type", "1");//支付类型

                LoadDataFromServer addOrderTask = new LoadDataFromServer(this, ServerAPI.SERVER_API_ADD_ORDER, params);

                addOrderTask.getData(new LoadDataFromServer.DataCallBack() {
                    @Override
                    public void onDataCallBack(ResultObject ro) {
                        dismissProgressDialog();
                        JSONObject data = ServerAPIHelper.handleServerResult(UserDetailActivity.this, ro);

                        //如果是业务错误,直接提示
                        if(ro != null && ro.getCode() != ServerErrorCode.OK) {
                            if(ro.getCode() == ServerErrorCode.FAIL_BIZ_ERROR) {
                                showAlertDialog("操作提示", ro.getMsg());
                            } else {
                                //其它错误
                                showAlertDialog("操作提示", "支付后台处理返回失败");
                            }

                            return;
                        }
                        //处理返回生成的订单
                        if(data != null) {
                            order_id = data.getLong("order_id");
                            LogHelper.debug(this, "后台生成预订单id:"+order_id);

                            order = new AcctOrder();

                            //成功后调转到支付页
//                            Intent i = new Intent(UserDetailActivity.this, OrderPayActivity.class);
//                            i.putExtra("user", detailUser);
                            order.setUser_id(loginUser.getUserId());
                            order.setTarget_id(detailUser.getUserId());
                            order.setOrder_id(order_id);
                            order.setTotal_amt(data.getBigDecimal("total_amt"));
                            order.setExpire_time(data.getDate("expire_time"));
//                            i.putExtra("order", order);
//                            i.putExtra("acct_balance", String.valueOf(data.getBigDecimal("acct_balance")));
//                            startActivity(i);


                            //弹出支付浮层
                            mPayRL.setVisibility(View.VISIBLE);
                            isPayAction = true;
                        }

                    }
                });



                break;

            case R.id.ta_user_detail_btn_follow:
                isPayAction = false;
                //关注,即加为好友
                GotyeUser addFriend = new GotyeUser(String.valueOf(detailUser.getUserId()));
                gotyeAPI.reqAddFriend(addFriend); ///< 对应回调GotyeDelegate中的onAddFriend，同时会更新本地好友列表

                break;

            case R.id.ta_btn_pay_acct:

                //余额支付
                startPay(PayChannelType.ACCT);
                break;
            case R.id.ta_btn_pay_zfb:
                //支付宝支付

                //判断是否安装支付宝

                //拉起支付
                startPay(PayChannelType.ZFB);

                break;
            case R.id.ta_btn_pay_wx:
                //微信支付

                //先判断本地是否安装微信
                if(!WeixinSDKHelper.hasInstalledWeixin()) {
                    ToastUtils.showShortToast(this, "请先安装微信后支付");
                    break;
                }

                startPay(PayChannelType.WX);
                break;
            case R.id.ta_btn_pay_cancel:
                //取消支付
                mPayRL.setVisibility(View.GONE);
                break;
            default:
                break;

        }
    }

    /**
     * 发起第三方支付
     */
    private void startPay(final PayChannelType type) {
        LogHelper.debug(this, "startPay channel:" + type);

        showProgressDialog("支付跳转中...");

        //首先,要先从后台获取支付凭证
        Map<String, Object>  paramMap = new HashMap<>();
        paramMap.put("order_id", order_id);
        paramMap.put("pay_type", type.name());
        LoadDataFromServer getChargeTask = new LoadDataFromServer(this, ServerAPI.SERVER_API_GEN_CHARGE, paramMap);
        getChargeTask.getData(new LoadDataFromServer.DataCallBack() {
            @Override
            public void onDataCallBack(ResultObject ro) {

                if (ServerAPIHelper.showErrorMsg(UserDetailActivity.this, ro)) {
                    return;
                }

                //如果是余额支付,直接显示结果,如果不是,拉起第三方支付
                if (type == PayChannelType.ACCT) {

                    AcctOrder order = new AcctOrder();
                    order.setOrder_id(order_id);
                    order.setStatus(OrderStatus.PAID);

                    showPayResult(order);

                } else {
                    JSONObject data = JsonUtils.formatDataObj(ro.getData());

                    if (data != null) {
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

                        startActivityForResult(intent, RequestCode.Request_Code_Payment);
                    }
                }


            }
        });
    }

    /**
     * 支付处理的各种回调
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogHelper.debug(this, "onActivityResult : requestCode[" + requestCode + "],resultCode[" + resultCode + "], " +
                "intend data:" + data);

        switch (requestCode) {
            case RequestCode.Request_Code_Payment:

                //支付结果回调
                if (resultCode == Activity.RESULT_OK) {

                    final AcctOrder order = new AcctOrder();
                    order.setOrder_id(order_id);

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
                            showPayResult(order);
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

    /**
     * 支付结果反馈
     */
    private void showPayResult(AcctOrder order) {
        dismissProgressDialog();
        if(order.getStatus() == OrderStatus.PAID) {

            //同时加关注
            GotyeUser addFriend = new GotyeUser(String.valueOf(detailUser.getUserId()));
            gotyeAPI.reqAddFriend(addFriend);

            //按钮变化
            mBuyTV.setText("已购买");
            //不可点击
            mBuyRL.setClickable(false);
            mBuyRL.setBackgroundResource(R.drawable.ta_shape_yuanjiao_cancel);

            //成功
            showConfirmDialog("支付成功,开始聊天?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //调转聊天页面
                    UserHelper.goChat(UserDetailActivity.this, detailUser.getUserId());
                    finish();
                }
            });
        } else {
            showAlertDialog("操作提示", "支付失败");
        }
    }



    //亲加服务回调统一处理,只重写你要处理的回调方法
    private GotyeDelegate delegate = new GotyeDelegate() {
        /**
         * 加为好友(关注)回调
         */
        @Override
        public void onAddFriend(int code, GotyeUser user) {
            if(code == GotyeStatusCode.CodeOK) {

                mFollowTV.setText("已关注");
                //不可点击
                mFollowRL.setClickable(false);
                mFollowRL.setBackgroundResource(R.drawable.ta_shape_yuanjiao_cancel);

                if(!isPayAction) {
                    //普通关注操作
                    showAlertDialog("操作提示","关注成功");

                    //关注对方的同时,发送一条谁谁关注了你的消息给对方
                    ChatHelper.sendAddFollowMsg(loginUser, detailUser);
                } else {
                    //支付成功捆绑关注操作
                    //支付成功后,除了加关注外,再发送一条已付款消息给对方
                    ChatHelper.sendPayMsg(loginUser,detailUser, order);
                }
            } else {
                ToastUtils.showShortToast(UserDetailActivity.this, "关注失败 "+code);
            }
        }

        @Override
        public void onRemoveFriend(int code, GotyeUser user) {
            super.onRemoveFriend(code, user);
            if(code == GotyeStatusCode.CodeOK) {
                //恢复按钮
                mFollowTV.setText("关注");
                mFollowRL.setClickable(true);
                mFollowRL.setBackgroundResource(R.drawable.ta_btn_yuanjiao_pink_selector);

                showAlertDialog("操作提示", "取消关注成功");
            } else {
                ToastUtils.showShortToast(UserDetailActivity.this, "取消关注失败 "+code);
            }
        }

        /**
         * 拉黑回调
         */
        @Override
        public void onAddBlocked(int code, GotyeUser user) {
            if(code == GotyeStatusCode.CodeOK) {
                showAlertDialog("操作提示", "拉黑成功");
            } else {
                ToastUtils.showShortToast(UserDetailActivity.this, "拉黑失败 "+code);
            }
        }

        /**
         * 取消拉黑
         */
        @Override
        public void onRemoveBlocked(int code, GotyeUser user) {
            super.onRemoveBlocked(code, user);
            if(code == GotyeStatusCode.CodeOK) {
                showAlertDialog("操作提示", "取消拉黑成功");
            } else {
                ToastUtils.showShortToast(UserDetailActivity.this, "取消拉黑失败 "+code);
            }
        }

        /**
         * 发送消息回调
         */
        @Override
        public void onSendMessage(int code, GotyeMessage message) {
            super.onSendMessage(code, message);
            LogHelper.debug(UserDetailActivity.this, "onSendMessage ==> message:"+JsonUtils.toJson(message));
        }
    };
}
