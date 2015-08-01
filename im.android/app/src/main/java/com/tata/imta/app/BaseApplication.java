package com.tata.imta.app;

import android.app.Application;

import com.gotye.api.GotyeAPI;
import com.tata.imta.component.GotyeService;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.helper.UserDBManager;
import com.tata.imta.helper.WeixinSDKHelper;
import com.tata.imta.helper.holder.SharePreferenceHolder;

/**
 * Created by will on 2015/4/30.
 *
 * app启动
 * 初始化一些环境变量；
 * 初始化一些第三方SDK
 * 加载一些本地缓存资源等
 * 注意保持单例模式
 *
 */
public class BaseApplication extends Application {
    /**
     * 使用单例模式
     */
    private static BaseApplication instance;

    /**
     * get instance of baseapplication
     */
    public static BaseApplication getInstance() {
        //如果不考虑高并发的话,可以这样使用单例
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LogHelper.info(this, "App Application Started~~~~~~~~~~~");

        //init instance
        if(instance == null) {
            instance = this;
        }

        //初始化全局异常处理
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());

        /**
         * 各种初始化工作
         * 这里可以开始初始化各种第三方平台SDK
         * 比如：IM第三方亲加
         * 微信开放平台SDK
         *
         *
         */

        //初始化本地缓存组件,优先级较高,必须写在其它初始化前面
        SharePreferenceHolder.init(this);

        /**
         * 初始化亲加通迅云
         */
        //使用您在亲加管理平台申请到的appkey初始化API，appkey如果为空会返回参数错误。
        //context为android上下文环境
        //注意,不要重复初始化
        //初始化
        int code = GotyeAPI.getInstance().init(getBaseContext(), GotyeService.gotyeAppKey);
        LogHelper.debug(this, "===> gotye init done!code[" + code + "]");

        //初始化微信组件
        WeixinSDKHelper.initAPI(this);
        WeixinSDKHelper.regToWeiXin(this);

        //初始化业务数据容器
        BizInfoHolder.init(this);//必须启动时就要初始化好,要不然后面业务用不了

        //本地数据库实例初始化
        UserDBManager.init(this);
    }

    @Override
    public void onTerminate() {
        //注销掉相关绑定操作
        //TO DO ...

        WeixinSDKHelper.getAPI().unregisterApp();

        //清理业务全局变量(内存部分)
        BizInfoHolder.destroy();

        UserDBManager.destroy();

        super.onTerminate();
    }
}
