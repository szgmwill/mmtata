package com.tata.imta.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gotye.api.GotyeChatTarget;
import com.gotye.api.GotyeDelegate;
import com.gotye.api.GotyeMedia;
import com.gotye.api.GotyeMessage;
import com.gotye.api.GotyeMessageType;
import com.tata.imta.R;
import com.tata.imta.adapter.ChatListAdapter;
import com.tata.imta.app.BaseFragment;
import com.tata.imta.bean.ChatListItem;
import com.tata.imta.bean.User;
import com.tata.imta.helper.FragmentHelper;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.task.GetUserDetailTask;
import com.tata.imta.util.ShowUtils;
import com.tata.imta.util.TimeUtil;
import com.tata.imta.view.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Will Zhang on 2015/5/2.
 *
 * 用户会话列表页
 */
public class FragmentChat extends BaseFragment implements PullToRefreshView.OnHeaderRefreshListener,
        GetUserDetailTask.GetUserDetailCallBack {

    /**
     * 该面板的根布局
     */
    private View mFragmentRootView;

    //没有数据展示空白页
    private RelativeLayout mNoRecordRL;

    /**
     * 待填充的聊天记录列表
     */
    private ListView listView;

    /**
     * 展示底部tab总消息未读数
     */
    private TextView mAllUnreadCount;

    /**
     * 数据填充器
     */
    private ChatListAdapter chatAdapter;

    /**
     * 当前用户会话数据列表
     */
    private List<ChatListItem> chatList = new ArrayList<ChatListItem>();

    //上下拉刷新
    private PullToRefreshView mPullToRefreshView;

    //当前会话的第一条记录
    public static final String fixName = "通知列表";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //添加亲加监听事件
        gotyeAPI.addListener(delegate);
    }

    @Override
    public void onDestroy() {
        //不用时移除掉监听器
        gotyeAPI.removeListener(delegate);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        /**
         * 这里要注意fragment销毁的时候,要和上一次的rootview绑定,不能重新生成一个新的rootview
         */
        if(mFragmentRootView == null) {
            mFragmentRootView = inflater.inflate(R.layout.fragment_chat, container, false);
        }
        return mFragmentRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mFragmentRootView != null) {
            //从viewpager里暂时移除掉
            ((ViewGroup)mFragmentRootView.getParent()).removeView(mFragmentRootView);
        }
    }

    /**
     * 当页面切换到当前活动页时
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(!isInstanceDone) {

            mNoRecordRL = (RelativeLayout) getView().findViewById(R.id.ta_fragment_rl_norecord);

            //待填充的会话列表
            listView = (ListView) getView().findViewById(R.id.frag_chat_user_list);

            mAllUnreadCount = (TextView) getActivity().findViewById(R.id.unread_tv_chat);

            //拉动刷新
            mPullToRefreshView = (PullToRefreshView) getView().findViewById(R.id.ta_chat_pullrefresh_view);
            mPullToRefreshView.setOnHeaderRefreshListener(this);
            mPullToRefreshView.hideFooterRefreshView();//该展示不需要底部刷新

            //

            //标记所有视图已经初始化过了
            isInstanceDone = true;

        }

        //直接刷新列表
        refreshChatList();
    }

    /**
     * 刷新当前会话列表
     * 会话列表以亲加保存的数据为准,该数据只支持本地,不上传服务端
     */
    private void refreshChatList() {

        //重置所有未读消息数量
        int count = gotyeAPI.getInstance().getTotalUnreadMessageCount();
        LogHelper.debug(this, "refreshChatList ==> TotalUnreadMessageCount["+count+"]");

        if(count == 0) {
            mAllUnreadCount.setVisibility(View.GONE);
        } else {
            mAllUnreadCount.setText(String.valueOf(count));
            mAllUnreadCount.setVisibility(View.VISIBLE);
        }

        //获取本地会话列表（最近联系人列表,该列表不上传服务器）
        List<GotyeChatTarget> sessions = gotyeAPI.getSessionList();

        if(sessions != null && sessions.size() > 0) {
            LogHelper.debug(this, "gotye session list:"+sessions.size());

            //待查询的用户id列表
            List<Long> userIdList = new ArrayList<>();

            //如果有最近的会话列表,则重新刷新一次,没有则保留上一次的会话
            chatList.clear();//清除上一次的记录

            //处理每一条会话消息
            for(GotyeChatTarget target : sessions) {
                String uniqName = target.getName();

                long userId = ShowUtils.parseLong(uniqName);
                if(userId > 0 && !userIdList.contains(userId)) {
                    userIdList.add(userId);
                }

                ChatListItem item = new ChatListItem(userId);

                //考虑还有admin情况
                if(userId == 0) {
                    item.setNick(uniqName);//后台管理员发的消息
                }

                //查询每个会话对象最近的一次聊天消息
                GotyeMessage latestMsg = gotyeAPI.getLastMessage(target);
                if(latestMsg != null) {
                    LogHelper.debug(this, "target["+userId+"],latest msg["+latestMsg.toString()+"]");

                    String chatText = latestMsg.getText();//最新一条消息的内容
                    //图片和语音没有文本消息
                    if(latestMsg.getType() == GotyeMessageType.GotyeMessageTypeImage) {
                        //图片
                        chatText = "[图片]";
                    } else if(latestMsg.getType() == GotyeMessageType.GotyeMessageTypeAudio) {
                        //语音
                        chatText = "[语音]";
                    } else if(latestMsg.getType() == GotyeMessageType.GotyeMessageTypeUserData) {
                        //自定义消息
                        chatText = "[聊天服务确认]";
                    }

                    item.setContent(chatText);//会话内容
                    item.setLastMsgTime(TimeUtil.dateToMessageTime(latestMsg.getDate() * 1000));//最后会话时间
                }

                //与当前会话对象的未读消息数
                int unreadCount = gotyeAPI.getUnreadMessageCount(target);
                LogHelper.debug(this, "unreadCount["+unreadCount+"]");
                item.setUnreadNum(unreadCount);//未读消息数
                chatList.add(item);
            }

            if(chatList.size() == 0) {
                LogHelper.debug(this, "处理完会话消息后没有会话结果列表");
            }

            //发起批量查询用户详情,需要补充一下用户的基本信息
            GetUserDetailTask task = new GetUserDetailTask(this);
            task.execute(userIdList);
        } else {
            //没有数据,刷新UI
            chatList.clear();
            refreshUI();
        }


    }
    /**
     * 处理查询用户详情的回调结果
     */
    @Override
    public void onCallBack(Map<Long, User> resultMap) {
        //将查回来的用户详情抽取到展示结果里
        if(chatList != null && resultMap != null) {
            LogHelper.debug(this, "detailMap size:" + resultMap.size());
            for (ChatListItem item : chatList) {
                long userId = item.getUserId();

                if(userId > 0 && resultMap.containsKey(userId)) {
                    User user = resultMap.get(userId);
                    item.setNick(user.getNick());//昵称
                    item.setHeadUrl(user.getHead());//头像
                }
            }
        }
        //不管结果如何,都刷新一下UI
        refreshUI();
    }

    /**
     * 外部调用刷新本页面
     */
    @Override
    public void refresh() {
        FragmentHelper.refreshTopbarErrTips(getActivity(), null);

        refreshChatList();
    }

    /**
     * 显示列表有更新,刷新UI
     */
    public void refreshUI() {

        //下拉刷新完毕
        mPullToRefreshView.onHeaderRefreshComplete();

        if(chatList.size() == 0) {
            mNoRecordRL.setVisibility(View.VISIBLE);
        } else {
            //有数据
            mNoRecordRL.setVisibility(View.GONE);
        }

        if(chatAdapter == null) {
            chatAdapter = new ChatListAdapter(this, chatList);
            listView.setAdapter(chatAdapter);
        } else {
//            adapter.resetList(followList);
            //刷新UI
            chatAdapter.notifyDataSetChanged();
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
    public void onHeaderRefresh(PullToRefreshView view) {

        mPullToRefreshView.postDelayed(new Runnable() {

            @Override
            public void run() {
                refreshChatList();//重新加载数据
            }
        },1000);

    }

    //亲加通迅云的回调统一处理,重写你要处理的回调方法
    private GotyeDelegate delegate = new GotyeDelegate() {

        @Override
        public void onDownloadMedia(int code, GotyeMedia media) {
            if(getActivity().isTaskRoot()) {
                chatAdapter.notifyDataSetChanged();
            }
        }
    };
}
