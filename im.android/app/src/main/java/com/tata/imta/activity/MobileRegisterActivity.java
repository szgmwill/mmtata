package com.tata.imta.activity;

import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.gotye.api.GotyeDelegate;
import com.gotye.api.GotyeUser;
import com.tata.imta.R;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.app.ContentView;
import com.tata.imta.app.ViewInject;
import com.tata.imta.bean.ResultObject;
import com.tata.imta.bean.User;
import com.tata.imta.bean.status.ServerAPI;
import com.tata.imta.bean.status.Sex;
import com.tata.imta.component.SMSReceiver;
import com.tata.imta.helper.LoadDataFromServer;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.helper.LoginHelper;
import com.tata.imta.helper.ServerAPIHelper;
import com.tata.imta.util.JsonUtils;
import com.tata.imta.util.MD5Utils;
import com.tata.imta.util.MyDialogUtils;
import com.tata.imta.util.ToastUtils;
import com.tata.imta.util.ViewInjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by Will Zhang on 2015/5/4.
 */
@ContentView(R.layout.ta_activity_mobile_register)
public class MobileRegisterActivity extends BaseActivity implements View.OnClickListener {

    // 填写从短信SDK应用后台注册得到的APPKEY
    private static String APPKEY = "8b19d398be09";

    // 填写从短信SDK应用后台注册得到的APPSECRET
    private static String APPSECRET = "32f81b61c04b7175761d764afcfe1151";

    //短信服务国家代码,目前只限中国大陆
    private static String countryCode = "86";

    @ViewInject(R.id.ta_tv_topbar_title)
    private TextView mTitleTV;
    @ViewInject(R.id.ta_rl_topbar_back)
    private RelativeLayout mBackRL;
    @ViewInject(R.id.ta_rl_topbar_more)
    private RelativeLayout mMoreRL;

    //页面视图
    @ViewInject(R.id.ta_et_reg_mobile)
    private EditText mMobileET;

    @ViewInject(R.id.ta_et_reg_pwd)
    private EditText mPwdET;

    @ViewInject(R.id.ta_et_reg_checkcode)
    private EditText mCheckCodeET;

    @ViewInject(R.id.ta_btn_reg_sendcode)
    private Button mSendCodeBtn;

    @ViewInject(R.id.ta_btn_go_register)
    private Button mSubmitBtn;

    //接收短信
    private SMSReceiver smsReceiver;
    //倒计时计时器
    private MySmsCountDownTimer timer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewInjectUtils.inject(this);

        gotyeAPI.addListener(delegate);

        // 初始化短信SDK
        SMSSDK.initSDK(this, APPKEY, APPSECRET);

        // 注册回调监听接口
        SMSSDK.registerEventHandler(smsHandler);

        //查询国家地区
//        SMSSDK.getSupportedCountries();//EventHandler回调

        smsReceiver = new SMSReceiver(new SMSSDK.VerifyCodeReadListener() {
            @Override
            public void onReadVerifyCode(final String verifyCode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //自动给填到验证码框里
                        mCheckCodeET.setText(verifyCode);
                    }
                });
            }
        });
        //注册广播接收短信通知
        registerReceiver(smsReceiver, new IntentFilter(
                "android.provider.Telephony.SMS_RECEIVED"));


        timer = new MySmsCountDownTimer(60000, 1000);

        initViews();

        initEvents();
    }

    @Override
    protected void onDestroy() {
        gotyeAPI.removeListener(delegate);
        // 销毁回调监听接口
        SMSSDK.unregisterAllEventHandler();

        super.onDestroy();
    }

    @Override
    protected void initViews() {
        mTitleTV.setText("注册新用户");

        mMoreRL.setVisibility(View.GONE);
    }
    @Override
    protected void initEvents() {
        mBackRL.setOnClickListener(this);

        mMobileET.addTextChangedListener(new MyTextChangedListener());

        mSendCodeBtn.setOnClickListener(this);

        mSubmitBtn.setOnClickListener(this);
        mSubmitBtn.setClickable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ta_rl_topbar_back:
                finish();
                break;
            case R.id.ta_btn_reg_sendcode://点击发送验证码到手机

                String phone = mMobileET.getText().toString();
                if(TextUtils.isEmpty(phone)) {
                    showAlertDialog("操作提示", "请填写正确手机号");
                } else {
                    MyDialogUtils.showSendSMSDialog(MobileRegisterActivity.this, countryCode, phone);
                }
                break;
            case R.id.ta_btn_go_register://调用后台注册新手机用户
                showProgressDialog("正在注册验证...");

                //校验填写
                String mobile = mMobileET.getText().toString();
                String checkcode = mCheckCodeET.getText().toString();

                SMSSDK.submitVerificationCode(countryCode, mobile, checkcode);//EventHandler回调

                break;
        }

    }

    /**
     * 是否激活按钮
     */
    private void isActiveBtn(boolean active) {
        if (active) {
            mSubmitBtn.setClickable(true);
            mSubmitBtn.setBackgroundResource(R.drawable.ta_btn_pink_selector);
        } else {
            mSubmitBtn.setClickable(false);
            mSubmitBtn.setBackgroundResource(R.color.ta_color_btn_cancel);
        }
    }

    /**
     * 向后台注册新用户
     */
    private void register() {
        showProgressDialog("注册新用户...");
        String mobile = mMobileET.getText().toString();
        String password = mPwdET.getText().toString();

        final User user = new User();
        user.setMobile(mobile);
        //这里密码必须md5后传输
        user.setPwd(MD5Utils.MD5Encode(password));
        //默认是男性
        user.setSex(Sex.BOY);
        //由于手机用户很多信息注册时没有,后台会自动补充一些

        LoadDataFromServer regTask = new LoadDataFromServer(this, ServerAPI.SERVER_API_REGISTER, user);

        regTask.getData(new LoadDataFromServer.DataCallBack() {
            @Override
            public void onDataCallBack(ResultObject result) {
                dismissProgressDialog();
                JSONObject json = ServerAPIHelper.handleServerResult(MobileRegisterActivity.this, result);

                if(result == null || result.getCode() != ServerAPIHelper.ServerOK) {
                    showAlertDialog("操作提示", "后台处理失败:"+(result == null ? "" : result.getMsg()));
                } else {
                    showProgressDialog("登录中...");
                    Long userId = json.getLong("user_id");
                    LogHelper.debug(MobileRegisterActivity.this, "新用户注册成功:" + userId);
                    user.setUserId(userId);

                    //注册成功后自动登录
                    LogHelper.debug(MobileRegisterActivity.this, "register new user succ,now login ==>");
                    LoginHelper.resetLoginUser(MobileRegisterActivity.this, user);
                }
            }
        });
    }

    /**
     * 倒计时计时器
     */
    class MySmsCountDownTimer extends CountDownTimer {

        /**
         * 总时长和间隔时间
         */
        public MySmsCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        /**
         * 每一次触发动作
         */
        @Override
        public void onTick(long millisUntilFinished) {
            String showRetry = millisUntilFinished/1000 + "秒后重发";
            mSendCodeBtn.setText(showRetry);
            //不可点击
            mSendCodeBtn.setClickable(false);
        }

        /**
         * 当计时完成后动作
         */
        @Override
        public void onFinish() {
            //激活可重新发送
            mSendCodeBtn.setText("点击获取验证码");
            mSendCodeBtn.setClickable(true);
        }
    };

    /**
     * 监听输入框的变化来激活按钮
     */
    class MyTextChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 0) {
                isActiveBtn(true);
            } else {
                isActiveBtn(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    /**
     * 短信回调处理
     * 在EventHandler的4个回调方法都可能不在UI线程下，因此如果要在其中执行UI操作，请务必使用Handler发送一个消息给UI线程处理
     */
    EventHandler smsHandler = new EventHandler() {

        @Override
        public void afterEvent(int event, int result, Object data) {


            if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //处理主线程事件必须采用异步
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //提交验证码成功,验证码校验
                            LogHelper.debug(MobileRegisterActivity.this, "校验码校验成功 ==> ");
                            register();
                        }
                    });

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功

                    //生成倒计时
                    if(timer != null) {
                        timer.start();
                    }

                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    //返回支持发送验证码的国家列表
                    List<HashMap<String, Object>> countryList = (ArrayList<HashMap<String, Object>>) data;
                    LogHelper.debug(MobileRegisterActivity.class, "countryList ==> :"+ JsonUtils.toJson(countryList));
                }
            } else {
                LogHelper.error(MobileRegisterActivity.class, "sms call back error:"+result);

                if(result == SMSSDK.RESULT_ERROR && event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MobileRegisterActivity.this.dismissProgressDialog();
                            ToastUtils.showShortToast(MobileRegisterActivity.this, "短信验证码错误");
                        }
                    });

                }
            }
        }

    };

    //亲加服务回调统一处理,只重写你要处理的回调方法
    private GotyeDelegate delegate = new GotyeDelegate() {
        @Override
        public void onLogin(int code, GotyeUser currentLoginUser) {
            dismissProgressDialog();
            LoginHelper.onGotyeLoginCallback(MobileRegisterActivity.this, code, currentLoginUser);
        }
    };
}
