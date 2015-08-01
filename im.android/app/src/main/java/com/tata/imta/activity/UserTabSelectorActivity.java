package com.tata.imta.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tata.imta.R;
import com.tata.imta.adapter.TabListAdapter;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.app.BizInfoHolder;
import com.tata.imta.app.ContentView;
import com.tata.imta.app.ViewInject;
import com.tata.imta.bean.status.TabInfo;
import com.tata.imta.constant.RequestCode;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.util.JsonUtils;
import com.tata.imta.util.ViewInjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Will Zhang on 2015/6/18.
 * 用户标签设置页面
 */
@ContentView(R.layout.ta_activity_tab_selector)
public class UserTabSelectorActivity extends BaseActivity implements View.OnClickListener {

    //视图组件========================start==
    @ViewInject(R.id.ta_tv_topbar_title)
    private TextView mTitleTV;

    @ViewInject(R.id.ta_rl_topbar_back)
    private RelativeLayout mBackRL;

    @ViewInject(R.id.ta_rl_topbar_more)
    private RelativeLayout mMoreRL;

//    @ViewInject(R.id.ta_et_tab_input)
//    private EditText mTabInputET;

    @ViewInject(R.id.ta_rl_tab_btn_confirm)
    private RelativeLayout mConfirmRL;

    @ViewInject(R.id.ta_rl_tab_type)
    private RelativeLayout mTabTypeRL;

    @ViewInject(R.id.ta_rl_tab_ability)
    private RelativeLayout mTabAbilityRL;

    @ViewInject(R.id.ta_rl_tab_lan)
    private RelativeLayout mTabLanRL;

    @ViewInject(R.id.ta_lv_tablist)
    private ListView mTabListLV;

    @ViewInject(R.id.ta_view_type_selected)
    private View mViewTypeSelected;
    @ViewInject(R.id.ta_view_ability_selected)
    private View mViewAbilitySelected;
    @ViewInject(R.id.ta_view_lan_selected)
    private View mViewLanSelected;
    //视图组件========================end==

    private TabListAdapter tabListAdapter;

    //所有可用标签
    private List<TabInfo> allList;

    //用户当前的标签列表
    private List<String> checkedTypeList = new ArrayList<>();
    private List<String> checkedAbilityList = new ArrayList<>();
    private List<String> checkedLanList = new ArrayList<>();

    //当前要用的标签列表
    private List<TabInfo> tabList;

    /**
     * 当前展示的标签类别：1-用户类型；2-语言；3-能力；
     */
    private int curTabType = TabInfo.TAB_TYPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();

        initEvents();
    }

    @Override
    protected void initViews() {

        ViewInjectUtils.inject(this);

        mTitleTV.setText("标签设置");

        mMoreRL.setVisibility(View.GONE);

        allList = BizInfoHolder.getInstance().getTabList();
        if(allList != null) {
            tabList = getMatchTabList();

            LogHelper.debug(this, "tabList size:"+tabList.size());
        }

        if(loginUser.getTabList() != null) {
            List<TabInfo> userList = loginUser.getTabList();
            for(TabInfo tab : userList) {
                if(tab.getTabType() == TabInfo.TAB_TYPE) {
                    checkedTypeList.add(tab.getTabName());
                } else if(tab.getTabType() == TabInfo.TAB_ABL) {
                    checkedAbilityList.add(tab.getTabName());
                } else if(tab.getTabType() == TabInfo.TAB_LAN) {
                    checkedLanList.add(tab.getTabName());
                }
            }
        }
        LogHelper.debug(this, "checkedTypeList:[" + checkedTypeList.size() + "], checkedAbilityList:[" + checkedAbilityList.size() + "], checkedLanList:[" + checkedLanList + "]");
        tabListAdapter = new TabListAdapter(this, tabList, getCheckedList());
        mTabListLV.setAdapter(tabListAdapter);
    }

    @Override
    protected void initEvents() {

        mBackRL.setOnClickListener(this);

        mConfirmRL.setOnClickListener(this);
        mTabTypeRL.setOnClickListener(this);
        mTabAbilityRL.setOnClickListener(this);
        mTabLanRL.setOnClickListener(this);

    }

    private List<String> getCheckedList() {
        if(curTabType == TabInfo.TAB_TYPE) {
            return checkedTypeList;
        } else if(curTabType == TabInfo.TAB_ABL) {
            return checkedAbilityList;
        } else if(curTabType == TabInfo.TAB_LAN) {
            return checkedLanList;
        }

        return null;
    }

    private List<TabInfo> getMatchTabList() {
        if(allList != null && allList.size() > 0) {
            //只找出当前类型的标签
            List<TabInfo> targetList = new ArrayList<>();
            for(TabInfo tabInfo : allList) {
                if(tabInfo.getTabType() == curTabType) {
                    targetList.add(tabInfo);
                }
            }
            return targetList;
        }

        return null;
    }

    private void refreshTabList() {
        //重置列表
        tabList.clear();

        tabList.addAll(getMatchTabList());

        tabListAdapter.refresh(tabList, getCheckedList());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ta_rl_topbar_back :

                finish();

                break;

            case R.id.ta_rl_tab_type :

                mViewTypeSelected.setVisibility(View.VISIBLE);
                mViewAbilitySelected.setVisibility(View.GONE);
                mViewLanSelected.setVisibility(View.GONE);

                curTabType = TabInfo.TAB_TYPE;

                refreshTabList();

                break;

            case R.id.ta_rl_tab_ability :

                mViewTypeSelected.setVisibility(View.GONE);
                mViewAbilitySelected.setVisibility(View.VISIBLE);
                mViewLanSelected.setVisibility(View.GONE);

                curTabType = TabInfo.TAB_ABL;

                refreshTabList();

                break;

            case R.id.ta_rl_tab_lan :

                mViewTypeSelected.setVisibility(View.GONE);
                mViewAbilitySelected.setVisibility(View.GONE);
                mViewLanSelected.setVisibility(View.VISIBLE);

                curTabType = TabInfo.TAB_LAN;

                refreshTabList();

                break;

            case R.id.ta_rl_tab_btn_confirm :
                //确认提交了
                submit();
                break;
        }
    }

    public void submit() {

        //读取用户设置的标签
        Intent toIntent = new Intent();
        List<TabInfo> newUserTablist = new ArrayList<>();
        if(checkedTypeList.size() > 0) {
            for(String name : checkedTypeList) {
                TabInfo info = new TabInfo();
                info.setTabName(name);
                info.setTabType(TabInfo.TAB_TYPE);
                newUserTablist.add(info);
            }
        }
        if(checkedAbilityList.size() > 0) {
            for(String name : checkedAbilityList) {
                TabInfo info = new TabInfo();
                info.setTabName(name);
                info.setTabType(TabInfo.TAB_ABL);
                newUserTablist.add(info);
            }
        }
        if(checkedLanList.size() > 0) {
            for(String name : checkedLanList) {
                TabInfo info = new TabInfo();
                info.setTabName(name);
                info.setTabType(TabInfo.TAB_LAN);
                newUserTablist.add(info);
            }
        }

        LogHelper.debug(this, "Submit Tablist is:"+ JsonUtils.toJson(newUserTablist));

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("tab_list", (ArrayList) newUserTablist);
        toIntent.putExtras(bundle);

        //设置回调结果
        setResult(RequestCode.Request_Code_Tab_Select, toIntent);

        super.finish();
    }
}
