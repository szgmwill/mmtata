package com.tata.imta.helper;

import android.util.Log;

/**
 * Created by Will Zhang on 2015/5/24.
 * 自定义日志打印器
 */
public class LogHelper {
    private static final String TAG_BASE = "mmtata -->> ";

    public static void debug(Object target, String content) {
        if(target != null) {
            debug(target.getClass(), content);
        }
    }

    public static void debug(Class target, String content) {
        if(target != null) {
            Log.d("[debug]/"+TAG_BASE+target.getSimpleName(), content) ;
        }
    }

    public static void info(Object target, String content) {
        if(target != null) {
            info(target.getClass(), content);
        }
    }

    public static void info(Class target, String content) {
        if(target != null) {
            Log.d("[info]/"+TAG_BASE+target.getSimpleName(), content) ;
        }
    }

    public static void error(Object target, String errmsg, Throwable ex) {
        if(target != null) {
            error(target.getClass(), errmsg, ex);
        }
    }

    public static void error(Object target, String errmsg) {
        if(target != null) {
//            Log.e("[error]/"+TAG_BASE+target.getClass().getSimpleName(), errmsg);
            error(target.getClass(), errmsg, null);
        }
    }

    public static void error(Class target, String errmsg, Throwable ex) {
        if(target != null) {
            if(ex != null) {
                Log.e("[error]/"+TAG_BASE+target.getSimpleName(), errmsg, ex);
            } else {
                Log.e("[error]/"+TAG_BASE+target.getSimpleName(), errmsg);
            }
        }
    }
}
