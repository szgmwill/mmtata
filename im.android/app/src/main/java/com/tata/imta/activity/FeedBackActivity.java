package com.tata.imta.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tata.imta.R;
import com.tata.imta.app.BaseActivity;

/**
 * Created by Will Zhang on 2015/7/6.
 * 用户意见收集页面
 */
public class FeedBackActivity extends BaseActivity {

    private TextView mTitleTV;

    private RelativeLayout mBackRL;

    private RelativeLayout mMoreRL;

    private EditText mSuggestionET;

    private RelativeLayout mBtnSubmitRL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ta_activity_suggestion);

        initViews();

        initEvents();
    }

    @Override
    protected void initViews() {
        mSuggestionET = (EditText) findViewById(R.id.ta_suggestion_content);

        mBtnSubmitRL = (RelativeLayout) findViewById(R.id.ta_suggestion_btn);

        mTitleTV = (TextView) findViewById(R.id.ta_tv_topbar_title);
        mBackRL = (RelativeLayout) findViewById(R.id.ta_rl_topbar_back);
        mMoreRL = (RelativeLayout) findViewById(R.id.ta_rl_topbar_more);

        //处理标题栏
        mTitleTV.setText("意见反馈");

        mMoreRL.setVisibility(View.GONE);
    }

    @Override
    protected void initEvents() {
        mBtnSubmitRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedback = mSuggestionET.getText().toString();
                if(TextUtils.isEmpty(feedback)) {
                    showAlertDialog("操作提示", "请填写意见后再提交");
                } else if(feedback.length() > 500) {
                    showAlertDialog("操作提示", "字数超制,意见请简结清晰");
                } else {

                    //提交意思到后台录入
                    // TO DO

                    showAlertDialog("意见反馈", "感谢您的宝贵意见,我们会及时跟进处理,谢谢参与");

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 2000);//2秒后自动关闭


                }
            }
        });
    }
}
