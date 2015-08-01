package com.tata.imta.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.tata.imta.R;
import com.tata.imta.bean.status.TabInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Will Zhang on 2015/5/15.
 */
public class UserTabAdapter extends MyAdapter {

    /**
     * 标签名列表
     *
     */
    private List<TabInfo> tabInfoList;

    /**
     * 当前选中标签
     */
    private List<String> checkedNameList;

    private LayoutInflater inflater;

    public UserTabAdapter (Context context, List<TabInfo> tabList, List<String> checkedList) {
        if(tabList != null && tabList.size() > 0) {
           this.tabInfoList = tabList;
        } else {
            tabInfoList = new ArrayList<TabInfo>();
        }
        if(checkedList != null && checkedList.size() > 0) {
            this.checkedNameList = checkedList;
        } else {
            checkedNameList = new ArrayList<>();
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
        UserTabHolder holder = null;
        if(convertView == null) {
            holder = new UserTabHolder();

            convertView = inflater.inflate(R.layout.ta_user_tab_item, null);
            holder.tabCheckBox = (CheckBox)convertView.findViewById(R.id.ta_checkbox_user_tab);

            convertView.setTag(holder);
        } else {
            holder = (UserTabHolder) convertView.getTag();
        }

        //填充内容
        holder.tabCheckBox.setText(tabInfoList.get(position).getTabName());
        //判断是否已经选中
        if(checkedNameList.contains(holder.tabCheckBox.getText())) {
            holder.tabCheckBox.setChecked(true);
        }

        return convertView;
    }

    private static class UserTabHolder {
        CheckBox tabCheckBox;
    }
}
