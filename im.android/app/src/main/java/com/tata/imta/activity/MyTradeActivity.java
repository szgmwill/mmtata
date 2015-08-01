package com.tata.imta.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tata.imta.R;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.app.BaseFragment;
import com.tata.imta.app.ContentView;
import com.tata.imta.app.ViewInject;
import com.tata.imta.fragment.FragmentMyOrderList;
import com.tata.imta.fragment.FragmentMyWithdrawList;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.util.ViewInjectUtils;

/**
 * Created by Will Zhang on 2015/6/27.
 * 我的收入支出提现明细
 */
@ContentView(R.layout.ta_activity_my_trade)
public class MyTradeActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.ta_tv_topbar_title)
    private TextView mTitleTV;
    @ViewInject(R.id.ta_rl_topbar_back)
    private RelativeLayout mBackRL;
    @ViewInject(R.id.ta_rl_topbar_more)
    private RelativeLayout mMoreRL;

    @ViewInject(R.id.ta_rl_my_trade_order)
    private RelativeLayout mTradeOrderRL;

    @ViewInject(R.id.ta_view_order_selected)
    private View mOrderSelected;

    @ViewInject(R.id.ta_rl_my_trade_withdraw)
    private RelativeLayout mTradeWithdrawRL;

    @ViewInject(R.id.ta_view_withdraw_selected)
    private View mWithdrawSelected;

    private FragmentManager fm;

    /**
     * 订单展示模块
     */
    private FragmentMyOrderList mOrderFragment;

    /**
     * 提现展示模块
     */
    private FragmentMyWithdrawList mWithdrawFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewInjectUtils.inject(this);

        fm = getSupportFragmentManager();

        initViews();

        initEvents();


    }

    @Override
    protected void initViews() {
        mTitleTV.setText("我的详单");

        mMoreRL.setVisibility(View.GONE);

        //初始化面板
        mOrderFragment = new FragmentMyOrderList();
        mWithdrawFragment = new FragmentMyWithdrawList();

        fm.beginTransaction().add(R.id.ta_fragment_my_trade_list, mOrderFragment).commit();
    }

    @Override
    protected void initEvents() {
        mBackRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTradeOrderRL.setOnClickListener(this);

        mTradeWithdrawRL.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ta_rl_my_trade_order) {

            mOrderSelected.setVisibility(View.VISIBLE);
            mWithdrawSelected.setVisibility(View.GONE);

            //查看我的消费明细
            fragmentExchage(mOrderFragment);

        } else if (v.getId() == R.id.ta_rl_my_trade_withdraw) {

            mOrderSelected.setVisibility(View.GONE);
            mWithdrawSelected.setVisibility(View.VISIBLE);

            //查看我的提现明细
            fragmentExchage(mWithdrawFragment);
        }
    }

    /**
     * 替换面板内容
     */
    private void fragmentExchage(BaseFragment fragment) {
        LogHelper.debug(this, "fragmentExchage :"+fragment);
        if(fragment instanceof FragmentMyOrderList) {
            LogHelper.debug(this, "show order fragment");
            //显示
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.ta_fragment_my_trade_list, fragment);
            ft.commit();

        } else if(fragment instanceof FragmentMyWithdrawList) {
            LogHelper.debug(this, "show withdraw fragment");
            //显示
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.ta_fragment_my_trade_list, fragment);
            ft.commit();
        }
    }
}
