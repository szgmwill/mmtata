package com.tata.imta.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tata.imta.R;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.bean.status.TabInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Will Zhang on 2015/5/15.
 */
public class TabListAdapter extends MyAdapter {

    private BaseActivity activity;

    /**
     * 标签名列表
     *
     */
    private List<TabInfo> tabInfoList;

    /**
     * 当前选中标签
     */
    private List<String> checkedList;

    private LayoutInflater inflater;

    public TabListAdapter(BaseActivity context, List<TabInfo> tabList, List<String> checkedList) {
        activity = context;
        if(tabList != null && tabList.size() > 0) {
           this.tabInfoList = tabList;
        } else {
            tabInfoList = new ArrayList<TabInfo>();
        }
        if(checkedList != null && checkedList.size() > 0) {
            this.checkedList = checkedList;
        } else {
            checkedList = new ArrayList<>();
        }

        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return tabInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return tabInfoList.get(position).getTabName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TabItemHolder holder = null;
        if(convertView == null) {
            holder = new TabItemHolder();

            convertView = inflater.inflate(R.layout.ta_item_tab_listview, null);
            holder.tabNameTV = (TextView) convertView.findViewById(R.id.ta_tv_tab_name);
            holder.tabSelectedIV = (ImageView) convertView.findViewById(R.id.ta_iv_tab_selected);

            convertView.setTag(holder);
        } else {
            holder = (TabItemHolder) convertView.getTag();
        }

        //填充内容
        String tabName = tabInfoList.get(position).getTabName();
        holder.tabNameTV.setText(tabName);
        //判断是否已经选中
        if(checkedList.contains(tabName)) {
            holder.tabSelectedIV.setVisibility(View.VISIBLE);
        } else {
            holder.tabSelectedIV.setVisibility(View.GONE);
        }

        RelativeLayout re_parent = (RelativeLayout) convertView.findViewById(R.id.ta_rl_tablist_item);
        re_parent.setTag(holder);
        re_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TabItemHolder tabHolder = (TabItemHolder) v.getTag();
                String tabName = tabHolder.tabNameTV.getText().toString();
                //配置点击事件
                if (tabHolder.tabSelectedIV.getVisibility() == View.VISIBLE) {
                    //原来选中,现在点击取消
                    if (checkedList.contains(tabName)) {
                        checkedList.remove(tabName);
                    }
                    tabHolder.tabSelectedIV.setVisibility(View.GONE);
                } else {
                    //重新选择
                    if (checkedList.size() > 5) {
                        activity.showAlertDialog("错误提示", "每类标签最多选择5个");
                    } else {
                        tabHolder.tabSelectedIV.setVisibility(View.VISIBLE);
                        checkedList.add(tabName);
                    }
                }
            }
        });


        return convertView;
    }

    private static class TabItemHolder {
        TextView tabNameTV;
        ImageView tabSelectedIV;
    }

    public void refresh(List<TabInfo> tabList, List<String> checkedList) {
        this.tabInfoList = tabList;
        this.checkedList = checkedList;

        notifyDataSetChanged();
    }
}
