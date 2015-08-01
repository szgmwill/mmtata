package com.tata.imta.view;

import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeUser;
import com.tata.imta.R;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.app.BizInfoHolder;
import com.tata.imta.bean.ResultObject;
import com.tata.imta.bean.status.ServerAPI;
import com.tata.imta.component.DialogItem;
import com.tata.imta.helper.LoadDataFromServer;
import com.tata.imta.helper.MyDialog;
import com.tata.imta.helper.holder.GotyeHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Will Zhang on 2015/6/14.
 */
public class PopWindowMore extends PopupWindow implements View.OnClickListener {

    private static final String CONTENT_SEX = "色情";
    private static final String CONTENT_AD = "广告";
    private static final String CONTENT_SR = "骚扰";
    private static final String CONTENT_QZ = "欺诈";

    private View contentView;

    private BaseActivity activity;

    private long targetId;

    /**
     * 对话框
     */
    private MyDialog dialog;

    /**
     * 举报类型
     * 1-色情；2-广告；3-骚扰；4-欺诈；
     */
    private int jubaoType;

    /**
     * 是否我关注的好友
     */
    private boolean isMyFriend;

    /**
     * 是否我的黑名单
     */
    private boolean isMyBlock;

    public PopWindowMore(final BaseActivity context, long userId) {
        activity = context;
        targetId = userId;
        LayoutInflater inflater = LayoutInflater.from(context);

        contentView = inflater.inflate(R.layout.ta_item_popupwindow_more, null);

        // 设置SelectPicPopupWindow的View
        this.setContentView(contentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);

        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);

        //各个控件视图和事件初始化
        dialog = new MyDialog(context);


        isMyFriend = GotyeHelper.isMyFriend(targetId);
        isMyBlock = GotyeHelper.isMyBlock(targetId);

        //聊天,举报,拉黑按钮
        RelativeLayout mFollowRL = (RelativeLayout) contentView.findViewById(R.id.ta_rl_popup_follow);
        RelativeLayout mJubaoRL = (RelativeLayout) contentView.findViewById(R.id.ta_rl_popup_jubao);
        RelativeLayout mBlacklistRL = (RelativeLayout) contentView.findViewById(R.id.ta_rl_popup_blacklist);
        RelativeLayout mCancelRL = (RelativeLayout) contentView.findViewById(R.id.ta_rl_popup_cancel);
        TextView mShowBlockTV = (TextView) contentView.findViewById(R.id.ta_tv_popup_show_block);

        if(isMyBlock) {
            //已经是黑名单,则显示取消拉黑
            mShowBlockTV.setText("取消拉黑");
        } else {
            mShowBlockTV.setText("拉黑");
        }

        mFollowRL.setOnClickListener(this);
        mJubaoRL.setOnClickListener(this);
        mBlacklistRL.setOnClickListener(this);
        mCancelRL.setOnClickListener(this);
    }


    /**
     * 显示popupWindow
     *
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, 0, 0);
        }
    }

    /**
     * 关闭显示
     */
    public void dismissPopup() {
        if(this.isShowing()) {
            this.dismiss();
        }
    }

    /**
     *
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ta_rl_popup_follow :

                //关注和取消关注操作
                dismissPopup();
                break;
            case R.id.ta_rl_popup_jubao :

                //举报
                //调用后台登记举报
                //举报类型选择框
                DialogItem dialog_seqing = new DialogItem(CONTENT_SEX, this);
                DialogItem dialog_guanggao = new DialogItem(CONTENT_AD, this);
                DialogItem dialog_saorao = new DialogItem(CONTENT_SR, this);
                DialogItem dialog_qizha = new DialogItem(CONTENT_QZ, this);

                dialog.showContentChooseDialog("请选择举报类型",dialog_seqing,
                        dialog_guanggao,dialog_saorao,dialog_qizha);

                dismissPopup();
                break;

            case R.id.ta_rl_popup_blacklist :

                activity.showConfirmDialog("确认拉黑吗?拉黑后的用户将不能再聊天", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //拉黑
                        GotyeUser targetUser = new GotyeUser(String.valueOf(targetId));
                        //拉黑
                        if(isMyBlock) {
                            //已经是黑名单,取消拉黑
                            GotyeAPI.getInstance().reqRemoveBlocked(targetUser);
                        } else {

                            GotyeAPI.getInstance().reqAddBlocked(targetUser);///< 对应回调GotyeDelegate onAddBlocked，同时会更新本地黑名单列表
                        }
                    }
                });

                dismissPopup();
                break;

            case R.id.ta_rl_popup_cancel :

                //取消
                dismissPopup();
                break;
        }

        //举报动作
        if(v.getTag() != null && v.getTag() instanceof String) {
            String content = (String) v.getTag();
            switch (content) {
                case CONTENT_SEX :
                    jubaoType = 1;
                    sendJubao(jubaoType);
                    dialog.cancelAlertDialog();
                    break;
                case CONTENT_AD :
                    jubaoType = 2;
                    sendJubao(jubaoType);
                    dialog.cancelAlertDialog();
                    break;
                case CONTENT_SR :
                    jubaoType = 3;
                    sendJubao(jubaoType);
                    dialog.cancelAlertDialog();
                    break;
                case CONTENT_QZ :
                    jubaoType = 4;
                    sendJubao(jubaoType);
                    dialog.cancelAlertDialog();
                    break;
            }
        }
    }

    /**
     * 举报后台操作
     */
    private void sendJubao(int type) {
        Map<String, Object> bizMap = new HashMap<>();
        bizMap.put("user_id", BizInfoHolder.getInstance().getLoginUser().getUserId());
        bizMap.put("target_id", targetId);
        bizMap.put("type", type);
        LoadDataFromServer jubaoTask = new LoadDataFromServer(activity, ServerAPI.SERVER_API_ADD_REPORT, bizMap);
        jubaoTask.getData(new LoadDataFromServer.DataCallBack() {
            @Override
            public void onDataCallBack(ResultObject result) {
                activity.showAlertDialog("举报成功");
            }
        });
    }
}
