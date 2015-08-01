package com.tata.imta.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.BaseAdapter;

/**
 * Created by Will Zhang on 2015/5/10.
 * Adapter 基类
 */
public abstract class MyAdapter extends BaseAdapter {

    private AlertDialog alertDialog;
    /**
     * 不带标题的对话框
     * @param context
     * @param content
     */
    protected void showAlertDialog(Context context, String content) {
        new AlertDialog.Builder(context).setMessage(content).show();
    }

    /**
     * 带标题对话框
     */
    protected void showAlertDialog(Context context, String title, String content) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(content);
    }


}
