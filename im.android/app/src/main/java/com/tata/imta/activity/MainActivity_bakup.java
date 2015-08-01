package com.tata.imta.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tata.imta.R;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.app.BaseFragment;
import com.tata.imta.fragment.FragmentChat;
import com.tata.imta.fragment.FragmentFind;
import com.tata.imta.fragment.FragmentFollow;
import com.tata.imta.fragment.FragmentProfile;
import com.tata.imta.helper.LogHelper;

import java.util.ArrayList;
import java.util.List;


/**
 *  APP登录后的主界面
 *  单例singleTask(只保存一个实例,并且其它所有回退到最后就只有这个界面,再回退就退出程序了)
 *  前台恢复到该页面时再回退将退出app
 */
public class MainActivity_bakup extends BaseActivity {

    //主界面主要由四大模块组成
    private FragmentFind fragmentFind;//发现模块

    private FragmentChat fragmentChat;//对话模块

    private FragmentFollow fragmentFollow;//关注模块

    private FragmentProfile fragmentProfile;//我的个人主页

    //fragment 布局容器
    private int frame_id = R.id.ta_main_frame;

    /**
     * 存放fragment实例
     */
    private List<BaseFragment> mFragmentList;

    //底部的tab image
    private ImageView[] tabViews;

    //底部的tab text
    private TextView[] textViews;

    //当前显示的fragment的索引
    private int currentIndex = 0;

    /**
     * fragment 管理器
     */
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogHelper.debug(this, "onCreate =====>");
        //加载主布局
        setContentView(R.layout.ta_activity_main_new);

        //jsut for test
        showAlertDialog(getString(R.string.app_name), "Welcome " + loginUser.getNick() + " !!!");

        fm = getSupportFragmentManager();

        mFragmentList = new ArrayList<>();

        initViews();

        initEvents();
    }

    @Override
    protected void onDestroy() {
        LogHelper.debug(this, "onDestroy ---->");
        super.onDestroy();
    }

    @Override
    public void finish() {
        LogHelper.debug(this, "finish()  ====>>");
        super.finish();
    }

    /**
     * 主页面由于是单例,每次重新回时只调用onNewIntent,不会再调用onCreate
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //刷新页面
        LogHelper.debug(this, "onNewIntent ===> ");
//        refreshMain(-1);
    }

    /**
     * 实现再按一次退出程序,必须连续两次点击回退,而且时间差在2秒内
     * 防止用户误退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.exitByDoubleClick(keyCode, event);
    }

    @Override
    protected void initViews() {

        //初始化所有模块

        fragmentFind = new FragmentFind();
        fragmentChat = new FragmentChat();
        fragmentFollow = new FragmentFollow();
        fragmentProfile = new FragmentProfile();

        //存到数据里,方便通过索引操作
        mFragmentList.add(fragmentFind);
        mFragmentList.add(fragmentChat);
        mFragmentList.add(fragmentFollow);
        mFragmentList.add(fragmentProfile);

        //tab的选中状态
        tabViews = new ImageView[mFragmentList.size()];
        tabViews[0] = (ImageView) findViewById(R.id.tab_iv_find);
        tabViews[1] = (ImageView) findViewById(R.id.tab_iv_chat);
        tabViews[2] = (ImageView) findViewById(R.id.tab_iv_follow);
        tabViews[3] = (ImageView) findViewById(R.id.tab_iv_profile);
        //初始化选中tab
        tabViews[0].setSelected(true);

        //文字部分选中
        textViews = new TextView[mFragmentList.size()];
        textViews[0] = (TextView) findViewById(R.id.tab_tv_find);
        textViews[1] = (TextView) findViewById(R.id.tab_tv_chat);
        textViews[2] = (TextView) findViewById(R.id.tab_tv_follow);
        textViews[3] = (TextView) findViewById(R.id.tab_tv_profile);
        //初始化文字选中
        textViews[0].setTextColor(getResources().getColor(R.color.ta_color_text_selected));

        //把第一个模块加入展示容器中
        fm.beginTransaction().add(frame_id, fragmentFind).commit();
    }

    @Override
    protected void initEvents() {

    }

    /**
     * 点击下面tab切换页面模块时
     */
    public void onTabClicked(View view) {
        int index = 0;
        switch (view.getId()) {
            case R.id.tab_layout_find:
                index = 0;
                break;
            case R.id.tab_layout_chat:
                index = 1;
                break;
            case R.id.tab_layout_follow:
                index = 2;
                break;
            case R.id.tab_layout_profile:
                index = 3;
                break;
        }

        fragmentExchage(index);

    }

    /**
     * 替换展示模块fragment
     */
    private void fragmentExchage(int clickIndex) {
        LogHelper.debug(this, "fragmentExchage :clickIndex[" + clickIndex + "],currentIndex[" + currentIndex + "]");

        if(clickIndex != currentIndex) {
            //有变化,要切换
            //显示
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(frame_id, mFragmentList.get(clickIndex));
            ft.commit();


            //图标选中切换
            tabViews[currentIndex].setSelected(false);
            tabViews[clickIndex].setSelected(true);

            //文字选中切换
            textViews[currentIndex].setTextColor(getResources().getColor(R.color.ta_color_text_normal));
            textViews[clickIndex].setTextColor(getResources().getColor(R.color.ta_color_text_selected));

            //同时更新当前索引
            currentIndex = clickIndex;
        }
    }

    /**
     * 主界面刷新
     * 各种面板fragment刷新
     * 有需要的组件也可以刷新
     * 比如最典型:用户登录，登出，重连；网络状态变化；消息通知变化等
     */
    public void refreshMain(int fragmentIndex) {
        //刷新每个面板
//        if(fragmentList != null && fragmentList.size() > 0 ) {
//
//            if(fragmentIndex > 0 && fragmentList.get(fragmentIndex) != null) {
//                fragmentList.get(fragmentIndex).refresh();
//            } else {
//                //全部刷新
//                for(BaseFragment fragment : fragmentList) {
//                    fragment.refresh();
//                }
//            }
//        }
    }
}
