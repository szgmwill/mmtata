package com.tata.imta.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tata.imta.R;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.bean.status.Sex;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.helper.holder.SharePreferenceHolder;

/**
 * Created by Will Zhang on 2015/5/26.
 */
public class UserTabSexActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 性别radio
     */
    private RadioGroup mRadioGroupSex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ta_user_guide_sex);

        initViews();

        initEvents();
    }

    /**
     * 实现再按一次退出程序,必须连续两次点击回退,而且时间差在2秒内
     * 防止用户误退出
     */
    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()- exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void initViews() {
        mRadioGroupSex = (RadioGroup) findViewById(R.id.ta_radiogroup_sex);

        //重置标题栏内容
        findViewById(R.id.ta_rl_topbar_back).setVisibility(View.GONE);
        findViewById(R.id.ta_rl_topbar_more).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.ta_tv_topbar_title)).setText("性别");

        //下一步
        Button btnNext = (Button) findViewById(R.id.ta_user_tab_submit);
        btnNext.setOnClickListener(this);

        //初始化性别
        LogHelper.debug(this, "ta_activity_mobile_login user sex:"+loginUser.getSex());
        if(loginUser.getSex().equals(Sex.GIRL)) {
            ((RadioButton) findViewById(R.id.ta_radio_user_sex_girl)).setChecked(true);
        } else {
            ((RadioButton) findViewById(R.id.ta_radio_user_sex_boy)).setChecked(true);
        }

        //设置妮称
        ((TextView) findViewById(R.id.tv_user_name)).setText(loginUser.getNick());
    }

    @Override
    protected void initEvents() {

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ta_user_tab_submit) {
            //进入下一步设置
            RadioButton checkSex = (RadioButton) findViewById(mRadioGroupSex.getCheckedRadioButtonId());
            if(checkSex.getId() == R.id.ta_radio_user_sex_girl) {
                //女的
                loginUser.setSex(Sex.GIRL);
            } else {
                loginUser.setSex(Sex.BOY);
            }

            SharePreferenceHolder.getInstance().saveUserInfo2Local(loginUser);

//            Intent i = new Intent();
//            i.putExtra("sex", loginUser.getSex());

            startActivity(UserTabTypeActivity.class);
        }
    }
}
