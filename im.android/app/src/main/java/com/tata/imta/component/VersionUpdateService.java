package com.tata.imta.component;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

import com.tata.imta.R;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Will Zhang on 2015/7/12.
 * apk版本自动升级后台任务
 */
public class VersionUpdateService extends Service {
    private static final String TAG = VersionUpdateService.class.getSimpleName();

    /** 超时 */
    private static final int TIMEOUT = 10 * 1000;
    /** 下载地址 */
    private static final String DOWN_URL_BASE = "http://www.mmtata.com/apk/";

    //下载文件名
    private String down_file_name = "mmtata-release-official-1.0.apk";

    //下载后的保存文件
    private File local_file;

    /** 下载成功 */
    private static final int DOWN_OK = 1;
    /** 下载失败 */
    private static final int DOWN_ERROR = 0;
    /***
     * 创建通知栏
     */
    RemoteViews mViews;
    /** 应用名称 */
    private String app_name;
    /** 通知管理器 */
    private NotificationManager notificationManager;
    /** 通知 */
    private Notification notification;
    /** 点击通知跳转 */
    private Intent mUpdateIntent;
    /** 等待跳转 */
    private PendingIntent mPendingIntent;
    /** 通知ID */
    private final int notification_id = 8800;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogHelper.debug(TAG, "onStartCommand ==> ");
        app_name = getResources().getString(R.string.app_name);

        // 创建文件
        local_file = FileUtils.createFile(app_name);
        //创建通知栏视图
        createNotification();

        //看看是否有传过来文件名
        if(intent != null) {
            String fileName = intent.getStringExtra("fileName");
            if(fileName != null) {
                down_file_name = fileName;
            }
        }

        //文件下载链接
        String fileUrl = DOWN_URL_BASE + down_file_name;
        LogHelper.debug(this, "version update file url:"+fileUrl);

        //开始下载安装包处理
        createThread(fileUrl);

        return super.onStartCommand(intent, flags, startId);

    }

    /**
     * 创建通知栏
     */
    public void createNotification() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        android.app.Notification.Builder builder = new Notification.Builder(this);

        //创建通知栏里的下载视图
        mViews = new RemoteViews(getPackageName(), R.layout.ta_notification_version_update);

        mViews.setTextViewText(R.id.ta_notification_version_update_title, "版本升级中...");
        mViews.setTextViewText(R.id.ta_notification_version_update_text, "当前进度: 0%");
        mViews.setProgressBar(R.id.ta_notification_version_update_progress, 100, 0, false);
        builder.setContent(mViews);
        mUpdateIntent = new Intent(Intent.ACTION_MAIN);
        mUpdateIntent.addCategory(Intent.CATEGORY_HOME);
        mPendingIntent = PendingIntent.getActivity(this, 0, mUpdateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(mPendingIntent);
        builder.setTicker("开始下载，点击可查看");
        builder.setSmallIcon(R.drawable.app_logo).setWhen(System.currentTimeMillis()).setAutoCancel(true);// 设置可以清除
        notification = builder.getNotification();
        notificationManager.notify(notification_id, notification);
    }

    /***
     * 开线程下载
     */
    public void createThread(final String fileUrl) {
        /***
         * 更新UI
         */
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DOWN_OK:
                        InstallationAPK();
                        break;
                    case DOWN_ERROR:
                        break;

                    default:
                        stopSelf();
                        break;
                }

            }

        };

        final Message message = new Message();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    long downloadSize = downloadUpdateFile(fileUrl, local_file.getAbsolutePath());
                    LogHelper.debug(VersionUpdateService.this, "download file size:"+downloadSize);
                    if (downloadSize > 0) {
                        // 下载成功
                        message.what = DOWN_OK;
                        handler.sendMessage(message);
                    }

                } catch (Exception e) {
                    LogHelper.error(this, "download update apk file error:", e);
                    message.what = DOWN_ERROR;
                    handler.sendMessage(message);
                }

            }
        }).start();
    }

    /**
     * 自动安装apk
     */
    private void InstallationAPK() {
        notificationManager.cancel(notification_id);
        // 停止服务
        stopSelf();
        // 下载完成，点击安装
        Uri uri = Uri.fromFile(local_file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);

    }

    /**
     * 下载文件
     */
    public long downloadUpdateFile(String down_url, String file) throws Exception {
        int down_step = 5;// 提示step
        int totalSize;// 文件总大小
        int downloadCount = 0;// 已经下载好的大小
        int updateCount = 0;// 已经上传的文件大小
        InputStream inputStream;
        OutputStream outputStream;


        // 构造URL
        URL url = new URL(down_url);
        // 打开连接
        URLConnection con = url.openConnection();
        //获得文件的长度
        int contentLength = con.getContentLength();
        LogHelper.debug(this, "长度 :" + contentLength);
        // 输入流
        inputStream = con.getInputStream();


//        URL url = new URL(down_url);
//        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//        httpURLConnection.setConnectTimeout(TIMEOUT);
//        httpURLConnection.setReadTimeout(TIMEOUT);
//        // 获取下载文件的size
//        totalSize = httpURLConnection.getContentLength();
//        if (httpURLConnection.getResponseCode() == 404) {
//            throw new Exception("fail!");
//        }
//        inputStream = httpURLConnection.getInputStream();
        outputStream = new FileOutputStream(file, false);// 文件存在则覆盖掉
        byte buffer[] = new byte[1024];
        int readsize = 0;
        while ((readsize = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, readsize);
            downloadCount += readsize;// 时时获取下载到的大小
            /**
             * 每次增张5%
             */
            if (updateCount == 0 || (downloadCount * 100 / contentLength - down_step) >= updateCount) {
                updateCount += down_step;
                mViews.setTextViewText(R.id.ta_notification_version_update_text, "当前进度: " + updateCount + "%");
                mViews.setProgressBar(R.id.ta_notification_version_update_progress, 100, updateCount, false);
                notificationManager.notify(notification_id, notification);
            }

        }
        if (con != null) {
            con = null;
        }
        inputStream.close();
        outputStream.close();

        return downloadCount;
    }
}
