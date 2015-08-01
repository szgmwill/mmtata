package com.tata.imta.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tata.imta.R;
import com.tata.imta.bean.AcctWithdraw;
import com.tata.imta.bean.status.AuditStatus;
import com.tata.imta.bean.status.WithdrawResult;
import com.tata.imta.util.DateUtils;

import java.util.List;

/**
 * Created by Will Zhang on 2015/6/29.
 */
public class MyWithdrawListAdapter extends MyAdapter {

    /**
     * 上下文
     */
    private Context context;

    private LayoutInflater inflater;

    /**
     * 数据列表
     */
    private List<AcctWithdraw> dataList;


    public MyWithdrawListAdapter(Context context, List<AcctWithdraw> dataList) {
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
        MyWithdrawHolder holder = null;

        if(convertView == null) {

            //初始化布局
            convertView = inflater.inflate(R.layout.ta_item_withdraw_list, null, false);

            holder = new MyWithdrawHolder();

            holder.tv_price = (TextView) convertView.findViewById(R.id.ta_tv_withdraw_price);
            holder.tv_time = (TextView) convertView.findViewById(R.id.ta_tv_withdraw_time);
            holder.tv_audit = (TextView) convertView.findViewById(R.id.ta_tv_withdraw_status);
            holder.tv_result = (TextView) convertView.findViewById(R.id.ta_tv_withdraw_result);

            //缓存
            convertView.setTag(holder);
        } else {
            holder = (MyWithdrawHolder) convertView.getTag();
        }

        //填充数据
        AcctWithdraw listItem = dataList.get(position);

        holder.tv_price.setText(String.valueOf(listItem.getAmount()));
        holder.tv_time.setText(DateUtils.getTimeStrFromDate(listItem.getCreate_time()));

        String audit = "[审核中]";
        AuditStatus status = listItem.getStatus();
        if(status == AuditStatus.PASS) {
            audit = "[审核通过]";
        } else if(status == AuditStatus.FAIL) {
            audit = "[审核不通过]";
        }
        holder.tv_audit.setText(audit);

        String showResult = "[转账成功]";
        WithdrawResult result = listItem.getResult();
        if(result == WithdrawResult.NEW) {
            showResult = "[未处理]";
        } else if(result == WithdrawResult.FAIL) {
            showResult = "[转账失败]";
        }
        holder.tv_result.setText(showResult);


        return convertView;
    }

    /**
     * 重置结果
     */
    public void refreshDataList(List<AcctWithdraw> list) {

        if(list != null) {
            this.dataList = list;
        }
        notifyDataSetChanged();
    }

    private static class MyWithdrawHolder {
        TextView tv_price;
        TextView tv_time;
        //审核状态
        TextView tv_audit;
        //转账结果
        TextView tv_result;
    }
}
