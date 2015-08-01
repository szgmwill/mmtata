package com.tata.imta.bean.status;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by Will Zhang on 2015/5/24.
 * 用户标签信息
 */
public class TabInfo implements Serializable {

    public static final int TAB_TYPE = 1;
    public static final int TAB_LAN = 2;
    public static final int TAB_ABL = 3;

    @JSONField(name = "tab_name")
    private String tabName;

    /**
     * 标签类别：1-用户类型；2-语言；3-能力；
     */
    @JSONField(name = "tab_type")
    private int tabType;

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public int getTabType() {
        return tabType;
    }

    public void setTabType(int tabType) {
        this.tabType = tabType;
    }
}

