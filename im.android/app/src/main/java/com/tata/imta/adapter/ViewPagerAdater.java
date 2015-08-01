package com.tata.imta.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.tata.imta.R;
import com.tata.imta.activity.GuideActivity;
import com.tata.imta.activity.MobileLoginActivity;

import java.util.List;

/**
 * Created by will on 2015/4/28.
 */
public class ViewPagerAdater extends PagerAdapter {

    private List<View> views;

    private Activity activity;

    public ViewPagerAdater(List<View> list, Activity atv) {
        views = list;
        activity = atv;
    }

    //删除元素
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager)container).removeView(views.get(position));
    }

    //初始化某个位置的view
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View currView = views.get(position);
        ((ViewPager) container).addView(currView);

        //判断一下是否已经最后一页了，并且最后一页有一个点击开启按钮
        if(position == views.size() - 1) {

            //添加登录微信事件
            RelativeLayout rl_wx = (RelativeLayout) currView.findViewById(R.id.ta_rl_login_wx);
            rl_wx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((GuideActivity) activity).wxLoginAuth();
                }
            });

            //手机号登录
            RelativeLayout rl_mobile = (RelativeLayout) currView.findViewById(R.id.ta_rl_login_mobile);
            rl_mobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(activity, MobileLoginActivity.class);

                    activity.startActivity(i);
//                    activity.finish();
                }
            });
        }


        return currView;
    }



    @Override
    public int getCount() {
        if(views != null) {
            return views.size();
        }

        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return (view == object);
    }
}
