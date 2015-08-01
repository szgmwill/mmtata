package com.tata.imta.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Will Zhang on 2015/5/6.
 */
public class AlertDialogHelper {

    private static AlertDialog dialog;

    private static void init(Context context) {
        if(dialog == null) {
            dialog = new AlertDialog.Builder(context).create();
        }
    }

    /** 含有标题和内容的对话框 **/
    public static AlertDialog showAlertDialog(Context context, String title, String message) {
        init(context);

        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.show();

        return dialog;
    }

    public static void dimissAlertDialog() {
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /** 含有标题、内容、两个按钮的对话框 **/
    public static AlertDialog showAlertDialog(Context context, String title, String message,
                                          String positiveText,
                                          DialogInterface.OnClickListener onPositiveClickListener,
                                          String negativeText,
                                          DialogInterface.OnClickListener onNegativeClickListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveText, onPositiveClickListener)
                .setNegativeButton(negativeText, onNegativeClickListener)
                .show();
        return alertDialog;
    }

    /** 含有标题、内容、图标、两个按钮的对话框 **/
    public static AlertDialog showAlertDialog(Context context, String title, String message,
                                          int icon, String positiveText,
                                          DialogInterface.OnClickListener onPositiveClickListener,
                                          String negativeText,
                                          DialogInterface.OnClickListener onNegativeClickListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle(title)
                .setMessage(message).setIcon(icon)
                .setPositiveButton(positiveText, onPositiveClickListener)
                .setNegativeButton(negativeText, onNegativeClickListener)
                .show();
        return alertDialog;
    }
}
