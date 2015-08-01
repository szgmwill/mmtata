package com.tata.imta.util;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tata.imta.R;

import cn.smssdk.SMSSDK;

/**
 * Created by Will Zhang on 2015/7/9.
 * 各式各样的对话框操作
 */
public class MyDialogUtils {

    /**
     * 发送短信验证码对话框
     */
    public static void showSendSMSDialog(Context context, final String code, final String phone) {
        final String phoneNum = "+86" + " " + phone;
        final Dialog dialog = new Dialog(context, R.style.SMSDialog);
        dialog.setContentView(R.layout.ta_dialog_sms_send_msg);

        TextView phoneTV = (TextView) dialog.findViewById(R.id.ta_tv_sms_phone);
        TextView hintTV = (TextView) dialog.findViewById(R.id.ta_tv_sms_dialog_hint);
        phoneTV.setText(phoneNum);
        hintTV.setText(Html.fromHtml(context.getResources().getString(R.string.mob_sms_make_sure_mobile_detail)));

        //点击
        Button btnOK = (Button) dialog.findViewById(R.id.ta_btn_sms_dialog_ok);
        Button btnCancel = (Button) dialog.findViewById(R.id.ta_btn_sms_dialog_cancel);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到验证码页面
                dialog.dismiss();

                //发送验证码
                SMSSDK.getVerificationCode(code, phone);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
}
