package com.tata.imta.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tata.imta.R;
import com.tata.imta.adapter.UserTabAdapter;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.app.BizInfoHolder;
import com.tata.imta.bean.ResultObject;
import com.tata.imta.bean.status.ServerAPI;
import com.tata.imta.bean.status.TabInfo;
import com.tata.imta.helper.LoadDataFromServer;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.helper.holder.SharePreferenceHolder;
import com.tata.imta.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Will Zhang on 2015/5/26.
 */
public class UserTabLanActivity extends BaseActivity implements View.OnClickListener, LoadDataFromServer.DataCallBack {

    /**
     * 用户类型填充器
     */
    private UserTabAdapter tabAdapter;

    /**
     * 用户类型类型网格
     */
    private GridView mLanGrideView;

    //语言标签数据
    private List<TabInfo> tabList = new ArrayList<>();

    //选中的标签名
    private List<String> checkNames = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ta_user_guide_lan);

        //读取标签
        List<TabInfo> allList = BizInfoHolder.getInstance().getTabList();
        if(allList != null && allList.size() > 0) {
            tabList.clear();
            for(TabInfo tab : allList) {
                if(tab.getTabType() == TabInfo.TAB_LAN) {
                    tabList.add(tab);
                }
            }
        }

        LogHelper.debug(this, "init tab type list:"+tabList.size());

        List<TabInfo> userTabList = loginUser.getTabList();
        if(userTabList != null && userTabList.size() > 0) {
            checkNames.clear();
            for(TabInfo userTab : userTabList) {
                if(userTab.getTabType() == TabInfo.TAB_LAN) {
                    checkNames.add(userTab.getTabName());
                }
            }
        }

        initViews();

        initEvents();
    }

    @Override
    protected void initViews() {
        //重置标题栏内容
        ((TextView) findViewById(R.id.ta_tv_topbar_title)).setText("语言类型");
        //实现回退上一步
        RelativeLayout mBackRL = (RelativeLayout) findViewById(R.id.ta_rl_topbar_back);
        mBackRL.setOnClickListener(this);

        RelativeLayout mMoreRL = (RelativeLayout) findViewById(R.id.ta_rl_topbar_more);
        mMoreRL.setVisibility(View.GONE);

        //设置妮称
        ((TextView) findViewById(R.id.ta_tv_user_nick)).setText(loginUser.getNick());

        //提交按钮
        Button submit = (Button) findViewById(R.id.ta_user_lan_submit);
        submit.setOnClickListener(this);

        //初始化标签列表
        mLanGrideView = (GridView) findViewById(R.id.ta_gv_user_lan);
        tabAdapter = new UserTabAdapter(this, tabList, checkNames);
        mLanGrideView.setAdapter(tabAdapter);
    }

    @Override
    protected void initEvents() {

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ta_user_lan_submit) {
            //读取已经选中的标签
            //用户类型
            //遍历所有checkbox id
            int checkBoxCount = mLanGrideView.getChildCount();

            List<TabInfo> lanList = new ArrayList<>();
            for(int i = 0; i < checkBoxCount; i++) {

                CheckBox check = (CheckBox)mLanGrideView.getChildAt(i).findViewById(R.id.ta_checkbox_user_tab);

                if(check.isChecked()) {
                    TabInfo lanTab = new TabInfo();
                    lanTab.setTabType(TabInfo.TAB_LAN);
                    lanTab.setTabName(check.getText().toString());
                    lanList.add(lanTab);
                }

            }

            if(lanList.size() == 0) {
                ToastUtils.showShortToast(this, "请至少选择一项标签");
            } else {
                //加入该类型标签之前,要先清理上一次的
                List<TabInfo> oldList = loginUser.getTabList();
                for(TabInfo old : oldList) {
                    if(old.getTabType() != TabInfo.TAB_LAN) {//把其它的纳进来
                        lanList.add(old);
                    }
                }
                loginUser.getTabList().clear();//清理
                loginUser.getTabList().addAll(lanList);//重新生成

                SharePreferenceHolder.getInstance().saveUserInfo2Local(loginUser);



                //更新用户信息到后台
                LoadDataFromServer updateUserTask = new LoadDataFromServer(this, ServerAPI.SERVER_API_EDIT_BASE, loginUser);

                updateUserTask.getData(this);
            }


        } else if (v.getId() == R.id.ta_rl_topbar_back) {
            //返回上一步
            Intent iback = new Intent(UserTabLanActivity.this, UserTabTypeActivity.class);
            startActivity(iback);
            this.finish();
        }
    }

    @Override
    public void onDataCallBack(ResultObject result) {
        //后台返回成功了
        LogHelper.debug(UserTabLanActivity.this, "update user["+loginUser.getUserId()+"] to server OK!");
        startActivity(MainActivity.class);

        //不能再回退了
        finish();
    }
}
