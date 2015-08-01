package com.tata.imta.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.tata.imta.R;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.bean.ResultObject;
import com.tata.imta.bean.UserExtend;
import com.tata.imta.bean.status.FeeUnit;
import com.tata.imta.bean.status.ServerAPI;
import com.tata.imta.helper.LoadDataFromServer;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.helper.holder.SharePreferenceHolder;
import com.tata.imta.util.JsonUtils;
import com.tata.imta.util.ShowUtils;

import java.math.BigDecimal;

/**
 * Created by Will Zhang on 2015/6/23.
 * 聊天资费设置
 */
public class FeeSettingActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTitleTV;

    private RelativeLayout mBackRL;

    private RelativeLayout mMoreRL;

    private Spinner mFeeSetting;

    private RelativeLayout mOpenAllowRL;

    private ImageView mOpenAllowIV;

    private ImageView mCloseAllowIV;

    /**
     * 聊天资费档位
     */
    private String[] mFeeItems;

    private ArrayAdapter<String> adapter;

    /**
     * 当前选择的资费档位
     */
    private int feeSelectIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ta_activity_fee_setting);

        //先写死,后续考虑读后台,或本地xml配置化
        mFeeItems = getResources().getStringArray(R.array.fee_type);

        initViews();

        initSelected();

        initEvents();
    }

    @Override
    protected void initViews() {

        mTitleTV = (TextView) findViewById(R.id.ta_tv_topbar_title);
        mBackRL = (RelativeLayout) findViewById(R.id.ta_rl_topbar_back);
        mMoreRL = (RelativeLayout) findViewById(R.id.ta_rl_topbar_more);

        //处理标题栏
        mTitleTV.setText(loginUser.getNick());

        //显示保存按钮
        TextView saveTV = (TextView) mMoreRL.findViewById(R.id.ta_tv_topbar_more);
        saveTV.setVisibility(View.VISIBLE);
        saveTV.setText("保存");
        mMoreRL.findViewById(R.id.ta_iv_topbar_more).setVisibility(View.GONE);

        mFeeSetting = (Spinner) findViewById(R.id.ta_spinner_fee_select);
        // 建立Adapter并且绑定数据源
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mFeeItems);
        //绑定 Adapter到控件
        mFeeSetting.setAdapter(adapter);

        mOpenAllowRL = (RelativeLayout) findViewById(R.id.ta_rl_open_switch);

        mOpenAllowIV = (ImageView) findViewById(R.id.ta_iv_setting_allow_open);
        mCloseAllowIV = (ImageView) findViewById(R.id.ta_iv_setting_allow_close);
    }

    @Override
    protected void initEvents() {
        //注册所有监听事件
        mBackRL.setOnClickListener(this);
        mMoreRL.setOnClickListener(this);

        mFeeSetting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                feeSelectIndex = position;
                String str = parent.getItemAtPosition(position).toString();
//                ToastUtils.showShortToast(FeeSettingActivity.this, "你选择了:"+str);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        mOpenAllowRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOpenAllowIV.getVisibility() == View.VISIBLE) {
                    mOpenAllowIV.setVisibility(View.GONE);
                    mCloseAllowIV.setVisibility(View.VISIBLE);
                } else {
                    mOpenAllowIV.setVisibility(View.VISIBLE);
                    mCloseAllowIV.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ta_rl_topbar_back:
                this.back(v);
                break;
            case R.id.ta_rl_topbar_more:

                //将信息保存到后台
                UserExtend extend = loginUser.getExtend();
                if(extend == null) {
                    extend = new UserExtend();
                    extend.setUser_id(loginUser.getUserId());
                }
                //聊天服务费价格
                BigDecimal fee = null;

                //服务费计算标准
                FeeUnit fee_unit = null;

                //服务费单位计费时长
                int fee_duration = 1;

                /**
                 * <item>免费</item>
                   <item>0.5元/分钟</item>
                   <item>5元/小时</item>
                   <item>20元/天</item>
                 */
                //是否允许未付费用户搭讪聊天
                int allow_free_chat = 1;
                switch (feeSelectIndex) {//"免费", "0.5元/分钟", "5元/小时", "20元/天"
                    case 0 :
                        //免费哦

                        break;
                    case 1 :
                        fee = new BigDecimal(0.5);
                        fee_unit = FeeUnit.min;
                        fee_duration = 1;
                        break;
                    case 2 :
                        fee = new BigDecimal(5);
                        fee_unit = FeeUnit.hour;
                        fee_duration = 1;
                        break;

                    case 3 :
                        fee = new BigDecimal(20);
                        fee_unit = FeeUnit.day;
                        fee_duration = 1;
                        break;
                }

                if(fee != null && fee_unit != null) {
                    extend.setFee(fee);
                    extend.setFee_unit(fee_unit);
                    extend.setFee_duration(fee_duration);
                }

                //判断是否开关打开
                if(mOpenAllowIV.getVisibility() == View.VISIBLE) {
                    extend.setAllow_free_chat(1);//允许
                } else {
                    extend.setAllow_free_chat(0);//不允许
                }

                loginUser.setExtend(extend);

                //调用后台更新
                LogHelper.debug(this, "更新请求参数:"+ JsonUtils.toJson(extend));
                LoadDataFromServer updateExtend = new LoadDataFromServer(this, ServerAPI.SERVER_API_UPDATE_USER_EXTEND, extend);

                updateExtend.getData(new LoadDataFromServer.DataCallBack() {
                    @Override
                    public void onDataCallBack(ResultObject ro) {
                        showAlertDialog("操作提示", "更新信息成功");
                        //同时更新本地
                        SharePreferenceHolder.getInstance().saveUserInfo2Local(loginUser);
                    }
                });

        }
    }

    /**
     * 匹配聊天资费和其它开关
     */
    private void initSelected() {
        if(loginUser != null && loginUser.getExtend() != null) {
            UserExtend extend = loginUser.getExtend();
            String showUnit = ShowUtils.showUnit(extend);
            if(!"".equals(showUnit)) {
                String price = String.valueOf(extend.getFee().floatValue());
                String myFee = price + "元" + showUnit;
                LogHelper.debug(this, "price["+price+"], myFee["+myFee+"]");
                for(int i = 0; i < mFeeItems.length; i++) {
//                    LogHelper.debug(this, "mFeeItems["+i+"]="+mFeeItems[i]);
                    if(mFeeItems[i].equals(myFee)) {
                        feeSelectIndex = i;
                        mFeeSetting.setSelection(feeSelectIndex);
                    }
                }

            }

            if(loginUser.getExtend().getAllow_free_chat() == 1) {
                mOpenAllowIV.setVisibility(View.VISIBLE);
                mCloseAllowIV.setVisibility(View.GONE);
            } else {
                mOpenAllowIV.setVisibility(View.GONE);
                mCloseAllowIV.setVisibility(View.VISIBLE);
            }

        }
    }
}
