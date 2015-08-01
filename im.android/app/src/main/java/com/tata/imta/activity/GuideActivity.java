package com.tata.imta.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tata.imta.R;
import com.tata.imta.adapter.ViewPagerAdater;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.helper.WeixinSDKHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by will on 2015/4/28.
 *
 * 实现一个引导页的demo
 */
public class GuideActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    /**
     * 使用viewpager 实现滑动
     */
    private ViewPager vp;

    //数据填充
    private ViewPagerAdater vpAdater;

    //待填充的图片列表
    private List<View> viewList;

    //图片下面的小圆点
    private ImageView[] dots;

    //当前的页码索引
    private int currIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.guide);

        initViews();

        initEvents();

        initShowIndex();
    }

    /**
     * 实现再按一次退出程序,必须连续两次点击回退,而且时间差在2秒内
     * 防止用户误退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.exitByDoubleClick(keyCode, event);
    }

    public void initShowIndex() {
        Intent fromIntent = getIntent();
        String logoutFlag = fromIntent.getStringExtra("logout");
        if(logoutFlag != null && viewList != null) {
            onPageSelected(viewList.size() - 1);
            vp.setCurrentItem(currIndex);
        }
    }

    @Override
    protected void initViews() {
        //初始化视图
        //初始化所有引导图片页
        LayoutInflater inflater = getLayoutInflater();

        View v1 = inflater.inflate(R.layout.welcome_pic_one, null);

        viewList = new ArrayList<View>();

        viewList.add(v1);
        viewList.add(inflater.inflate(R.layout.welcome_pic_two, null));
        viewList.add(inflater.inflate(R.layout.welcome_pic_three, null));


        View v4 = inflater.inflate(R.layout.ta_guide_login, null);

        viewList.add(v4);

        vpAdater = new ViewPagerAdater(viewList, this);
        //填充viewpager视图
        vp = (ViewPager) findViewById(R.id.viewpager);

        vp.setAdapter(vpAdater);
        vp.setOnPageChangeListener(this);

        //初始化小圆点
        initDot();
    }

    @Override
    protected void initEvents() {
    }

    private void initDot() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.dotlist);

        int dotSize = ll.getChildCount();

        dots = new ImageView[dotSize];

        for(int i = 0; i < dotSize; i++) {

            //将引导页下面的所有小圆点初始化
            ImageView dot = (ImageView)ll.getChildAt(i);

            //初始状状下是灰色的
            dot.setEnabled(false);

            dots[i] = dot;
        }


        currIndex = 0;

        dots[currIndex].setEnabled(true);//刚进入第一个是选中状态
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        //切换页面的时候
        //重绘小圆点选中
        if(position >= 0 && position < dots.length) {
            dots[position].setEnabled(true);//当前选中

            //将原来的点恢复不选中状态
            dots[currIndex].setEnabled(false);

            currIndex = position;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * 点击微信登录处理
     * 登录流程：第一步：先登录微信服务器；第二步，登录业务服务器；第三方，登录第三方IM服务器
     */
    public void wxLoginAuth() {
//        Toast.makeText(this, "微信跳转中.....", Toast.LENGTH_SHORT).show();

        //拉起微信授权页
        WeixinSDKHelper.sendAuthLogin(this);

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
