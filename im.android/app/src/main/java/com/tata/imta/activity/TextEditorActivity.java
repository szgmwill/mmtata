package com.tata.imta.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tata.imta.R;
import com.tata.imta.app.BaseActivity;

/**
 * Created by Will Zhang on 2015/6/17.
 * 文本编辑页
 */
public class TextEditorActivity extends BaseActivity {

    private TextView mTitleTV;

    private RelativeLayout mBackRL;

    private RelativeLayout mMoreRL;

    private EditText contentET;

    private TextView tipsTV;

    private int reqCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ta_activity_text_editor);

        initViews();

        initEvents();

        //根据送过来的内容进行处理
        Intent from = getIntent();

        if(from != null) {
            String title = from.getStringExtra("title");
            String content = from.getStringExtra("content");
            String tips = from.getStringExtra("tips");

            reqCode = from.getIntExtra("code", 0);

            mTitleTV.setText(title);
            contentET.setText(content);
            tipsTV.setText(tips);
        }
    }

    @Override
    protected void initViews() {
        mTitleTV = (TextView) findViewById(R.id.ta_tv_topbar_title);
        mBackRL = (RelativeLayout) findViewById(R.id.ta_rl_topbar_back);
        mMoreRL = (RelativeLayout) findViewById(R.id.ta_rl_topbar_more);
        contentET = (EditText) findViewById(R.id.ta_texteditor_content);
        tipsTV = (TextView) findViewById(R.id.ta_texteditor_tips);

        //重置标题
        mMoreRL.setVisibility(View.GONE);
    }

    @Override
    protected void initEvents() {

    }

    /**
     * 当关闭时将填写内容回传回去
     */
    @Override
    public void finish() {
        String value = contentET.getText().toString().trim();
        if(TextUtils.isEmpty(value)) {
            showAlertDialog("错误提示", "内容为空,请重新填写");
        } else {
            Intent toIntent = new Intent();
            toIntent.putExtra("content", value);
            setResult(reqCode, toIntent);
            super.finish();
        }
    }
}
