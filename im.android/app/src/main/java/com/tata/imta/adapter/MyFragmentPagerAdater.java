package com.tata.imta.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tata.imta.app.BaseFragment;

import java.util.List;

/**
 * Created by Will Zhang on 2015/5/3.
 */
public class MyFragmentPagerAdater extends FragmentPagerAdapter {

    //要切换的页面
    private List<BaseFragment> fragments;

    public MyFragmentPagerAdater (FragmentManager fm, List<BaseFragment> list) {
        super(fm);
        fragments = list;
    }

    @Override
    public int getCount() {

        if(fragments != null)
            return fragments.size();

        return 0;
    }


    @Override
    public Fragment getItem(int position) {
        if(fragments != null)
            return fragments.get(position);

        return null;
    }
}
