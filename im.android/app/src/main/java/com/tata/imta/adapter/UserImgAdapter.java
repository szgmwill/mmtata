package com.tata.imta.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tata.imta.R;
import com.tata.imta.activity.UserDetailActivity;
import com.tata.imta.bean.ImgInfo;
import com.tata.imta.helper.img.LoadUserImg;
import com.tata.imta.page.DisplayMultiImageActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Will Zhang on 2015/6/8.
 * 用户详情页头像图片填充器
 */
public class UserImgAdapter extends MyAdapter {
    /**
     * 当前处理的活动页
     */
    private UserDetailActivity activity;

    /**
     * 用户头像列表
     * 一般不超过8张
     */
    private List<ImgInfo> headList;

    private LayoutInflater inflater;

    /*
     * 图片加载器
     */
    private LoadUserImg imgLoader;

    public UserImgAdapter(UserDetailActivity activity, List<ImgInfo> headList) {
        this.headList = headList;

        this.activity = activity;

        inflater = LayoutInflater.from(activity);

        imgLoader = new LoadUserImg();
    }

    public void refresh() {
        //更新界面
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(headList != null) {
            return headList.size();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(headList != null) {
            return headList.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        HeadHolder holder = null;
        if(convertView == null) {
            holder = new HeadHolder();

            convertView = inflater.inflate(R.layout.ta_item_user_avatar, null);
            holder.headIV = (ImageView) convertView.findViewById(R.id.ta_item_iv_avatar);

            convertView.setTag(holder);
        } else {
            holder = (HeadHolder) convertView.getTag();
        }

        //读出当前的头像
        final ImgInfo headInfo = headList.get(position);

        holder.headIV.setTag(headInfo.getUrl());
        //展示图片
        imgLoader.showImgView(holder.headIV, headInfo.getUrl());

        holder.headIV.setId(position);

        //点击事件
        holder.headIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() < headList.size()) {
                    //发起大图查看
                    Intent intent = new Intent(activity, DisplayMultiImageActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("head_list", (ArrayList) headList);
                    intent.putExtras(bundle);
                    //当前点击的索引
                    intent.putExtra("index", position);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//如果当前前台在查看图片的话，直接复用当前的活动页
                    activity.startActivity(intent);

                    //不能结束,查看图片完后要返回的
                }
            }
        });

        return convertView;
    }

    private static class HeadHolder {
        private ImageView headIV;
    }
}
