package com.tata.imta.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.tata.imta.R;
import com.tata.imta.helper.LogHelper;

/**
 * Created by Will Zhang on 2015/5/7.
 * 消息通知工具类
 */
public class NotificationUtils {

    private static Notification notification;
    private static NotificationManager nManager;
    // Notification的标示ID,同一个ID产生的通知只会是一条,不同ID将产生不同通知
    private static final int ID = 8001;


    private static void initNotification(Context context, String msg) {

        if(nManager == null) {
            nManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        }

        if(notification == null) {
            notification = new Notification();
        }

        if(TextUtils.isEmpty(msg)) {// 通知提示
            msg = "未知消息";
        }
        // 显示时间
        long currentTimeMillis = System.currentTimeMillis();

        notification.icon = R.drawable.ta_notify_avatar;// 设置通知的图标,一般就是应用的logo了
        notification.tickerText = msg; // 显示在状态栏中的文字
        notification.when = currentTimeMillis; // 设置来通知时的时间
        notification.sound = Uri.parse("android.resource://com.sun.alex/raw/dida"); // 自定义声音
//        notification.flags = Notification.FLAG_NO_CLEAR; // 点击清除按钮时就会清除消息通知,但是点击通知栏的通知时不会消失
//        notification.flags = Notification.FLAG_ONGOING_EVENT; // 点击清除按钮不会清除消息通知,可以用来表示在正在运行
        notification.flags |= Notification.FLAG_AUTO_CANCEL; // 点击清除按钮或点击通知后会自动消失
//        notification.flags |= Notification.FLAG_INSISTENT; // 一直进行，比如音乐一直播放，知道用户响应
//        notification.defaults = Notification.DEFAULT_SOUND; // 调用系统自带声音
//        notification.defaults = Notification.DEFAULT_SOUND;// 设置默认铃声
//        notification.defaults = Notification.DEFAULT_VIBRATE;// 设置默认震动
//        notification.defaults = Notification.DEFAULT_ALL; // 设置铃声震动
        notification.defaults = Notification.DEFAULT_ALL; // 把所有的属性设置成默认
    }

    public static void notify(Context curContext, Class target, String title, String content) {

        initNotification(curContext, content);

        // 单击通知后会跳转到NotificationResult类
        Intent intent = new Intent(curContext, target);
        // 获取PendingIntent,点击时发送该Intent
        PendingIntent pIntent = PendingIntent.getActivity(curContext, 0,
                intent, 0);
        // 设置通知的标题和内容
        notification.setLatestEventInfo(curContext, title,
                content, pIntent);
        // 发出通知
        LogHelper.debug(NotificationUtils.class, "send notify ==> ");
        nManager.notify(ID, notification);
    }
}
