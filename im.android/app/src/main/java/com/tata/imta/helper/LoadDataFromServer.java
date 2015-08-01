package com.tata.imta.helper;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.tata.imta.bean.ResultObject;
import com.tata.imta.bean.status.ServerAPI;
import com.tata.imta.constant.ServerErrorCode;
import com.tata.imta.util.ToastUtils;

/**
 * Created by Will Zhang on 2015/5/15.
 * 服务器调用工具类
 * 主要处理与业务后台服务器交互
 * app与后台交互采用异步方式,只有后台成功返回后客户端才进行下一步动作
 * app客户端与服务器交互安全策略目前采用简单的签名算法
 */
public class LoadDataFromServer {

    /**
     * 后台服务器接口URI
     */
    private ServerAPI api;

    /**
     * 业务请求参数
     */
    private Object bizParam;

    /**
     * 客户端当前上下文,主要是处理一些展示和当前环境界面关联
     */
    private Context context;

    //当前方法回调的标记位
    private static final int WHAT = 111;

    public LoadDataFromServer(Context context, ServerAPI api,
                              Object params) {
        this.api = api;
        this.context = context;
        bizParam = params;
    }

    /**
     * 客户端注册回调接口并且收到服务器返回后把结果回调
     */
    public void getData(final DataCallBack dataCallBack) {

        //如果dataCallBack == null 则认为不需要返回信息

        final Handler handler = new Handler() {
            /**
             * 当收到服务器返回的内容后,触发以下回调
             */
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == WHAT && dataCallBack != null) {

                    //这里强制返回内容必须是Json格式的
                    ResultObject result = (ResultObject) msg.obj ;

                    if(result == null) {
                        LogHelper.debug(this, "调用后台api["+api+"]通迅异常");
                        ToastUtils.showLongToast(context, "服务器出小差了...");
                    } else if(result.getCode() != ServerErrorCode.OK) {
                        LogHelper.debug(this, "调用后台api["+api+"]返回失败:"+result.getMsg());
                    }

                    //不管怎么样,强制回调结果
                    dataCallBack.onDataCallBack(result);
//                    if (result != null && result.getCode() == 0) {
//                        //调用成功的返回
//                        dataCallBack.onDataCallBack(JsonUtils.formatDataObj(result.getData()));
//
//                    } else if (result != null && result.getCode() == -1003) {
//                        //查询成功,但没有数据
//                        dataCallBack.onDataCallBack(null);
//                    } else if(result != null && result.getCode() == -1004) {
//                        //业务失败提示
//                        dataCallBack.onDataCallBack(JsonUtils.formatDataObj(result));
//                    }
//                    else {
//                        LogHelper.info(this, "访问服务器出错...");
//                        ToastUtils.showLongToast(context, "服务器出小差了...");
//                    }
                }
            }
        };

        /**
         * 启动独立线程调用后台
         */
        new Thread() {

            public void run() {
                Message msg = new Message();//不管怎么样,都要回调一下
                msg.what = WHAT;

                //请求后台
                ResultObject serverRO = ServerAPIHelper.invokeAPI(bizParam, api);
                //把整个返回吧
                msg.obj = serverRO;
                //回调
                handler.sendMessage(msg);
            }
        }.start();

    }

    /**
     * 调用回调接口
     *
     */
    public interface DataCallBack {
        void onDataCallBack(ResultObject result);
    }
}
