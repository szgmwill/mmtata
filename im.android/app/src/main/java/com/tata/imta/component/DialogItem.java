package com.tata.imta.component;

import android.view.View;

/**
 * Created by Will Zhang on 2015/6/15.
 * 对话框内容元素
 */
public class DialogItem {

    /**
     * 处理单击事件的回调实例对象
     */
    private View.OnClickListener onClickListener;

    public DialogItem(String content, View.OnClickListener listener) {
        onClickListener = listener;
        this.content = content;
    }

    /**
     * 对话框展示内容
     */
    private String content;

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
