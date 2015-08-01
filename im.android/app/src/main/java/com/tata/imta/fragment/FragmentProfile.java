package com.tata.imta.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tata.imta.R;
import com.tata.imta.activity.AboutMeActivity;
import com.tata.imta.activity.FeeSettingActivity;
import com.tata.imta.activity.FeedBackActivity;
import com.tata.imta.activity.MyWalletActivity;
import com.tata.imta.activity.SettingsMainActivity;
import com.tata.imta.activity.UserDetailActivity;
import com.tata.imta.app.BaseFragment;
import com.tata.imta.app.BizInfoHolder;
import com.tata.imta.helper.FragmentHelper;
import com.tata.imta.helper.MyViewHelper;
import com.tata.imta.helper.img.LoadUserImg;
import com.tata.imta.util.DateUtils;
import com.tata.imta.util.ShowUtils;

/**
 * Created by Will Zhang on 2015/5/4.
 */
public class FragmentProfile extends BaseFragment {

    //视图组件========================start====================
    /**
     * 面板主布局
     */
    private View mFragmentRootView;

    private RelativeLayout mSettingRL;

    private RelativeLayout mProfileRL;

    private RelativeLayout mChatFeeRL;

    //我的钱包
    private RelativeLayout mMyWalletRL;

    //用户意见反馈
    private RelativeLayout mFeedbackRL;

    //关于我们
    private RelativeLayout mAboutMeRL;

    private ImageView mHeadIV;
    private TextView mNickTV;
    private TextView mSignTV;

    //视图组件========================end====================

    private LoadUserImg loadUserImg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadUserImg = new LoadUserImg();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * 这里要注意fragment销毁的时候,要和上一次的rootview绑定,不能重新生成一个新的rootview
         */
        if(mFragmentRootView == null) {
            mFragmentRootView = inflater.inflate(R.layout.fragment_profile, container, false);
        }
        return mFragmentRootView;
    }

    /**
     * 切换fragment的时候会调用
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        /**
         * 切换fragment时,要暂时解除掉当前视图在父容器中的绑定关系,然后再一次切回来调用
         * onCreateView的时候,才可以重新将当前视图和新的父容器重新绑定关系
         * 要不然会报:The specified child already has a parent.
         * You must call removeView() on the child's parent first
         */
        if(mFragmentRootView != null) {
            //从viewpager里暂时移除掉
            ((ViewGroup) mFragmentRootView.getParent()).removeView(mFragmentRootView);
        }
    }

    /**
     * 当该活动主页恢复显示时调用
     */
    @Override
    public void onResume() {
        super.onResume();
        boolean isHidden = this.isHidden();
        if (!isHidden) {
            refresh();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(!isInstanceDone) {
            //初始化组件
            mSettingRL = (RelativeLayout) getView().findViewById(R.id.ta_fragment_profile_rl_setting);
            mProfileRL = (RelativeLayout) getView().findViewById(R.id.ta_fragment_profile_rl_me);

            mChatFeeRL = (RelativeLayout) getView().findViewById(R.id.ta_fragment_profile_rl_chatset);

            mMyWalletRL = (RelativeLayout) getView().findViewById(R.id.ta_fragment_profile_rl_account);

            mHeadIV = (ImageView) getView().findViewById(R.id.ta_fragment_profile_iv_avatar);

            mNickTV = (TextView) getView().findViewById(R.id.ta_fragment_profile_tv_nick);

            mSignTV = (TextView) getView().findViewById(R.id.ta_fragment_profile_tv_sign);

            mFeedbackRL = (RelativeLayout) getView().findViewById(R.id.ta_fragment_profile_rl_feedback);

            mAboutMeRL = (RelativeLayout) getView().findViewById(R.id.ta_fragment_profile_rl_about);

            mSettingRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //进入设置页面
                    Intent i = new Intent();
                    i.setClass(getActivity(), SettingsMainActivity.class);
                    startActivity(i);

                }
            });

            mProfileRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), UserDetailActivity.class);
                    i.putExtra("user", BizInfoHolder.getInstance().getLoginUser());
                    startActivity(i);
                }
            });

            mChatFeeRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent();
                    i.setClass(getActivity(), FeeSettingActivity.class);
                    startActivity(i);
                }
            });

            mMyWalletRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent();
                    i.setClass(getActivity(), MyWalletActivity.class);
                    startActivity(i);
                }
            });

            //用户反馈,使用友盟组件
            mFeedbackRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //友盟页面
//                    FeedbackAgent agent = new FeedbackAgent(getActivity());
//                    agent.startFeedbackActivity();

                    //自己页面
                    Intent i = new Intent();
                    i.setClass(getActivity(), FeedBackActivity.class);
                    startActivity(i);

                }
            });

            mAboutMeRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent();
                    i.setClass(getActivity(), AboutMeActivity.class);
                    startActivity(i);
                }
            });

            //标记实例化好了
            isInstanceDone = true;
        }

        refreshContent();
    }

    /**
     * 填充页面内容
     */
    private void refreshContent() {
        //头像
        loadUserImg.showImgView(mHeadIV, loginUser.getHead());
        //昵称
        mNickTV.setText(loginUser.getNick());
        //个性签名
        mSignTV.setText(ShowUtils.showTextLimit(loginUser.getSign(), 20));

        //性别年龄
        MyViewHelper.showSexAgeView(getView(), loginUser.getSex(), DateUtils.getAgeFromDate(loginUser.getBirth()));
    }

    @Override
    public void refresh() {
        FragmentHelper.refreshTopbarErrTips(getActivity(), null);

        refreshContent();
    }
}
