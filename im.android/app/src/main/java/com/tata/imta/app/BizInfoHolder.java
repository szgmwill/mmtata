package com.tata.imta.app;

import android.content.Context;
import android.graphics.Bitmap;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tata.imta.bean.ResultObject;
import com.tata.imta.bean.User;
import com.tata.imta.bean.status.ServerAPI;
import com.tata.imta.bean.status.TabInfo;
import com.tata.imta.helper.LoadDataFromServer;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.helper.ServerAPIHelper;
import com.tata.imta.helper.holder.CityInfoHolder;
import com.tata.imta.helper.holder.SharePreferenceHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Will Zhang on 2015/6/11.
 * 业务全局变量存放容器,一般存放一些全局变量信息
 * 考虑内存关系,只可存放轻量的数据量
 * 数据较大的建议存放在shareprefenre或者本地sqlite
 * 该类只能单例
 */
public class BizInfoHolder {

    private volatile static BizInfoHolder singleton;

    private Context context;

    /**
     * 私有实例化
     */
    private BizInfoHolder(Context context) {
        this.context = context;
    }

    /**
     * 实例化单例
     */
    public static void init(Context context) {
        if (singleton == null) {
            synchronized (BizInfoHolder.class) {
                if (singleton == null) {
                    LogHelper.info(BizInfoHolder.class, "init BizInfoHolder ===> ");
                    singleton = new BizInfoHolder(context);
                }
            }
        }

        //初始化各种缓存

        //初始化登录用户信息
        singleton.refreshLoginUser();

        //初始化表情
        singleton.initEmojiList();

        //初始化标签
        singleton.initTabList();

        //初始化城市数据
        singleton.initCityList();
    }

    public static BizInfoHolder getInstance() {
        return singleton;
    }

    public static void destroy() {
        if(singleton != null) {
            singleton = null;
        }
    }

    /**
     * 聊天表情图标名称缓存
     */
    private List<String> staticEmojiList;

    /**
     * 聊天表情图标bitmap缓存
     */
    private Map<String, Bitmap> emojiMap = new HashMap<String, Bitmap>();

    /**
     * 当前可选择的用户标签列表
     * 从后台读取
     */
    private List<TabInfo> tabList = new ArrayList<>();

    /**
     * 当前登录用户单例,全局唯一
     */
    private User loginUser;

    /**
     * 城市数据
     */
    private CityInfoHolder cityHolder;

    /**
     * 从资源文件中加载静态表情名称
     */
    private void initEmojiList() {
        if(staticEmojiList == null) {
            staticEmojiList = new ArrayList<String>();
        }
        try {
            String[] faces = context.getAssets().list("face/png");
            //将Assets中的表情名称转为字符串一一添加进staticEmojiList
            for (int i = 0; i < faces.length; i++) {
                staticEmojiList.add(faces[i]);
            }
            //去掉删除图片
            staticEmojiList.remove("emotion_del_normal.png");
        } catch (Exception e) {
            e.printStackTrace();
            LogHelper.error(this, "initEmojiList", e);
        }
    }

    /**
     * 初始化用户标签
     */
    private void initTabList() {
        Map<String, String> params = new HashMap<>();
        params.put("tab_type", "0");
        LoadDataFromServer queryTablistTask = new LoadDataFromServer(context, ServerAPI.SERVER_API_TAB_LIST, params);

        queryTablistTask.getData(new LoadDataFromServer.DataCallBack() {
            @Override
            public void onDataCallBack(ResultObject ro) {
                JSONObject data = ServerAPIHelper.handleServerResult(context, ro);
                if(data != null) {
                    JSONArray array = data.getJSONArray("tab_list");
                    if(array != null && array.size() > 0) {
                        LogHelper.info(this, "Query Tablist from Server:" + array.size());
                        tabList.clear();
                        for(Object obj : array) {
                            JSONObject json = (JSONObject) obj;
                            String tabName = json.getString("tab_name");
                            int tabType = json.getIntValue("tab_type");
                            TabInfo tab = new TabInfo();
                            tab.setTabName(tabName);
                            tab.setTabType(tabType);
                            tabList.add(tab);
                        }
                    }
                }

                LogHelper.info(this, "init tablist size:"+tabList.size());
            }
        });
    }

    /**
     * 初始化城市数据
     */
    private void initCityList() {
        if(cityHolder == null) {
            cityHolder = new CityInfoHolder();
        }

        //init
        cityHolder.initProvinceDatas(context);
    }

    /**
     * 重新加载登录用户信息
     */
    public void refreshLoginUser() {
        loginUser = null;
        //判断一下是否用户已经登录了,没有登录的话,直接引导到引导页去
        loginUser = SharePreferenceHolder.getInstance().getLoginUserInfo();
    }

    public List<String> getStaticEmojiList() {
        return staticEmojiList;
    }

    public List<TabInfo> getTabList() {
        return tabList;
    }

    public User getLoginUser() {
        return loginUser;
    }

    public CityInfoHolder getCityHolder() {
        return cityHolder;
    }

}
