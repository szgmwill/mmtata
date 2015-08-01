package com.tata.imta.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images.ImageColumns;
import android.text.TextUtils;

import com.tata.imta.helper.LogHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 本地文件操作工具类
 * 主要处理图片和普通文件
 */
public class FileUtils {

    /**
     * 本地图片的缓存地址(默认地址)
     */
    private static final String LOCAL_IMG_PATH = "/sdcard/mmtata/img/";

    /**
     * 本地文件的保存地址
     */
    private static final String LOCAL_FILE_PATH = "/sdcard/mmtata/apk/";

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * 保存图片到本地文件存储路径
     * 
     */
    public static void saveBitmap(String key, Bitmap bitmap) {
        if (!isExternalStorageWritable()) {
            LogHelper.debug(FileUtils.class, "SD卡不可用，保存失败");
            return;
        }

        if (bitmap == null) {
            return;
        }
     
        try {
            //保存文件路径
            File file = new File(LOCAL_IMG_PATH, key);

            if(file.exists()) {
                file.delete();
            }

            FileOutputStream outputstream = new FileOutputStream(file);

            //对图片进行一定的压缩后存储
            if((key.indexOf(".png") != -1) || (key.indexOf(".PNG") != -1))
            {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputstream);
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputstream);
            }
            
            outputstream.flush();
            outputstream.close();
            
        } catch (FileNotFoundException fnfe) {
            LogHelper.error(FileUtils.class, "saveBitmap", fnfe);
        } catch (IOException ioe) {
            LogHelper.error(FileUtils.class, "saveBitmap", ioe);
        } catch (Exception e) {
            LogHelper.error(FileUtils.class, "saveBitmap", e);
        }
    }

    public static boolean isBitmapExists(String key) {
        File dir = new File(LOCAL_IMG_PATH);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        //context.getExternalFilesDir(null);
        File file = new File(dir, key);

        return file.exists();
    }

    public static File getImgFileByName(String imgName) {
        if(imgName != null) {
            String key = getKeyFromImgNameOrUrl(imgName);
            File file = new File(LOCAL_IMG_PATH, key);
            if(file.exists()) {
                return file;
            }
        }
        return null;
    }

    public static String genImageFilePath(String imgName) {
        if(imgName != null) {
            String key = getKeyFromImgNameOrUrl(imgName);

            return LOCAL_IMG_PATH + key;
        }
        return null;
    }

    public static boolean isImageFileExists(String imageName) {
        String key = getKeyFromImgNameOrUrl(imageName);

        return isBitmapExists(key);

    }

    public static Bitmap getBigMapByKey(String key) {
        if (key != null) {
            File file = new File(LOCAL_IMG_PATH, key);
            if(file.exists()) {

                return BitmapFactory.decodeFile(file.getAbsolutePath());
            }
        }
        return null;
    }

    public static Bitmap getBigMapByImageName(String imageName) {
        if (imageName != null) {
            String key = getKeyFromImgNameOrUrl(imageName);
            File file = new File(LOCAL_IMG_PATH, key);
            if(file.exists()) {
                return BitmapFactory.decodeFile(file.getAbsolutePath());
            }
        }
        return null;
    }

    /**
     * 将图片文件名进行过滤处理
     */
    public static String getKeyFromImgNameOrUrl(String imageName) {
        if(imageName != null) {
            return imageName.replaceAll("/|:", "-").trim();
        }
        return "";
    }

    /**
     * Try to return the absolute file path from the given Uri
     *
     */
    public static File getRealFileFromUri( final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        if(data != null) {
            File file = new File(data);
            if(file.exists()) {
                return file;
            }
        }
        return null;
    }


    /**
     * 创建一个文件
     */
    public static File createFile(String fileName) {
        if(!TextUtils.isEmpty(fileName)) {

            File dir = new File(LOCAL_FILE_PATH);//dir
            if(!dir.exists()) {
                dir.mkdir();
            }

            String file_path = LOCAL_FILE_PATH + fileName;
            File file = new File(file_path);
            if(!file.exists()) {//新建文件
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //删除旧文件
                file.delete();
            }
            return file;
        }

        return null;
    }
}
