package com.tata.imta.app;


import android.support.v4.app.Fragment;

import com.gotye.api.GotyeAPI;
import com.tata.imta.bean.User;

/**
 * Created by Will Zhang on 2015/5/6.
 * 面板模块统一基类
 */
public abstract class BaseFragment extends Fragment {

    public BaseFragment() {
        super();
    }

    /**
     * 该面板是否第一次初始化完
     * 当面板相关视图控件等已经初始化过后,每次进来就不需要再初始化
     * 只需要改变视图的元素内容即可
     */
    protected boolean isInstanceDone = false;

    /**
     * 亲加通迅云操作SDK
     * 方便子类进行相关操作
     */
    protected GotyeAPI gotyeAPI = GotyeAPI.getInstance();

    /**
     * 当前登录用户
     */
    protected User loginUser = BizInfoHolder.getInstance().getLoginUser();

    /**
     * 刷新面版相关视图
     * 子类来实现
     */
    public abstract void refresh();
}
