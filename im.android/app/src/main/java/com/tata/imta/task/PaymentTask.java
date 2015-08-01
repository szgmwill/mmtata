package com.tata.imta.task;

import android.os.AsyncTask;

import com.tata.imta.app.BaseActivity;
import com.tata.imta.bean.AcctOrder;
import com.tata.imta.bean.status.PayChannelType;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.helper.WeixinSDKHelper;

/**
 * Created by Will Zhang on 2015/6/26.
 * 第三方支付异步任务
 */
public class PaymentTask extends AsyncTask<AcctOrder, Void, String> {

    private BaseActivity activity;

    private PayChannelType channelType;

    public PaymentTask(BaseActivity activity, PayChannelType channelType) {
        this.activity = activity;
        this.channelType = channelType;

        activity.showProgressDialog("支付跳转中...");
    }

    @Override
    protected String doInBackground(AcctOrder... params) {
        LogHelper.debug(this, "doInBackground  ==> ");
        AcctOrder order = params[0];
        String err = null;
        if (order != null) {
            //开始处理支付发起动作
            if(channelType != null && channelType == PayChannelType.WX) {
                //微信支付
                err = WeixinSDKHelper.sendPayReq(order);

            } else if(channelType != null && channelType == PayChannelType.ZFB) {
                //支付宝支付


            }
        }

        return err;
    }

    /**
     * 执行完任务后回调主UI线程
     */
    @Override
    protected void onPostExecute(String err) {
        LogHelper.debug(this, "onPostExecute ==> err:" + err);
        activity.dismissProgressDialog();
        super.onPostExecute(err);

        if(err != null) {
            activity.showAlertDialog("操作提示", err);
        }
    }
}
