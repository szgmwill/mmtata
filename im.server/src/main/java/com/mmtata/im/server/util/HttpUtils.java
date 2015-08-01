package com.mmtata.im.server.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;

/**
 * Created by Will Zhang on 2015/5/15.
 */
public class HttpUtils {
    private static final String DEFAULT_CHARSET = "UTF-8";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_GET = "GET";
    /**
     * time out setting
     */
    private static final int DEFAULT_TIMEOUT = 6000;

    public static String doGet(String host, int port, String uri, Map<String, String> params) throws IOException {
    	HttpClient client = new HttpClient();
    	client.getHostConfiguration().setHost(host, port);
    	client.getParams().setSoTimeout(DEFAULT_TIMEOUT);
    	client.getParams().setConnectionManagerTimeout(DEFAULT_TIMEOUT);
    	client.getParams().setContentCharset(DEFAULT_CHARSET);
    	
        GetMethod get = new GetMethod(uri + buildGetQuery(params, DEFAULT_CHARSET));
        
        int status = client.executeMethod(get);
        
        if(status == 200) {
        	InputStream is = get.getResponseBodyAsStream();
        	String result = inStream2String(is);
        	return result;
        }
        return null;
    }

    public static String doPost(String host, int port, String uri, Map<String, String> params) throws IOException {
    	HttpClient client = new HttpClient();
    	client.getHostConfiguration().setHost(host, port);
    	client.getParams().setSoTimeout(DEFAULT_TIMEOUT);
    	client.getParams().setConnectionManagerTimeout(DEFAULT_TIMEOUT);
    	client.getParams().setContentCharset(DEFAULT_CHARSET);
    	
    	PostMethod post = new PostMethod(uri);
    	
    	post.setRequestBody(buildPostEntity(params));
    	
    	int status = client.executeMethod(post);
    	
    	if(status == 200) {
        	InputStream is = post.getResponseBodyAsStream();
        	String result = inStream2String(is);
        	return result;
        }
        return null;
    }

    /**
     * 根据map生成请求 url
     * @param params
     * @param charset
     * @return
     * @throws IOException
     */
    private static String buildGetQuery(Map<String, String> params, String charset)
            throws IOException {
        if (params == null || params.isEmpty()) {
            return null;
        }

        StringBuilder query = new StringBuilder();
        Set<Entry<String, String>> entries = params.entrySet();
        boolean hasParam = false;

        for (Entry<String, String> entry : entries) {
            String name = entry.getKey();
            String value = entry.getValue() == null ? "" : entry
                    .getValue();
            // 忽略参数名为空的参数，参数值为空需要传递EmptyString
            if (!StringUtils.isEmpty(name)) {
                if (hasParam) {
                    query.append("&");
                } else {
                    hasParam = true;
                }

                query.append(name).append("=")
                        .append(URLEncoder.encode(value, charset));
            }
        }

        return "?" + query.toString();
    }

    /**
     * 生成post请求参数
     */
    private static NameValuePair[] buildPostEntity(Map<String, String> params) throws IOException {
    	NameValuePair[] reqBody = new NameValuePair[params.size()];

        Set<String> keySet = params.keySet();
        
        Iterator<String> it = keySet.iterator();
        int i = 0;
        while(it.hasNext()) {
        	String key = it.next();
        	reqBody[i] = new NameValuePair();
        	reqBody[i].setName(key);
        	reqBody[i].setValue(params.get(key));
        	
        	i++;
        }
        
        return reqBody;
    }

    /**
     * 文件上传类
     */
//    private static MultipartEntity buildMultiEntity(File upload , Map<String, String> params) {
//
//    }

    // 将输入流转换成字符串
    private static String inStream2String(InputStream is) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = -1;
        while ((len = is.read(buf)) != -1) {
            byteArrayOutputStream.write(buf, 0, len);
        }
        return new String(byteArrayOutputStream.toByteArray() , DEFAULT_CHARSET);
    }
    
    /**
	 * 获取http的所有请求参数
	 */
	public static String getAllParamFromHttpReq(HttpServletRequest request) {
		Enumeration paramNames = request.getParameterNames();
		StringBuffer sb = new StringBuffer();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();

			String[] paramValues = request.getParameterValues(paramName);
			if (paramValues.length == 1) {
				String paramValue = paramValues[0];
				if (paramValue.length() != 0) {
					sb.append("&").append(paramName).append("=")
							.append(paramValue);
				}
			}
		}

		return sb.toString();
	}
}
