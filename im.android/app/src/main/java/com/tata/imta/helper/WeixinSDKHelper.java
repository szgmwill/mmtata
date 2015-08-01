package com.tata.imta.helper;

import android.content.Context;
import android.text.TextUtils;
import android.util.Xml;

import com.alibaba.fastjson.JSONObject;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.bean.AcctOrder;
import com.tata.imta.util.HttpUtils;
import com.tata.imta.util.JsonUtils;
import com.tata.imta.util.MD5Utils;
import com.tata.imta.util.NetworkUtils;
import com.tata.imta.util.ToastUtils;
import com.tata.imta.util.Util;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Will Zhang on 2015/5/30.
 * 封装与微信app和服务端的交互
 */
public class WeixinSDKHelper {
    /**
     * 微信平台注册的appid
     *
     */
    private static final String WX_APP_ID = "wx43f96fb249d11759";

    /**
     * 微信平台注册的AppSecret
     *
     */
    private static final String WX_App_Secret = "329a04ccaf2864c3b90c49f83c9f6ef2";

    /**
     * 微信支付商户号
     */
    private static final String WX_MCH_ID = "1245810502";

    /**
     * 微信支付APP应用密钥
     * TaTaIN20150505987654321000000000
     * TaTaIN20151234567890000000000000
     */
    private static final String WX_MCH_APP_KEY = "TaTaIN20150505987654321000000000";

    /**
     * 请求access token url
     */
    private static final String GET_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";

    /**
     * 请求用户信息URL
     */
    private static final String GET_USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo";

    /**
     * app与微信侧通迅通道
     * 单例
     */
    private static IWXAPI api;

    public static void initAPI(Context context) {
        if(api == null) {
            LogHelper.debug(context, "now init wx api");
            // 通过WXAPIFactory工厂，获取IWXAPI的实例
            api = WXAPIFactory.createWXAPI(context, WX_APP_ID, false);
        }
    }

    /**
     * 初始化:向微信注册应用
     * 只需要调用一次即可,可放在程序启动处
     */
    public static void regToWeiXin(Context context) {
        LogHelper.debug(context, "now regToWeiXin");

        // 将该app注册到微信
        boolean isRegOK = api.registerApp(WX_APP_ID);

        LogHelper.debug(context, "regToWeiXin:" + isRegOK);
    }

    /**
     * 判断本地是否安装微信
     */
    public static boolean hasInstalledWeixin() {
        if(api.isWXAppInstalled()) {
            return true;
        }

        return false;
    }

    /**
     * 授权登录
     */
    public static void sendAuthLogin(BaseActivity activity) {

        if (!api.isWXAppInstalled()) {
            //提醒用户没有按照微信

            ToastUtils.showShortToast(activity, "找不到微信,请先安装微信后登录");

            return;
        }

        activity.showProgressDialog("微信跳转中...");

        // send oauth request
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "com.tata.imta.test";
        boolean sendOK = api.sendReq(req);
        LogHelper.info(activity, "sendAuthLogin:"+sendOK);
    }

    public static IWXAPI getAPI() {
        return api;
    }

    /**
     * 发起微信支付
     *
     */
    public static String sendPayReq(AcctOrder order) {
        if (!api.isWXAppInstalled()) {
            //提醒用户没有按照微信

            return "找不到微信,请先安装微信后登录";
        }

        //先得到预付订单id
        String prepareId = genPreparePayId(order);
        LogHelper.debug(WeixinSDKHelper.class, "prepareId:"+prepareId);
        if(prepareId == null) {
            return "生成预付单失败";
        }
        //请求支付
        PayReq req = genPayReq(prepareId);

        api.sendReq(req);

        return null;
    }

    /**
     * 生成微信支付请求参数
     */
    private static PayReq genPayReq(String prepareId) {
        PayReq req = new PayReq();
        req.appId = WX_APP_ID;
        req.partnerId = WX_MCH_ID;
        req.prepayId = prepareId;
        req.packageValue = "Sign=WXPay";
        req.nonceStr = Util.genNonceStr();
        req.timeStamp = String.valueOf(Util.genTimeStamp());


        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

        req.sign = genAppSign(signParams);

        LogHelper.debug(WeixinSDKHelper.class, "genPayReq:"+req.toString());

        return req;
    }

    /**
     * 第二步：通过code获取access_token
     * 获取第一步的code后，请求以下链接获取access_token：
     * https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code

     * 参数说明
     * 参数	是否必须	说明
     * appid	是	应用唯一标识，在微信开放平台提交应用审核通过后获得
     * secret	是	应用密钥AppSecret，在微信开放平台提交应用审核通过后获得
     * code	是	填写第一步获取的code参数
     * grant_type	是	填authorization_code
     * 返回说明
     * 正确的返回：
     * {
     * "access_token":"ACCESS_TOKEN",
     * "expires_in":7200,
     * "refresh_token":"REFRESH_TOKEN",
     * "openid":"OPENID",
     * "scope":"SCOPE",
     * "unionid":"o6_bmasdasdsad6_2sgVt7hMZOPfL"
     * }
     * @param code
     * @return
     */
    private static JSONObject getAccessTokenFromCode(String code) {
        if(!TextUtils.isEmpty(code)) {
            Map<String, String> reqParam = new HashMap<>();
            reqParam.put("appid", WX_APP_ID);
            reqParam.put("secret", WX_App_Secret);
            reqParam.put("code", code);
            reqParam.put("grant_type", "authorization_code");


            try {
                String result = HttpUtils.doGet(GET_ACCESS_TOKEN_URL, reqParam);
                JSONObject json = JsonUtils.formatJson(result);
                if(json != null) {
                    Integer errcode = json.getInteger("errcode");
                    if(errcode != null && errcode > 0) {
                        LogHelper.debug(WeixinSDKHelper.class, "Get Token From WX Error:errcode["+errcode+"],errmsg["+json.getString("errmsg")+"]");
                    } else {
                        return json;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                LogHelper.error(WeixinSDKHelper.class, "获取token网络异常", e);
            }
        }
        return null;
    }

    /**
     * 第三步：获取用户个人信息（UnionID机制）
     * 请求说明
     * http请求方式: GET
     * https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
     * 参数说明
     * 参数	是否必须	说明
     * access_token	是	调用凭证
     * openid	是	普通用户的标识，对当前开发者帐号唯一
     * 返回说明
     * 正确的Json返回结果：
     * {
     *  "openid":"OPENID",
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
    public static String getUserBaseInfo(String code) {
        JSONObject tokenJson = getAccessTokenFromCode(code);
        if(tokenJson != null) {
            Map<String, String> reqParam = new HashMap<>();
            reqParam.put("access_token", tokenJson.getString("access_token"));
            reqParam.put("openid", tokenJson.getString("openid"));

            try {
                String result = HttpUtils.doGet(GET_USER_INFO_URL, reqParam);
                JSONObject json = JsonUtils.formatJson(result);
                if(json != null) {
                    Integer errcode = json.getInteger("errcode");
                    if(errcode != null && errcode > 0) {
                        LogHelper.debug(WeixinSDKHelper.class, "Get Userinfo From WX Error:errcode["+errcode+"],errmsg["+json.getString("errmsg")+"]");
                    } else {
                        return json.toJSONString();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                LogHelper.error(WeixinSDKHelper.class, "获取token网络异常", e);
            }
        }

        //to do
        return null;
    }

    /**
     * 微信支付生成预订单
     */
    private static String genPreparePayId(AcctOrder order) {
        String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
        //生成当前订单的支付凭证
        String entity = genProductArgs(order);
        if(entity != null) {
            byte[] buf = Util.httpPost(url, entity);

            String content = new String(buf);
            LogHelper.debug("orion", content);
            Map<String,String> xml = decodeXml(content);
            if(xml != null) {
                return xml.get("prepay_id");
            }
        }
        return null;
    }

    private static String genProductArgs(AcctOrder order) {
        try {
            //生成一个随机数
            String nonceStr = Util.genNonceStr();
            String desc = "陪你的Ta订单-"+order.getOrder_id();

            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            //微信支付预订单请求参数
            packageParams.add(new BasicNameValuePair("appid", WX_APP_ID));
            //商品描述
            packageParams.add(new BasicNameValuePair("body", desc));
            //商户号
            packageParams.add(new BasicNameValuePair("mch_id", WX_MCH_ID));
            packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
            //回调地址,不知道有啥用?
            packageParams.add(new BasicNameValuePair("notify_url", "http://121.40.35.3/test"));
            //支付订单号
            packageParams.add(new BasicNameValuePair("out_trade_no", String.valueOf(order.getOrder_id())));
            //当前请求客户端IP
            packageParams.add(new BasicNameValuePair("spbill_create_ip", NetworkUtils.getPhoneIp()));

            //订单总价:单位分
            BigDecimal amt = order.getTotal_amt();
            long total_fee = amt.multiply(new BigDecimal(100)).longValue();//将总价转换为分单位
            packageParams.add(new BasicNameValuePair("total_fee", String.valueOf(total_fee)));

            packageParams.add(new BasicNameValuePair("trade_type", "APP"));//支付类型:app支付


            String sign = genPackageSign(packageParams);
            packageParams.add(new BasicNameValuePair("sign", sign));


            String xmlstring = toXml(packageParams);
            LogHelper.debug(WeixinSDKHelper.class, "genProductArgs====>sign:"+sign+",xml:"+xmlstring);
            return xmlstring;

        } catch (Exception e) {
            LogHelper.debug(WeixinSDKHelper.class, "genProductArgs fail, ex = " + e.getMessage());
            return null;
        }
    }


    /**
     * 生成签名
     */
    private static String genPackageSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        //接接商户api密钥
        sb.append(WX_MCH_APP_KEY);

        LogHelper.debug(WeixinSDKHelper.class, "genPackageSign source:"+sb.toString());

        String packageSign = MD5Utils.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        LogHelper.debug(WeixinSDKHelper.class, packageSign);
        return packageSign;
    }

    private static String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(WX_MCH_APP_KEY);

        sb.append("sign str\n"+sb.toString()+"\n\n");
        String appSign = MD5Utils.getMessageDigest(sb.toString().getBytes()).toUpperCase();

        LogHelper.debug(WeixinSDKHelper.class, "appSign:"+appSign);

        return appSign;
    }

    private static String toXml(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < params.size(); i++) {
            sb.append("<"+params.get(i).getName()+">");
            sb.append(params.get(i).getValue());
            sb.append("</"+params.get(i).getName()+">");
        }
        sb.append("</xml>");

        LogHelper.debug(WeixinSDKHelper.class, sb.toString());
        return sb.toString();
    }

    public static Map<String,String> decodeXml(String content) {

        try {
            Map<String, String> xml = new HashMap<String, String>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {

                String nodeName=parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:

                        if("xml".equals(nodeName)==false){
                            //实例化student对象
                            xml.put(nodeName,parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
            LogHelper.debug(WeixinSDKHelper.class, e.toString());
        }
        return null;

    }
}
