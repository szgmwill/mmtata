package com.tata.imta.helper.img;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;

import com.tata.imta.helper.LogHelper;
import com.tata.imta.util.FileUtils;
import com.tata.imta.util.HTTPService;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 图片异步加载类
 * 
 */
public class LoadUserImg {
    // 最大线程数
    private static final int MAX_THREAD_NUM = 5;
    // 一级内存缓存基于 LruCache
    private BitmapCache bitmapCache;
    // 二级文件缓存

    // 线程池
    private ExecutorService threadPools = null;

    private static final int REQ_IMG_WHAT = 111;


    public LoadUserImg() {
        bitmapCache = new BitmapCache();
        threadPools = Executors.newFixedThreadPool(MAX_THREAD_NUM);
    }

    /**
     * 注意imageUrl的各种情况：
     * 1.网络图片地址:http://xxx.xxx.com/xxx.png
     * 2.本地图片地址Uri:content://
     * 3.七牛图片文件名:
     * @param imageUrl
     * @param imageDownloadedCallBack
     * @return
     */
    public Bitmap loadImage(final String imageUrl,
            final ImageDownloadedCallBack imageDownloadedCallBack) {

        if(TextUtils.isEmpty(imageUrl)) {
            return null;
        }

//        LogHelper.debug(this, "loadImage ==> imageUrl["+imageUrl+"]");
        /**
         * 标记图片的主键
         * 去掉'/'等特殊字符
         */
        final String key = FileUtils.getKeyFromImgNameOrUrl(imageUrl);
//        LogHelper.debug(this, "loadImage ==> img key["+key+"]");
        // 先从内存中拿
        Bitmap bitmap = bitmapCache.getBitmap(key);

        //如果内存里有这个图片的话,直接返回了,不需要回调了
        if (bitmap != null) {
//            LogHelper.debug(this, "image exists in bitmapCache");
            return bitmap;
        }

        // 从文件中找,有的话直接返回了,不需要回调了
        if (FileUtils.isBitmapExists(key)) {
//            LogHelper.debug(this, "image exists in file:" + key);

            bitmap = FileUtils.getBigMapByKey(key);
            if(bitmap != null) {
                // 先缓存到内存
                bitmapCache.putBitmap(key, bitmap);
                return bitmap;
            }
        }

        //开始网络下载
        final Handler handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == REQ_IMG_WHAT && imageDownloadedCallBack != null) {
                    Bitmap bitmap = (Bitmap) msg.obj;
                    //将bitmap和视图都回调回去
                    imageDownloadedCallBack.onImageDownloaded(bitmap);
                }
            }
        };



        //异步线程读取网络图片
        Thread thread = new Thread() {
            @SuppressLint("NewApi")
            @Override
            public void run() {
                LogHelper.debug(this, Thread.currentThread().getName()
                        + " is running");

                String httpGetUrl = null;
                if(imageUrl.startsWith("http")) {
                    //网络图片下载
                    httpGetUrl = imageUrl;
                } else {
                    //七牛云图片读取
                    httpGetUrl = QiniuCloudLoader.QINIU_DOMAIN + imageUrl;
                }
                Bitmap bitmap = null;
                try {
                    InputStream inputStream = HTTPService.getInstance()
                            .getStream(httpGetUrl);
                    /**
                     * 对下载完的图片进行一定的处理
                     */
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inJustDecodeBounds = false;
//                options.inSampleSize = 5; // width，height设为原来的十分一
//                Bitmap bitmap = BitmapFactory.decodeStream(inputStream,
//                        null, options);

                    bitmap = BitmapFactory.decodeStream(inputStream);//原图大小下载
                    inputStream.close();
                } catch (Exception e) {
                    LogHelper.error(this, "download img error", e);
                }

                // 图片下载成功后缓存并执行回调刷新界面
                if (bitmap != null) {

                    // 先缓存到内存
                    bitmapCache.putBitmap(key, bitmap);
                    // 缓存到文件系统
                    FileUtils.saveBitmap(key, bitmap);

                    Message msg = new Message();
                    msg.what = REQ_IMG_WHAT;
                    msg.obj = bitmap;
                    handler.sendMessage(msg);
                }
            }
        };

        //控制线程处理数
        threadPools.execute(thread);

        return null;
    }

 
    /**
     * 图片下载完成回调接口
     * 
     */
    public interface ImageDownloadedCallBack {
        void onImageDownloaded(Bitmap bitmap);
    }

    /**
     * 展示图片
     */
    public void showImgView(final ImageView imageView, final String imgUrl) {
        if (!TextUtils.isEmpty(imgUrl)) {
            //打个标记好让回调时可以判断
            imageView.setTag(imgUrl);
            Bitmap bitmap = loadImage(imgUrl, new LoadUserImg.ImageDownloadedCallBack() {

                        @Override
                        public void onImageDownloaded(Bitmap bitmap) {
                            if(bitmap == null) {
                                LogHelper.debug(this, "下载图片失败:"+imgUrl);
                            } else {
                                if(imageView.getTag() != null && imageView.getTag() instanceof String) {
                                    if(((String)imageView.getTag()).equals(imgUrl)) {
                                        imageView.setImageBitmap(bitmap);//异步下载后刷新
                                    }
                                }
                            }

                        }

                    });

            if (bitmap != null) {

                imageView.setImageBitmap(bitmap);//缓存中找到则同步刷新

            }

        }
    }

    /**
     * 根据头像读取图片
     */
    public Bitmap getBitMapFromHead(String head) {
        if(head != null) {
            //先从内存里读
            final String key = FileUtils.getKeyFromImgNameOrUrl(head);
//        LogHelper.debug(this, "loadImage ==> img key["+key+"]");
            // 先从内存中拿
            Bitmap bitmap = bitmapCache.getBitmap(key);

            //如果内存里有这个图片的话,直接返回了,不需要回调了
            if (bitmap != null) {
//            LogHelper.debug(this, "image exists in bitmapCache");
                return bitmap;
            }

            // 从文件中找,有的话直接返回了,不需要回调了
            if (FileUtils.isBitmapExists(key)) {
//            LogHelper.debug(this, "image exists in file:" + key);

                bitmap = FileUtils.getBigMapByKey(key);
                if(bitmap != null) {
                    // 先缓存到内存
                    bitmapCache.putBitmap(key, bitmap);
                    return bitmap;
                }
            }
        }

        return null;
    }
}
