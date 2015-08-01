package com.mmtata.im.server.util;

import java.security.MessageDigest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.thoughtworks.xstream.core.util.Base64Encoder;

/**
 * 安全签名工具
 * @author zhangweirui
 *
 */
public class SignUtils {
	
	private static final Logger logger = Logger.getLogger(SignUtils.class);
	
	/**
	 * 安全对称密钥token,可考虑写死在代码里或者配置文件中
	 * 要和客户端对上
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
    
	public static String makeSign(String reqURI, String client, String timestamp) {
		return makeSign(reqURI, client, timestamp, null);
	}
	
    /**
     * 暂时采用较简单算法,不需要所有请求参数一同进来计算
     * 计算签名:签名规则：将reqURI + client + timestamp + token 拼接一起计算
     * 
     */
    public static String makeSign(String reqURI, String client, String timestamp, String token) {
    	if(StringUtils.isNotBlank(client) && StringUtils.isNotBlank(timestamp) && StringUtils.isNotBlank(reqURI)) {
    		
    		if(StringUtils.isBlank(token)) {
    			token = SIGN_TOKEN;
    		}
    		
    		String source = reqURI + client + timestamp + token;
    		logger.debug("make sign source:"+source);
//    		System.out.println("source:"+source);
    		byte[] hash = encryptSHA1(source);
    		String hashString = byte2hex(hash);
//    		System.out.println("hashString:"+hashString);
    		Base64Encoder encoder = new Base64Encoder();
    		return encoder.encode(hashString.getBytes());
    	}
    	
    	return "";
    }
    
//    public static String makeSignOrigin(String reqURI, String client, String timestamp) {
//    	if(StringUtils.isNotBlank(client) && StringUtils.isNotBlank(timestamp) && StringUtils.isNotBlank(reqURI)) {
//    		
//    		String source = reqURI + client + timestamp + SIGN_TOKEN;
////    		System.out.println("source:"+source);
//    		byte[] hash = encryptSHA1(source);
//    		String hashString = byte2hex(hash);
////    		System.out.println("hashString:"+hashString);
//    		return Base64.getEncoder().encodeToString(hashString.getBytes());
//    	}
//    	
//    	return "";
//    }
    
    private static String byte2hex(byte[] b) {
        StringBuffer hs = new StringBuffer();
        String stmp = "";
        for (int i = 0; i < b.length; i++) {
            stmp = (java.lang.Integer.toHexString(b[i] & 0XFF));
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
    
    public static void main(String[] args) {
		String client = "android";
		String timestamp = "1432396605768";
		String uri = "/login/test";
		
		String encode = SignUtils.makeSign(uri, client, timestamp, "");
//		String origin = SignUtils.makeSignOrigin(uri, client, timestamp);
		System.out.println("encode:"+encode);
		
//		System.out.println("origin:"+origin);
	}
}
