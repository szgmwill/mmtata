package com.tata.imta.wxapi;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.gotye.api.GotyeDelegate;
import com.gotye.api.GotyeUser;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.bean.User;
import com.tata.imta.bean.status.Sex;
import com.tata.imta.helper.CommonHelper;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.helper.LoginHelper;
import com.tata.imta.helper.WeixinSDKHelper;
import com.tata.imta.util.DateUtils;
import com.tata.imta.util.JsonUtils;
import com.tata.imta.util.ToastUtils;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

/**
 * Created by Will Zhang on 2015/5/30.
 *
 * 微信请求的回调返回统一入口
 */
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gotyeAPI.addListener(delegate);

        initViews();

        initEvents();

        WeixinSDKHelper.getAPI().handleIntent(getIntent(), this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        gotyeAPI.removeListener(delegate);

        super.onDestroy();
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initEvents() {
    }


    @Override
    public void onReq(BaseReq baseReq) {
        LogHelper.info(this, "WeiXin onReq==============:" + baseReq.toString());
    }

    @Override
    public void onResp(BaseResp baseResp) {

        int code = baseResp.errCode;
        String errmsg = baseResp.errStr;

        LogHelper.info(this, "WeiXin onResp============== type[" + baseResp.getType() + "] :errCode[" + code + "],errmsg[" + code + "]");

        //微信支付
        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("微信支付返回结果");
            builder.setMessage("WeiXin onResp==============:errCode["+code+"],errmsg["+code+"]");
            builder.show();
        }
        //微信登录
        else if (baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {

            //提示正在登录中
            showProgressDialog("正在登录...");

            String authResult = "授权成功";
            switch (code) {
                case BaseResp.ErrCode.ERR_OK:

                    //拿到code去处理后续获取用户信息
                    String authCode = ((SendAuth.Resp)baseResp).code;

                    //启动异步线程与微信服务端交互获取用户信息
                    putAsyncTask(new WeixinLoginTask(), authCode);

                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    authResult = "用户取消授权";
                    LoginHelper.cancelLogin(this);
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    authResult = "用户拒绝授权";
                    LoginHelper.cancelLogin(this);
                    break;
                default:
                    authResult = "用户操作异常";
                    LoginHelper.cancelLogin(this);
                    break;
            }

            ToastUtils.showShortToast(this, authResult);
        }

    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        WeixinSDKHelper.getAPI().handleIntent(intent, this);
    }

    //亲加服务回调统一处理,只重写你要处理的回调方法
    private GotyeDelegate delegate = new GotyeDelegate() {
        @Override
        public void onLogin(int code, GotyeUser currentLoginUser) {
            LogHelper.info(this, "onLogin --------------------:user[" + currentLoginUser.getName() + "][" + code + "]");
            dismissProgressDialog();

            LoginHelper.onGotyeLoginCallback(WXEntryActivity.this, code, currentLoginUser);
        }
    };

    /**
     * 微信服务端异步任务
     *
     */
    class WeixinLoginTask extends AsyncTask<String, Void, Void> {

        /**
         * 微信登录后的用户信息
         */
        private User loginUser;


        @Override
        protected Void doInBackground(String... params) {
            LogHelper.debug(this, "doInBackground================");
            String authCode = params[0];//授权码
            String userInfoJson = WeixinSDKHelper.getUserBaseInfo(authCode);
            if(!TextUtils.isEmpty(userInfoJson)) {
                loginUser = new User();
                //登录成功后跳转
                JSONObject userJson = JsonUtils.formatJson(userInfoJson);
                /**
                 * {
                 * "openid":"OPENID",
                 * "nickname":"NICKNAME",
                 * "sex":1,
                 * "province":"PROVINCE",
                 * "city":"CITY",
                 * "country":"COUNTRY",
                 * "headimgurl": "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0",
                 * "privilege":[
                 * "PRIVILEGE1",
                 * "PRIVILEGE2"
                 * ],
                 * "unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"
                 * }
                 */
                //目前暂以用户unionid作为token和服务器API交互的身份认证
                loginUser.setWxOpenId(userJson.getString("unionid"));//注意unionid和openid的区别
                loginUser.setNick(userJson.getString("nickname"));
                loginUser.setHead(userJson.getString("headimgurl"));
                int sexValue = userJson.getIntValue("sex");
                loginUser.setSex(sexValue == 2 ? Sex.BOY : Sex.GIRL);
                loginUser.setBirth(DateUtils.getDateFromDayStr("1995-01-01"));//微信用户信息里没有生日,给一个默认的
                loginUser.setCareer("请填写职业");
                loginUser.setLocation(CommonHelper.getCityNameByPingyin(userJson.getString("city")));
                loginUser.setSign("请自定义个性签名");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(loginUser == null) {
                ToastUtils.showShortToast(WXEntryActivity.this, "微信登录处理异常");
            } else {
                //进行后续登录处理
                LoginHelper.loginServer(WXEntryActivity.this, loginUser);
            }
        }
    }



}
