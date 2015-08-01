package com.tata.imta.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tata.imta.R;
import com.tata.imta.bean.OrderListItem;
import com.tata.imta.util.DateUtils;
import com.tata.imta.util.ShowUtils;

import java.util.List;

/**
 * Created by Will Zhang on 2015/6/28.
 * 我的消费支出明细
 */
public class MyOrderListAdapter extends MyAdapter {

    /**
     * 上下文
     */
    private Context context;

    private LayoutInflater inflater;

    /**
     * 数据列表
     */
    private List<OrderListItem> dataList;


    public MyOrderListAdapter(Context context, List<OrderListItem> dataList) {
        this.context = context;
        this.dataList = dataList;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if(dataList != null) {
            return dataList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(dataList != null) {
            return dataList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OrderShowHolder holder = null;

        if(convertView == null) {

            //初始化布局
            convertView = inflater.inflate(R.layout.ta_item_trade_list, null, false);

            holder = new OrderShowHolder();

            holder.target_nick = (TextView) convertView.findViewById(R.id.ta_tv_target_nick);
            holder.service_time = (TextView) convertView.findViewById(R.id.ta_tv_chat_time);
            holder.show_get = (TextView) convertView.findViewById(R.id.ta_tv_show_get);
            holder.show_pay = (TextView) convertView.findViewById(R.id.ta_tv_show_pay);
            holder.show_price = (TextView) convertView.findViewById(R.id.ta_tv_order_price);
            holder.show_time = (TextView) convertView.findViewById(R.id.ta_tv_order_time);

            //缓存
            convertView.setTag(holder);
        } else {
            holder = (OrderShowHolder) convertView.getTag();
        }

        //填充数据
        OrderListItem listItem = dataList.get(position);

        holder.target_nick.setText(listItem.getTarget_nick());
        holder.service_time.setText(ShowUtils.showOrderServiceTime(listItem.getFee_unit(), listItem.getFee_duration(),
                listItem.getBuy_num()));
        holder.show_price.setText(String.valueOf(listItem.getTotal_amt()));
        //判断是收入还是支出 1-支出；2-收入
        if(listItem.getType() == 1) {
            holder.show_pay.setVisibility(View.VISIBLE);
            holder.show_get.setVisibility(View.GONE);
        } else {
            holder.show_pay.setVisibility(View.GONE);
            holder.show_get.setVisibility(View.VISIBLE);
        }
        holder.show_time.setText(DateUtils.getTimeStrFromDate(listItem.getCreate_time()));

        return convertView;
    }

    /**
     * 重置结果
     */
    public void refreshDataList(List<OrderListItem> list) {

        if(list != null) {
            this.dataList = list;
        }
        notifyDataSetChanged();
    }


    private static class OrderShowHolder {
        TextView target_nick;
        TextView service_time;
        TextView show_pay;
        TextView show_get;
        TextView show_price;
        TextView show_time;
    }
}
