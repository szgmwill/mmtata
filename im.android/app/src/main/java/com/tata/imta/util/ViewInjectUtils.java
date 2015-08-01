package com.tata.imta.util;

import android.app.Activity;

import com.tata.imta.app.ContentView;
import com.tata.imta.app.ViewInject;
import com.tata.imta.helper.LogHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Will Zhang on 2015/6/12.
 * 安卓IOC实现
 */
public class ViewInjectUtils {

    private static final String METHOD_SET_CONTENTVIEW = "setContentView";
    private static final String METHOD_FIND_VIEW_BY_ID = "findViewById";
    /**
     * 注入主布局文件
     *
     * @param activity
     */
    private static void injectContentView(Activity activity)
    {
        Class<? extends Activity> clazz = activity.getClass();
        // 查询类上是否存在ContentView注解
        ContentView contentView = clazz.getAnnotation(ContentView.class);
        if (contentView != null)// 存在
        {
            int contentViewLayoutId = contentView.value();
            try
            {
                Method method = clazz.getMethod(METHOD_SET_CONTENTVIEW,
                        int.class);
                method.setAccessible(true);
                method.invoke(activity, contentViewLayoutId);
            } catch (Exception e)
            {
                e.printStackTrace();
                LogHelper.error(ViewInjectUtils.class, "injectContentView", e);
            }
        }
    }

    /**
     * 注入所有的控件
     *
     * @param activity
     */
    private static void injectViews(Activity activity)
    {
        Class<? extends Activity> clazz = activity.getClass();
        Field[] fields = clazz.getDeclaredFields();
        // 遍历所有成员变量
        for (Field field : fields)
        {

            ViewInject viewInjectAnnotation = field.getAnnotation(ViewInject.class);
            if (viewInjectAnnotation != null)
            {
                int viewId = viewInjectAnnotation.value();
                if (viewId > 0)
                {
//                    LogHelper.debug(ViewInjectUtils.class, viewId + "");
                    // 初始化View
                    try
                    {
                        Method method = clazz.getMethod(METHOD_FIND_VIEW_BY_ID,
                                int.class);
                        Object resView = method.invoke(activity, viewId);
                        field.setAccessible(true);
                        field.set(activity, resView);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        LogHelper.error(ViewInjectUtils.class, "injectViews", e);
                    }

                }
            }
        }
    }

    public static void inject(Activity activity)
    {
        injectContentView(activity);
        injectViews(activity);
    }
}
