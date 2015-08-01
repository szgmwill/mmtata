package com.tata.imta.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tata.imta.R;
import com.tata.imta.bean.status.TabInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Will Zhang on 2015/6/8.
 */
public class UserDetailTabAdapter extends  MyAdapter {
    private Context context;

    /**
     * 用户标签
     */
    private List<TabInfo> tabInfos;

    /**
     * 标签类型
     */
    private int tabType;

    /**
     * 最终展示的列表
     */
    private List<String> tabNameList = new ArrayList<>();

    private LayoutInflater inflater;

    public UserDetailTabAdapter(Context context, List<TabInfo> tabInfos, int tabType) {
        this.context = context;
        this.tabInfos = tabInfos;
        this.tabType = tabType;
        inflater = LayoutInflater.from(context);

        filterList();


    }
    /**
     * 筛选出展示标签
     */
    private void filterList() {
        if(tabInfos != null) {
            tabNameList.clear();//先将上次的结果清空重新加载
            for(TabInfo tab : tabInfos) {
                if(tab.getTabType() == tabType || tabType == 0) {
                    tabNameList.add(tab.getTabName());
                }
            }
        }
    }

    @Override
    public int getCount() {
        if(tabNameList != null) {
            return tabNameList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(tabNameList != null) {
            return tabNameList.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TabHolder holder = null;
        if(convertView == null) {
            holder = new TabHolder();
            switch (tabType) {
                case TabInfo.TAB_ABL :
                    convertView = inflater.inflate(R.layout.ta_item_user_tab, null);
                    break;
                case TabInfo.TAB_LAN :
//                    TextView lanViewItem = new TextView(context);
//                    lanViewItem.setId(R.id.ta_tv_user_detail_ability);
//                    lanViewItem.setText("啥话都可以");
//                    convertView = lanViewItem;
                    convertView = inflater.inflate(R.layout.ta_item_user_tab, null);
//                    convertView = inflater.inflate(R.layout.ta_item_user_detail_lan, null);
                    break;
                default:
                    convertView = inflater.inflate(R.layout.ta_item_user_tab, null);
            }

            holder.tv = (TextView) convertView.findViewById(R.id.ta_tv_user_detail_ability);

            convertView.setTag(holder);
        } else {
            holder = (TabHolder) convertView.getTag();
        }



        //读出当前的标签
        String tabName = tabNameList.get(position);

        if(tabName != null) {
            holder.tv.setText(tabName);
        } else {
            holder.tv.setText("请填写");
        }

        return convertView;
    }

    private static class TabHolder {
        private TextView tv;
    }

    public void refresh(List<TabInfo> newList) {
        if(newList != null && newList.size() > 0) {
            tabInfos = newList;
            filterList();
            notifyDataSetChanged();
        }
    }
}
