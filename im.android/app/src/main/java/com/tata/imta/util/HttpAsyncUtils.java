package com.tata.imta.util;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by Will Zhang on 2015/5/31.
 * 使用开源框架:android-async-http 实现的异步http网络请求处理工具类
 */
public class HttpAsyncUtils {

    /**
     * 官方建议使用static 方式
     */
    private static AsyncHttpClient client = new AsyncHttpClient();

    private static final String BASE_URL = "http://api.twitter.com/1/";

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
