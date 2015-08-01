package com.tata.imta.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gotye.api.GotyeDelegate;
import com.gotye.api.GotyeMessage;
import com.gotye.api.GotyeStatusCode;
import com.tata.imta.R;
import com.tata.imta.adapter.MyFragmentPagerAdater;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.app.BaseFragment;
import com.tata.imta.fragment.FragmentChat;
import com.tata.imta.fragment.FragmentFind;
import com.tata.imta.fragment.FragmentFollow;
import com.tata.imta.fragment.FragmentProfile;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.helper.LoginHelper;

import java.util.ArrayList;
import java.util.List;


/**
 *  APP登录后的主界面
 *  单例singleTask(只保存一个实例,并且其它所有回退到最后就只有这个界面,再回退就退出程序了)
 *  前台恢复到该页面时再回退将退出app
 */
public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    //定义一些页面组件
    private ViewPager viewPager;

    //存放各个模块实例
    private List<BaseFragment> fragmentList;

    private MyFragmentPagerAdater fragmentAdapter;

    //底部的tab image
    private ImageView[] tabViews;

    //底部的tab text
    private TextView[] textViews;

    //当前切换页的索引
    private int currIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加载主布局
        setContentView(R.layout.ta_activity_main);

        //亲加回调监听
        gotyeAPI.addListener(delegate);

        //just for test
        showAlertDialog(getString(R.string.app_name), "Welcome " + loginUser.getNick() + " !!!");

        initViews();

        initEvents();
    }

    @Override
    protected void onDestroy() {
        gotyeAPI.removeListener(delegate);
        super.onDestroy();
    }

    @Override
    public void finish() {
        LogHelper.debug(this, "finish()  ===>>");
        super.finish();
    }

    /**
     * 主页面由于是单例,每次重新回时只调用onNewIntent,不会再调用onCreate
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //刷新页面
        LogHelper.debug(this, "onNewIntent ===> refreshMain [" + currIndex + "]");

        //刷新一下对话面板吧
        refreshMain(1);

        viewPager.setCurrentItem(1);
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

        //初始化所有视图组件
        fragmentList = new ArrayList<BaseFragment>();

        //先把几个待切换的页面初始化
        FragmentFind find = new FragmentFind();
        FragmentChat chat = new FragmentChat();
        FragmentFollow follow = new FragmentFollow();
        FragmentProfile profile = new FragmentProfile();

        fragmentList.add(find);//0
        fragmentList.add(chat);//1
        fragmentList.add(follow);//2
        fragmentList.add(profile);//3

        //tab的选中状态
        tabViews = new ImageView[fragmentList.size()];
        tabViews[0] = (ImageView) findViewById(R.id.tab_iv_find);
        tabViews[1] = (ImageView) findViewById(R.id.tab_iv_chat);
        tabViews[2] = (ImageView) findViewById(R.id.tab_iv_follow);
        tabViews[3] = (ImageView) findViewById(R.id.tab_iv_profile);
        //初始化选中图标
        tabViews[0].setSelected(true);

        //文字部分选中
        textViews = new TextView[fragmentList.size()];
        textViews[0] = (TextView) findViewById(R.id.tab_tv_find);
        textViews[1] = (TextView) findViewById(R.id.tab_tv_chat);
        textViews[2] = (TextView) findViewById(R.id.tab_tv_follow);
        textViews[3] = (TextView) findViewById(R.id.tab_tv_profile);
        //初始化选中文字
        textViews[0].setTextColor(getResources().getColor(R.color.ta_color_text_selected));


        //滑动页面填充模块
        fragmentAdapter = new MyFragmentPagerAdater(getSupportFragmentManager(), fragmentList);

        viewPager = (ViewPager) findViewById(R.id.ta_main_fragment_viewpager);

        viewPager.setAdapter(fragmentAdapter);

        //初始化当前页
        viewPager.setCurrentItem(currIndex);

        //设置切换页面时的监听事件
        viewPager.setOnPageChangeListener(this);
    }

    @Override
    protected void initEvents() {
        //初始化各种页面事件
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //滑动过程中的事件,无需处理
    }

    /**
     * 切换页面事件
     */
    @Override
    public void onPageSelected(int position) {
        //要刷新一下面版
//        fragmentList.get(position).refresh();

        setTabSelected(position);

        if(currIndex != position) {
            //重置一下当前页索引
            currIndex = position;

//            refreshMain(currIndex);
        }


    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //无需处理
    }

    /**
     * 点击下面tab切换时
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

        setTabSelected(index);

    }

    /**
     * 设置tab选中状态
     */
    private void setTabSelected(int newIndex) {
        if(currIndex != newIndex && newIndex < tabViews.length) {
            //页面有变化,要处理

            tabViews[newIndex].setSelected(true);
            tabViews[currIndex].setSelected(false);

            textViews[newIndex].setTextColor(getResources().getColor(R.color.ta_color_text_selected));
            textViews[currIndex].setTextColor(getResources().getColor(R.color.ta_color_text_normal));

            //切换当前页,后面会调用onPageSelected方法
            viewPager.setCurrentItem(newIndex);
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
        if(fragmentList != null && fragmentList.size() > 0 ) {

            if(fragmentIndex >= 0 && fragmentList.get(fragmentIndex) != null) {
                fragmentList.get(fragmentIndex).refresh();
            } else {
                //全部刷新
                for(BaseFragment fragment : fragmentList) {
                    fragment.refresh();
                }
            }
        }
    }

    //监听亲加通迅的消息推送
    private GotyeDelegate delegate = new GotyeDelegate() {
        /**
         * 消息通知推送
         * 当收到消息的时候做出UI的一定更新,提高用户体验
         */
        @Override
        public void onReceiveMessage(GotyeMessage message) {
            super.onReceiveMessage(message);

            LogHelper.info(this, "onReceiveMessage >>>>>:" + message.getText());

            //这条消息是否接收方是我
            if(message.getReceiver().getName().equals(gotyeUser.getName())) {
                //刷新一下对话面板吧
                refreshMain(1);
            }
        }

        /**
         *  当收到强制退出时,可能是账号在别的地方登录了
         *  强制刷新
         *
         *   登出回调（onLogout）中的状态码可能有以下三种情况：
         *   CodeOK：正常登出成功
         *   CodeNetworkDisConnected: 网络异常，转换成离线状态, 这种情况下API会自动重连
         *   CodeForceLogout：账号在其他设备登录，被强制下线
         */
        @Override
        public void onLogout(int code) {
            super.onLogout(code);

            if(code == GotyeStatusCode.CodeForceLogout) {
                showAlertDialog("账号提示", "你的账号在其它设备登录,被强制下线");
                LoginHelper.cancelLogin(MainActivity.this);
            }

        }
    };
}
