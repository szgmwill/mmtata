package com.tata.imta.helper;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tata.imta.R;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.component.DialogItem;

import java.util.Calendar;

/**
 * Created by Will Zhang on 2015/6/15.
 */
public class MyDialog {

    //对话框
    private ProgressDialog progressDialog;

    private AlertDialog.Builder alertDialog;

    private AlertDialog dlg;

    private BaseActivity activity;

    public MyDialog(BaseActivity activity) {
        this.activity = activity;
        alertDialog = new AlertDialog.Builder(activity);
    }

    /**
     * 进度提示对话框
     */
    public void showProgressDialog(String message) {
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
        }
        progressDialog.setMessage(message);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /** 含有标题和内容的对话框 **/
    public void showAlertDialog(String title, String message) {
        alertDialog.setTitle(title)
                .setMessage(message).show();
    }

    /** 只含有内容的对话框 **/
    public void showAlertDialog(String message) {
        alertDialog.setMessage(message).show();
    }

    /**
     * 标准确认框
     * 确认和取消
     */
    public void showConfirmDialog(String title, DialogInterface.OnClickListener confirm) {
        alertDialog.setTitle(title)
                .setPositiveButton(R.string.confirm, confirm)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();//标准取消
                    }
                }).show();
    }

    /**
     * 标准确认框
     * 自定义按钮内容
     */
    public void showConfirmDialog(String title, String confirmStr, String cancelStr, DialogInterface.OnClickListener confirm) {
        alertDialog.setTitle(title)
                .setPositiveButton(confirmStr, confirm)
                .setNegativeButton(cancelStr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();//标准取消
                    }
                }).show();
    }

    /**
     * 含有内容选择的对话框
     * 支持最多一个标题和5项内容
     */
    public void showContentChooseDialog(String title, final DialogItem... item) {

        if(item != null && item.length > 0) {
            dlg = alertDialog.create();
            dlg.show();
            Window window = dlg.getWindow();
            //自定义布局文件
            window.setContentView(R.layout.ta_common_alert_dialog);

            if(title != null) {
                window.findViewById(R.id.ta_dialog_ll_title).setVisibility(View.VISIBLE);
                TextView tvTile = (TextView) window.findViewById(R.id.ta_dialog_tv_title);
                tvTile.setText(title);
            }

            // 为确认按钮添加事件,执行退出应用操作
            LinearLayout ll1 = (LinearLayout) window.findViewById(R.id.ta_dialog_ll_content1);
            ll1.setVisibility(View.VISIBLE);
            //通过setTag打标签让回调时可以区分出元素
            ll1.setTag(item[0].getContent());
            ll1.setOnClickListener(item[0].getOnClickListener());
            TextView tv1 = (TextView) window.findViewById(R.id.ta_dialog_tv_content1);
            tv1.setText(item[0].getContent());


            if(item.length > 1 && item[1] != null) {//content2
                LinearLayout ll2 = (LinearLayout) window.findViewById(R.id.ta_dialog_ll_content2);
                ll2.setVisibility(View.VISIBLE);
                ll2.setTag(item[1].getContent());
                ll2.setOnClickListener(item[1].getOnClickListener());
                TextView tv2 = (TextView) window.findViewById(R.id.ta_dialog_tv_content2);
                tv2.setText(item[1].getContent());
            }
            if(item.length > 2 && item[2] != null) {//content3
                LinearLayout ll3 = (LinearLayout) window.findViewById(R.id.ta_dialog_ll_content3);
                ll3.setVisibility(View.VISIBLE);
                ll3.setTag(item[2].getContent());
                ll3.setOnClickListener(item[2].getOnClickListener());
                TextView tv3 = (TextView) window.findViewById(R.id.ta_dialog_tv_content3);
                tv3.setText(item[2].getContent());
            }
            if(item.length > 3 && item[3] != null) {//content4
                LinearLayout ll4 = (LinearLayout) window.findViewById(R.id.ta_dialog_ll_content4);
                ll4.setVisibility(View.VISIBLE);
                ll4.setTag(item[3].getContent());
                ll4.setOnClickListener(item[3].getOnClickListener());
                TextView tv4 = (TextView) window.findViewById(R.id.ta_dialog_tv_content4);
                tv4.setText(item[3].getContent());
            }
            if(item.length > 4 && item[4] != null) {//content5
                LinearLayout ll5 = (LinearLayout) window.findViewById(R.id.ta_dialog_ll_content5);
                ll5.setVisibility(View.VISIBLE);
                ll5.setTag(item[4].getContent());
                ll5.setOnClickListener(item[4].getOnClickListener());
                TextView tv5 = (TextView) window.findViewById(R.id.ta_dialog_tv_content5);
                tv5.setText(item[4].getContent());
            }
        }
    }

    public void cancelAlertDialog() {
        if(dlg != null && dlg.isShowing()) {
            dlg.cancel();
        }
    }

    /**
     * 弹出日期选择框
     */
    public void showDefaultDateDialog(DatePickerDialog.OnDateSetListener listener) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(activity,
                listener,
                cal.get(Calendar.YEAR ),
                cal.get(Calendar.MONTH ),
                cal.get(Calendar.DAY_OF_MONTH )
        ).show();
    }
}
