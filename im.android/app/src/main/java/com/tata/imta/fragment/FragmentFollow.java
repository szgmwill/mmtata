package com.tata.imta.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.gotye.api.GotyeDelegate;
import com.gotye.api.GotyeUser;
import com.tata.imta.R;
import com.tata.imta.adapter.FollowAdapter;
import com.tata.imta.app.BaseFragment;
import com.tata.imta.bean.User;
import com.tata.imta.helper.FragmentHelper;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.task.GetUserDetailTask;
import com.tata.imta.view.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Will Zhang on 2015/5/4.
 */
public class FragmentFollow extends BaseFragment implements PullToRefreshView.OnHeaderRefreshListener {

    /**
     * 当前模块实例的根视图
     */
    private View mFragmentRootView;

    //上下拉刷新
    private PullToRefreshView mPullToRefreshView;

    /**
     * 待填充的关注用户列表
     */
//    private SwipeMenuListView listView;
    private ListView listView;

    //没有数据展示空白页
    private RelativeLayout mNoRecordRL;

    /**
     * 数据填充器
     */
    private FollowAdapter adapter;

    /**
     * 用户数据列表
     */
    private List<User> followList = new ArrayList<User>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //添加必要的监听器处理
        gotyeAPI.addListener(delegate);
    }

    @Override
    public void onDestroy() {
        //不用时移除掉监听器
        gotyeAPI.removeListener(delegate);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        /**
         * 这里要注意fragment销毁的时候,要和上一次的rootview绑定,不能重新生成一个新的rootview
         */
        if(mFragmentRootView == null) {
            mFragmentRootView = inflater.inflate(R.layout.fragment_follow, container, false);
        }
        return mFragmentRootView;
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

            //待填充的关注列表
            listView = (ListView) getView().findViewById(R.id.frag_follow_user_list);

            //拉动刷新
            mPullToRefreshView = (PullToRefreshView) getView().findViewById(R.id.ta_follow_pullrefresh_view);
            mPullToRefreshView.setOnHeaderRefreshListener(this);

            //这个模块暂时不考虑分页读取吧,直接一次读取所有
            mPullToRefreshView.hideFooterRefreshView();
        }

        //每次进来都刷新一下当前列表
        refreshFollowList();
    }

    /**
     * 通过外部调用刷新当前面板UI
     */
    @Override
    public void refresh() {
        FragmentHelper.refreshTopbarErrTips(getActivity(), null);

        //重新读取列表进行全量更新
        refreshFollowList();
    }

    /**
     * 刷新当前关注用户列表
     * 每次刷新都从亲加读取的本地好友列表中分段读取
     */
    private void refreshFollowList() {

        //从第三方im获取最新的好友(关注)列表
        List<GotyeUser> myFriendList = gotyeAPI.getLocalFriendList();//获取本地的好友列表,如需要时可向亲加服务端刷新列表
        //gotyeAPI.reqFriendList();//如需向服务器请求更新该列表,回调: void onGetFriendList(int code, List<GotyeUser> friendlist);

        if(myFriendList != null && myFriendList.size() > 0) {
            LogHelper.debug(this, "refreshFollowList ==> friendList size:"+myFriendList.size());
            final List<Long> userIdList = new ArrayList<Long>();

            for(GotyeUser gotyeUser : myFriendList) {
                userIdList.add(Long.parseLong(gotyeUser.getName()));
            }
            //批量查询用户详情信息
            GetUserDetailTask getUserDetailTask = new GetUserDetailTask(new GetUserDetailTask.GetUserDetailCallBack() {
                @Override
                public void onCallBack(Map<Long, User> resultMap) {
                    followList.clear();//先清掉上一次的
                    for(int i = 0; i < userIdList.size(); i++) {
                        User targetUser = resultMap.get(userIdList.get(i));
                        if(targetUser != null) {
                            followList.add(targetUser);
                        }
                    }
                    //列表更新完后刷新界面
                    refreshUI();

                    //同时加载完成
                    mPullToRefreshView.onHeaderRefreshComplete();
                }
            });
            getUserDetailTask.execute(userIdList);
        } else {
            //没有好友列表了
            followList.clear();
            //列表更新完后刷新界面
            refreshUI();
            //同时加载完成
            mPullToRefreshView.onHeaderRefreshComplete();
        }
    }

    /**
     * 一般显示列表有变化时需要更新当前界面UI
     */
    private void refreshUI() {
        if(followList.size() == 0) {
//            FragmentHelper.refreshTopbarErrTips(getActivity(), "刷新关注列表失败");
            mNoRecordRL.setVisibility(View.VISIBLE);
        } else {
            //有数据
            mNoRecordRL.setVisibility(View.GONE);
        }

        if(adapter == null) {
            adapter = new FollowAdapter(this, followList);
            listView.setAdapter(adapter);
        } else {
//            adapter.resetList(followList);
            //刷新UI
            adapter.notifyDataSetChanged();
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
    public void onDestroyView() {
        if(mFragmentRootView != null) {
            //从viewpager里暂时移除掉
            ((ViewGroup) mFragmentRootView.getParent()).removeView(mFragmentRootView);
        }
        super.onDestroyView();
    }


    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        mPullToRefreshView.postDelayed(new Runnable() {

            @Override
            public void run() {
                LogHelper.debug(FragmentFollow.class, "onHeaderRefresh running ===>");

                //刷新内容
                refreshFollowList();
            }
        }, 1000);
    }

    //亲加服务回调统一处理,只重写你要处理的回调方法
    private GotyeDelegate delegate = new GotyeDelegate() {
        /**
         * 刷新关注列表后回调
         */
        @Override
        public void onGetFriendList(int code, List<GotyeUser> mList) {
            super.onGetFriendList(code, mList);
            LogHelper.debug(this, "onGetFriendList ==> code[" + code + "]");
        }

        /**
         * 取消关注后回调
         */
        @Override
        public void onRemoveFriend(int code, GotyeUser user) {
            super.onRemoveFriend(code, user);
            LogHelper.debug(this, "onRemoveFriend ==> code["+code+"],user["+user.getName()+"]");

            //删除关注后,一般关注列表即有变化,亲加回调回来时同时更新UI
            refreshUI();
        }
    };
}
