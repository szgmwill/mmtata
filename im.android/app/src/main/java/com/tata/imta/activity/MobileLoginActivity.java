package com.tata.imta.activity;

import android.os.Bundle;
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
import com.tata.imta.helper.LoadDataFromServer;
import com.tata.imta.helper.LoginHelper;
import com.tata.imta.helper.ServerAPIHelper;
import com.tata.imta.util.MD5Utils;
import com.tata.imta.util.ViewInjectUtils;
import com.tata.imta.view.HandyTextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by will on 2015/4/26.
 * 手机登录页
 */
@ContentView(R.layout.ta_activity_mobile_login)
public class MobileLoginActivity extends BaseActivity implements View.OnClickListener{

    @ViewInject(R.id.ta_tv_topbar_title)
    private TextView mTitleTV;
    @ViewInject(R.id.ta_rl_topbar_back)
    private RelativeLayout mBackRL;
    @ViewInject(R.id.ta_rl_topbar_more)
    private RelativeLayout mMoreRL;

    //页面组件定义
    @ViewInject(R.id.ta_et_login_mobile)
    private EditText mMobileET;

    @ViewInject(R.id.ta_et_login_pwd)
    private EditText mPwdET;

    @ViewInject(R.id.ta_htv_login_forgotpassword)
    private HandyTextView mForgetPW;

    @ViewInject(R.id.ta_btn_go_login)
    private Button mLoginBtn;

    @ViewInject(R.id.ta_btn_go_register)
    private Button mRegBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewInjectUtils.inject(this);

        gotyeAPI.addListener(delegate);

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
        mTitleTV.setText("手机号登录");

        mMoreRL.setVisibility(View.GONE);


    }

    @Override
    protected void initEvents() {
        mBackRL.setOnClickListener(this);

        mLoginBtn.setOnClickListener(this);
        mLoginBtn.setClickable(false);

        mRegBtn.setOnClickListener(this);

        mMobileET.addTextChangedListener(new MyTextChangedListener());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ta_rl_topbar_back :
                finish();
                break;
            case R.id.ta_btn_go_register ://跳去注册页面
                startActivity(MobileRegisterActivity.class);

                finish();

                break;
            case R.id.ta_btn_go_login ://调用后台验证登录

                String mobile = mMobileET.getText().toString();
                String pwd = mPwdET.getText().toString();

                if(TextUtils.isEmpty(mobile) || TextUtils.isEmpty(pwd)) {
                    showAlertDialog("操作提示", "手机号和密码都必填");
                } else {
                    showProgressDialog("正在验证中...");
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("mobile", mobile);
                    //这里密码必须加md5后传输
                    paramMap.put("password", MD5Utils.MD5Encode(pwd));

                    LoadDataFromServer checkLoginTask = new LoadDataFromServer(this, ServerAPI.SERVER_API_MOBILE_LOGIN,
                            paramMap);

                    checkLoginTask.getData(new LoadDataFromServer.DataCallBack() {
                        @Override
                        public void onDataCallBack(ResultObject result) {
                            dismissProgressDialog();
                            if(result == null || result.getCode() != ServerAPIHelper.ServerOK) {
                                showAlertDialog("操作提示", "后台失败:"+(result == null ? "" : result.getMsg()));
                            } else {
                                showProgressDialog("正在登录...");
                                //开始登录
                                //得到注册用户id
                                JSONObject json = ServerAPIHelper.handleServerResult(MobileLoginActivity.this, result);
                                Long userId = json.getLong("user_id");

                                User user = new User();
                                user.setUserId(userId);
                                //后台登录成功后,更新信息
                                LoginHelper.resetLoginUser(MobileLoginActivity.this, user);
                            }
                        }
                    });
                }

                break;


        }

    }

    /**
     * 是否激活按钮
     */
    private void isActiveBtn(boolean active) {
        if(active) {
            mLoginBtn.setClickable(true);
            mLoginBtn.setBackgroundResource(R.drawable.ta_btn_pink_selector);
        } else {
            mLoginBtn.setClickable(false);
            mLoginBtn.setBackgroundResource(R.color.ta_color_btn_cancel);
        }
    }

    /**
     * 监听输入框的变化来激活按钮
     */
    class MyTextChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.length() > 0) {
                isActiveBtn(true);
            } else {
                isActiveBtn(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    //亲加服务回调统一处理,只重写你要处理的回调方法
    private GotyeDelegate delegate = new GotyeDelegate() {
        @Override
        public void onLogin(int code, GotyeUser currentLoginUser) {
            dismissProgressDialog();
            LoginHelper.onGotyeLoginCallback(MobileLoginActivity.this, code, currentLoginUser);
        }
    };
}
