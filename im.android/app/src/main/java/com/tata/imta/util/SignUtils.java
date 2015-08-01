package com.tata.imta.util;

import android.text.TextUtils;
import android.util.Base64;

import com.tata.imta.helper.LogHelper;

import java.security.MessageDigest;

/**
 * 安全签名工具:客户端和服务端保持一致
 * @author zhangweirui
 *
 */
public class SignUtils {
	
	/**
	 * 安全对称密钥token,可考虑写死在代码里或者配置文件中
	 * 要和客户端对上
     * 存放客户端不安全
	 */
	private static final String SIGN_TOKEN = "a4ed87c12e784e16693e3bcdc998mmtata";
	
	/**
	 * 统一字符串编码
	 */
	private static final String CHART_SET = "UTF-8";
	
	/**
	 * 参与sign签名计算的参数名
	 */
//	private static Set<String>  fieldsToSign = new HashSet<String>();
//    static {
//		  fieldsToSign.add("reqUrl");
//        fieldsToSign.add("timestamp");
//        fieldsToSign.add("client");
//    }
    
    /**
     * 暂时采用较简单算法,不需要所有请求参数一同进来计算
     * 计算签名:签名规则：将reqUrl + client + timestamp + token 拼接一起计算
     * 
     */
    public static String makeSign(String reqURI, String client, String timestamp, String token) {
    	try {
//            LogHelper.debug(SignUtils.class, "sign token:"+token);
            if(TextUtils.isEmpty(token)) {
                //使用默认的
                token = SIGN_TOKEN;
            }
            String source = reqURI + client + timestamp + token;
            LogHelper.debug(SignUtils.class, "make sign source:" + source);
            byte[] hash = encryptSHA1(source);
            String hashString = byte2hex(hash);
            String base64Str = Base64.encodeToString(hashString.getBytes(), Base64.NO_WRAP);//Base64.NO_WRAP 编码时不要出现换行符
            return base64Str;
        } catch (Exception e) {
            e.printStackTrace();
        }
    	return "";
    }
    
    private static String byte2hex(byte[] b) {
        StringBuffer hs = new StringBuffer();
        String stmp = "";
        for (int i = 0; i < b.length; i++) {
            stmp = (Integer.toHexString(b[i] & 0XFF));
            if (stmp.length() == 1) {
                hs.append(0).append(stmp);
            } else {
                hs.append(stmp);
            }
        }
        return hs.toString();
    }
    
    private static byte[] encryptSHA1(String data) {
        byte[] bytes = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(data.getBytes(CHART_SET));
            bytes = digest.digest();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return bytes;
    }

}
