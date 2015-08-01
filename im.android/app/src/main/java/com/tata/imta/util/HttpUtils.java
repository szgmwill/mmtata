package com.tata.imta.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

    public static String doGet(String url, Map<String, String> params) throws IOException {
        HttpClient client = new DefaultHttpClient();

        client.getParams().setParameter(
                CoreConnectionPNames.CONNECTION_TIMEOUT, DEFAULT_TIMEOUT);
        // 请求超时
        client.getParams().setParameter(
                CoreConnectionPNames.SO_TIMEOUT, DEFAULT_TIMEOUT);

        client.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, DEFAULT_CHARSET);

        HttpGet get = new HttpGet(url + buildGetQuery(params, DEFAULT_CHARSET));


        HttpResponse response = client.execute(get);

        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            InputStream is = response.getEntity().getContent();
            String result = inStream2String(is);

            return result;
        }

        return null;
    }

    public static String doPost(String url, Map<String, String> params) throws IOException {
        HttpClient client = new DefaultHttpClient();

        client.getParams().setParameter(
                CoreConnectionPNames.CONNECTION_TIMEOUT, DEFAULT_TIMEOUT);
        // 请求超时
        client.getParams().setParameter(
                CoreConnectionPNames.SO_TIMEOUT, DEFAULT_TIMEOUT);

        client.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, DEFAULT_CHARSET);

        HttpPost post = new HttpPost(url);
        post.setEntity(buildPostEntity(params));

        HttpResponse response = client.execute(post);

        if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            InputStream is = response.getEntity().getContent();
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
            if (!TextUtils.isEmpty(name)) {
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
    private static HttpEntity buildPostEntity(Map<String, String> params) throws IOException {
        List<NameValuePair> postList = new ArrayList<NameValuePair>();

        Set<Entry<String, String>> entries = params.entrySet();

        for (Entry<String, String> entry : entries) {
            String name = entry.getKey();
            String value = entry.getValue() == null ? "" : entry.getValue();

            // 忽略参数名为空的参数，参数值为空需要传递EmptyString
            if (!TextUtils.isEmpty(name)) {
                postList.add(new BasicNameValuePair(name, value));
            }
        }

        StringEntity postEntity = new UrlEncodedFormEntity(postList, HTTP.UTF_8);
        return postEntity;
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
     * 网络是否可用
     * 判断设备联网状态
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if (info != null) {
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }
}
