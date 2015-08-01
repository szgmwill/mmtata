package com.mmtata.im.server.helper;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mmtata.im.server.bean.PingCharge;
import com.pingplusplus.Pingpp;
import com.pingplusplus.exception.PingppException;
import com.pingplusplus.model.Charge;

/**
 * 第三方支付平台帮助类
 * @author 张伟锐
 *
 */
public class PingPPHelper {
	private static final Logger logger = Logger.getLogger(PingPPHelper.class);
	
	/**
	 * pingpp 管理平台对应的API key
	 */
//	private static String apiKey = "sk_test_XXnPiTePSaHO0aP0iHeDqr9O";
	
	/**
     * pingpp 管理平台对应的API key
     * 正式环境
     */
    private static String apiKey = "sk_live_3NxXlX5zUmqqricxFl4PkzlL";
	
	/**
	 * pingpp 管理平台对应的应用I
	 */
	private static String appId = "app_z9mbDKOCejLSev1m";
	
	static {
		Pingpp.apiKey = apiKey;
	}
	
	/**
     * 创建Charge
     * 发起支付请求
     * @return
     */
    public static Charge sendReqCharge(PingCharge pc) {
        Charge charge = null;
        Map<String, Object> chargeMap = new HashMap<String, Object>();
        /**
         * 订单总金额, 单位为对应币种的最小货币单位，例如：人民币为分（如订单总金额为 1 元，此处请填 100）
         */
        chargeMap.put("amount", pc.getAmount());
        chargeMap.put("currency", "cny");//默认都是人民币
        /**
         * 商品的标题，该参数最长为 32 个 Unicode 字符，银联限制在 32 个字节
         */
        chargeMap.put("subject", pc.getSubject());
        chargeMap.put("body", pc.getBody());
        /**
         * 商户订单号，适配每个渠道对此参数的要求，
         * 必须在商户系统内唯一。(alipay: 1-64 位， wx: 1-32 位，bfb: 1-20 位，upacp: 8-40 位，
         * yeepay_wap:1-50 位，jdpay_wap:1-30 位，推荐使用 8-20 位，要求数字或字母，不允许特殊字符)
         */
        chargeMap.put("order_no", pc.getOrder_no());//就是平台生成的唯一订单号
        chargeMap.put("channel", pc.getChannel());//支付渠道,目前只支持微信或者支付宝
        chargeMap.put("client_ip", "120.24.239.210");//真实的服务器公网IP
        Map<String, String> app = new HashMap<String, String>();
        app.put("id", appId);
        chargeMap.put("app", app);
        try {
            //发起交易请求,ping++后台会预处理该笔订单支付
            charge = Charge.create(chargeMap);
            System.out.println(charge);
        } catch (PingppException e) {
            e.printStackTrace();
            logger.error("发起支付请求处理异常:", e);
        }
        return charge;
    }
}
