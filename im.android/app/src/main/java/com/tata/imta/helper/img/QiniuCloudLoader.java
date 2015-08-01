package com.tata.imta.helper.img;

import android.content.Context;
import android.text.TextUtils;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.tata.imta.bean.ResultObject;
import com.tata.imta.bean.status.ServerAPI;
import com.tata.imta.helper.LoadDataFromServer;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.helper.ServerAPIHelper;
import com.tata.imta.util.ToastUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Will Zhang on 2015/6/8.
 * 七牛图片工具处理
 */
public class QiniuCloudLoader {

    /**
     * 七牛下载时的域名
     */
    public static final String QINIU_DOMAIN = "http://7xjkrr.com1.z0.glb.clouddn.com/";

    /**
     * 上传控制管理器
     */
    private UploadManager uploadManager;

    /**
     * 当前图片所在的展示活动页
     */
    private Context context;

    public QiniuCloudLoader(Context context) {
        this.context = context;

        Configuration config = new Configuration.Builder()
                .chunkSize(256 * 1024)  //分片上传时，每片的大小。 默认 256K
                .putThreshhold(512 * 1024)  // 启用分片上传阀值。默认 512K
                .connectTimeout(10) // 链接超时。默认 10秒
                .responseTimeout(60) // 服务器响应超时。默认 60秒
//                .recorder(recorder)  // recorder 分片上传时，已上传片记录器。默认 null
//                .recorder(recorder, keyGen)  // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                .build();

        // 重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
        uploadManager = new UploadManager(config);
    }


    /**
     * 简单类型上传图片
     */
    public boolean uploadImg(final File file, final String key, final QiniuUploadedCallBack callBack) {

        if(file != null && file.exists() && !TextUtils.isEmpty(key)) {
            //先从后台获取上传凭证
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("user_id", "123");
            LoadDataFromServer getTokenTask = new LoadDataFromServer(context, ServerAPI.SERVER_API_QINIU_UPTOKEN, paramMap);

            getTokenTask.getData(new LoadDataFromServer.DataCallBack() {
                @Override
                public void onDataCallBack(ResultObject result) {
                    com.alibaba.fastjson.JSONObject data = ServerAPIHelper.handleServerResult(context, result);

                    if(data == null) {
                        ToastUtils.showShortToast(context, "服务器返回错误");
                        return;
                    }

                    String token = data.getString("uptoken");
                    LogHelper.debug(this, "从服务器获取到上传凭证token:"+token);
                    if(token != null) {

                        //上传七牛
                        uploadManager.put(file, key, token,
                                new UpCompletionHandler() {
                                    //七牛的回调
                                    @Override
                                    public void complete(String key, ResponseInfo info, JSONObject response) {
                                        LogHelper.info(this, "uploadImg callback info:" + info.toString());
                                        LogHelper.info(this, "response:" + response.toString());

                                        //回调业务返回
                                        if (callBack != null) {
                                            callBack.onUploaded(key);
                                        }
                                    }
                                }, null);
                    }
                }
            });
        }
        return true;
    }

    /**
     * 图片上传完成回调接口
     *
     */
    public interface QiniuUploadedCallBack {
        void onUploaded(String key);
    }
}
