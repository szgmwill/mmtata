package com.tata.imta.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Will Zhang on 2015/5/6.
 */
public class ToastUtils {

    /**
     * Toast提示,全局只有一个,防止重复显示
     */
    private static Toast toast;

    private void cancelToast() {
        if(toast != null) {
            toast.cancel();
        }
    }

    /** 短暂显示Toast提示(来自res) **/
    public static void showShortToast(Context context, int resId) {
        String showMsg = context.getString(resId);
        if(toast == null) {
            toast = Toast.makeText(context, showMsg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(showMsg);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }



    /** 短暂显示Toast提示(来自String) **/
    public static void showShortToast(Context context, String text) {
        if(toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    /** 长时间显示Toast提示(来自res) **/
    public static void showLongToast(Context context, int resId) {
        String showMsg = context.getString(resId);
        if(toast == null) {
            toast = Toast.makeText(context, showMsg, Toast.LENGTH_LONG);
        } else {
            toast.setText(showMsg);
            toast.setDuration(Toast.LENGTH_LONG);
        }
        toast.show();
    }

    /** 长时间显示Toast提示(来自String) **/
    public static void showLongToast(Context context, String text) {
        if(toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        } else {
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_LONG);
        }
        toast.show();
    }

    /** 显示自定义Toast提示(来自res) **/
    public static void showCustomToast(int resId) {
       //TO DO....
    }

    /** 显示自定义Toast提示(来自String) **/
    public static void showCustomToast(String text) {
        //TO DO....
    }

}
