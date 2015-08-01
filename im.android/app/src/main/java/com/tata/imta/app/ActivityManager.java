package com.tata.imta.app;

import android.app.Activity;

import com.tata.imta.helper.LogHelper;

import java.util.LinkedList;

/**
 * Created by Will Zhang on 2015/6/12.
 * 模拟安卓活动管理
 * 自定义管理当前活动实例的操作
 * 单例
 */
public class ActivityManager {
    /**
     * 所有保存活动的活动栈
     * 使用链表方式
     */
    private static LinkedList<Activity> activityList;

    private static ActivityManager instance;

    private ActivityManager() {
        //不允许外部自己new
        if(activityList == null) {
            activityList = new LinkedList<>();
        }
    }

    //严格单例
    public static ActivityManager getInstance() {
        if(instance == null) {
            synchronized (ActivityManager.class) {
                if(instance == null) {
                    instance = new ActivityManager();
                }
            }
        }

        return instance;
    }

    /**
     * 关闭栈顶活动
     */
    public void popTopActivity() {
        Activity activity = activityList.removeFirst();
        if(activity != null) {
            LogHelper.debug(activity, "popTopActivity :"+activity.getClass().getSimpleName());
            finishActivity(activity);
        }
    }

    public int size() {
        return activityList.size();
    }

    /**
     * 关闭指定活动
     */
    public void popActivity(Activity activity){
        LogHelper.debug(activity, "popActivity :"+activity.getClass().getSimpleName());
        if(activity != null && activityList.contains(activity)){
            activityList.remove(activity);
//            finishActivity(activity);
        }
    }

    /**
     * 读取当前活动
     */
    public Activity currentActivity() {
        if(activityList.size() > 0) {
            return activityList.getFirst();
        }
        return null;
    }

    /**
     * 新增活动页
     */
    public void pushActivity(Activity activity) {
        LogHelper.debug(activity, "pushActivity :"+activity.getClass().getSimpleName());
        activityList.addFirst(activity);
    }

    public void popAllActivityExceptOne(Class cls){
        LogHelper.debug(this, "ActivityManager list size:" + activityList.size());
        while(true){
            Activity activity = currentActivity();
            if(activity == null){
                break;
            }

            LogHelper.debug(this, "current class:"+activity.getClass().getSimpleName());

            if(activity.getClass().equals(cls) ) {
                LogHelper.debug(this, "break to class:"+cls.getSimpleName());
                break;
            }
            popActivity(activity);
            finishActivity(activity);
        }
    }

    /**
     * 结束指定的Activity
     * 指定的Activity。
     */
    private final void finishActivity(Activity activity) {
        if (activity != null && !activity.isFinishing()) {

            activity.finish();

            activity = null;
        }
    }

    /**
     * 结束所有活动
     */
    public void cleanAll() {
        while(true) {
            Activity activity = currentActivity();
            if(activity == null){
                break;
            }

            popActivity(activity);
            finishActivity(activity);
        }
    }
}
