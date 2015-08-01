package com.tata.imta.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tata.imta.R;
import com.tata.imta.app.BaseActivity;

/**
 * Created by Will Zhang on 2015/7/6.
 * 关于我们简介页
 */
public class AboutMeActivity extends BaseActivity {

    private TextView mTitleTV;

    private RelativeLayout mBackRL;

    private RelativeLayout mMoreRL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ta_activity_about_me);

        initViews();
        initEvents();
    }

    @Override
    protected void initViews() {
        mTitleTV = (TextView) findViewById(R.id.ta_tv_topbar_title);
        mBackRL = (RelativeLayout) findViewById(R.id.ta_rl_topbar_back);
        mMoreRL = (RelativeLayout) findViewById(R.id.ta_rl_topbar_more);

        //处理标题栏
        mTitleTV.setText("关于我们");

        mMoreRL.setVisibility(View.GONE);
    }

    @Override
    protected void initEvents() {
        mBackRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
