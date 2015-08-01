package com.tata.imta.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.tata.imta.helper.LogHelper;

import java.util.List;

/**
 * Created by Will Zhang on 2015/5/14.
 */
public class UserTabPageAdapter extends PagerAdapter {

    private List<View> views;

    private Activity activity;

    public UserTabPageAdapter(Activity activity, List<View> views) {
        this.activity = activity;
        this.views = views;
    }

    //删除元素
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager)container).removeView(views.get(position));

//        super.destroyItem(container, position, object);
    }

    //初始化某个位置的view
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View currView = views.get(position);
        ((ViewPager)container).addView(currView);

        //判断一下是否已经最后一页了，并且最后一页有一个提交设置按钮
        if(position == views.size() - 1) {
           LogHelper.debug(this, "is last index page");


           //处理提交
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
