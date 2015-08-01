package com.tata.imta.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tata.imta.R;
import com.tata.imta.adapter.FindAdapter;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.app.BaseFragment;
import com.tata.imta.bean.ResultObject;
import com.tata.imta.bean.UserFind;
import com.tata.imta.bean.status.ServerAPI;
import com.tata.imta.constant.ServerErrorCode;
import com.tata.imta.helper.FragmentHelper;
import com.tata.imta.helper.LoadDataFromServer;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.helper.UserHelper;
import com.tata.imta.util.JsonUtils;
import com.tata.imta.view.PullToRefreshView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Will Zhang on 2015/5/2.
 * 查询用户列表页
 */
public class FragmentFind extends BaseFragment implements View.OnClickListener,LoadDataFromServer.DataCallBack,
        PullToRefreshView.OnHeaderRefreshListener,PullToRefreshView.OnFooterRefreshListener {

    private BaseActivity context;

    /**
     * 条件筛选
     */
    private TextView mFilterTV;

    /**
     * 面板主布局
     */
    private View mFragmentRootView;

    /**
     * 待填充的用户列表
     */
    private ListView listView;

    /**
     * 数据填充器
     */
    private FindAdapter findAdapter;

    /**
     * 用户数据列表
     */
    private List<UserFind> findUserList = new ArrayList<UserFind>();

    private RelativeLayout mNoRecordRL;
    private LinearLayout mListLL;

    //上下拉刷新
    private PullToRefreshView mPullToRefreshView;

    //用户筛选类型
    private int userSex = 0;

    //当前拉取后台数据的翻页索引
    private int pageIndex = 1;
    //固定每次拉取20条
    private int pageSize = 20;

    //刷新的触发类型 1-当前页刷新;2-追加数据刷新
    private int refreshType = 1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = (BaseActivity) getActivity();
    }

    @Override
    public void onDestroy() {
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

    /**
     * 当页面切换到当前活动页时
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //每页面切换到当前页的时候都调用,所以要防止有一些不能重复处理的情况
        //重新重置一些变量
        userSex = 0;
        pageIndex = 1;

        if(!isInstanceDone) {//第一次加载要实例化视图控件
            mNoRecordRL = (RelativeLayout) getView().findViewById(R.id.ta_fragment_find_rl_norecord);
            mListLL = (LinearLayout) getView().findViewById(R.id.ta_fragment_find_ll_list);

            //待展示的用户列表
            listView = (ListView) getView().findViewById(R.id.frag_find_user_list);

            mFilterTV = (TextView) getView().findViewById(R.id.ta_tv_topbar_filter);

            RelativeLayout mFilterRL = (RelativeLayout) getView().findViewById(R.id.ta_rl_topbar_filter);
            mFilterRL.setVisibility(View.VISIBLE);
            mFilterRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getView().findViewById(R.id.ta_find_rl_choice).setVisibility(View.VISIBLE);
                }
            });


            //筛选按钮
            getView().findViewById(R.id.ta_find_btn_all).setOnClickListener(this);
            getView().findViewById(R.id.ta_find_btn_girl).setOnClickListener(this);
            getView().findViewById(R.id.ta_find_btn_boy).setOnClickListener(this);
            getView().findViewById(R.id.ta_find_btn_cancel).setOnClickListener(this);

            //拉动刷新
            mPullToRefreshView = (PullToRefreshView) getView().findViewById(R.id.ta_find_pullrefresh_view);
            mPullToRefreshView.setOnHeaderRefreshListener(this);
            mPullToRefreshView.setOnFooterRefreshListener(this);

            mPullToRefreshView.goHeaderRefresh();

            isInstanceDone = true;
        }

        //初始筛化选条件
        resetFilterShow();
    }

    private void resetFilterShow() {
        switch (userSex) {
            case 0 :
                mFilterTV.setText("(全部)");
                break;
            case 1 :
                mFilterTV.setText("(男)");
                break;
            case 2 :
                mFilterTV.setText("(女)");
                break;

        }
    }

    /**
     * 重新加载数据
     */
    private void reloadData() {
        //从服务器后台抓取要展示的满足条件的用户列表 getUserListFromServer
        //初始化数据填充
        Map<String, Object> paramMap = new HashMap<>();
//        paramMap.put("user_id", BizInfoHolder.getInstance().getLoginUser().getUserId());
        paramMap.put("page_index", pageIndex);
        paramMap.put("page_size", pageSize);
        paramMap.put("sex", userSex);

        LoadDataFromServer findList = new LoadDataFromServer(getActivity(), ServerAPI.SERVER_API_FIND_USER_LIST, paramMap);

        findList.getData(this);
    }


    /**
     *
     * 刷新当前用户列表
     */
    private void refreshUI() {

        //当前列表没有数据的话,展示没有数据图标说明
        if(findUserList.size() == 0) {
//            FragmentHelper.refreshTopbarErrTips(getActivity(), "查询不到匹配的用户");
            mNoRecordRL.setVisibility(View.VISIBLE);
            mListLL.setVisibility(View.GONE);
        } else {
            //有数据
            mNoRecordRL.setVisibility(View.GONE);
            mListLL.setVisibility(View.VISIBLE);
        }

        if(findAdapter == null) {
            findAdapter = new FindAdapter((BaseActivity)getActivity(), findUserList);
            listView.setAdapter(findAdapter);
        } else {
            //重新导入
//            findAdapter.setUserList(findUserList);
            findAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * 这里要注意fragment销毁的时候,要和上一次的rootview绑定,不能重新生成一个新的rootview
         */
        if(mFragmentRootView == null) {
            mFragmentRootView = inflater.inflate(R.layout.fragment_find, container, false);
        }
        return mFragmentRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    /**
     * 外部调用刷新本页面
     */
    @Override
    public void refresh() {
        FragmentHelper.refreshTopbarErrTips(getActivity(), null);

        reloadData();
    }

    @Override
    public void onClick(View v) {
        //处理筛选结果
        if(v.getId() == R.id.ta_find_btn_cancel) {
            getView().findViewById(R.id.ta_find_rl_choice).setVisibility(View.GONE);
        } else {
            //根据结果重新刷新列表
            if(v.getId() == R.id.ta_find_btn_all) {
                userSex = 0;
            } else if(v.getId() == R.id.ta_find_btn_boy) {
                userSex = 1;
            } else if(v.getId() == R.id.ta_find_btn_girl) {
                userSex = 2;
            }
            resetFilterShow();
            getView().findViewById(R.id.ta_find_rl_choice).setVisibility(View.GONE);

//            context.showProgressDialog("正在刷新...");
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    pageIndex = 1;
//                    refreshType = 1;
//                    reloadData();
//                }
//            }, 1000);

            //每次重新筛选结果的时候刷新一下列表
            mPullToRefreshView.goHeaderRefresh();
        }
    }

    /**
     * 查询在线用户后台返回用户列表回调
     */
    @Override
    public void onDataCallBack(ResultObject result) {
        context.dismissProgressDialog();

        if(result != null && result.getCode() == ServerErrorCode.OK) {
            //查询成功
            JSONObject data = JsonUtils.formatDataObj(result.getData());

            if(data != null) {
                LogHelper.debug(this, "onDataCallBack ==> result:"+data.toJSONString());
                JSONArray array = data.getJSONArray("user_list");
                if(array != null && array.size() > 0) {
                    List<UserFind> tempList = new ArrayList<>();
                    LogHelper.debug(getActivity(), "onDataCallBack find userlist:" + array.size());

                    for(int i=0;i<array.size();i++) {
                        JSONObject json = (JSONObject) array.get(i);
                        UserFind find = JsonUtils.json2Obj(json.toString(), UserFind.class);
                        if(find != null && !UserHelper.isLoginUser(find.getUserId())) {
                            LogHelper.debug(this, "find user:"+JsonUtils.toJson(find));
                            tempList.add(find);
                        }
                    }

                    if(tempList.size() > 0) {

                        //判断刷新类型
                        if(refreshType == 1) {
                            //当前页刷新
                            findUserList.clear();//清空上一次的
                            findUserList.addAll(tempList);
                        } else if(refreshType == 2) {
                            //追加刷新
                            findUserList.addAll(tempList);
                        }
                    }
                }
            }
        } else if(result != null && result.getCode() == ServerErrorCode.FAIL_RET_EMPTY) {
            //如果当前是查询第一页的话,没有数据,则清空列表
            if(pageIndex == 1) {
                findUserList.clear();
            }

        }

        //判断一下当前数据列表的长度,如果是pageSize的整数倍的话,说是可能还有下一页的数据,则展示底部刷新
        if(findUserList.size() % pageSize == 0) {
            mPullToRefreshView.showFooterRefreshView();
        } else {
            mPullToRefreshView.hideFooterRefreshView();
        }
        //加载完成
        mPullToRefreshView.onHeaderRefreshComplete();
        mPullToRefreshView.onFooterRefreshComplete();

        //不管结果如何,刷新一下UI
        refreshUI();
    }

    /**
     * 上拉加载更多
     */
    @Override
    public void onFooterRefresh(PullToRefreshView view) {

        pageIndex ++;
        refreshType = 2;//追加

        //为了保持刷新的时间间隔,提高视觉体验,统一间隔停留一段时间
        mPullToRefreshView.postDelayed(new Runnable() {

            @Override
            public void run() {
                LogHelper.debug(FragmentFind.class, "onFooterRefresh running ===>");

                //开始加载下一页内容
                reloadData();

            }
        }, 1000);
    }

    /**
     * 下拉刷新
     */
    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        //每次头部刷新都重置为1
        pageIndex = 1;
        refreshType = 1;//当前页刷新

        //为了保持刷新的时间间隔,提高视觉体验,统一间隔停留一段时间
        mPullToRefreshView.postDelayed(new Runnable() {

            @Override
            public void run() {
                LogHelper.debug(FragmentFind.class, "onHeaderRefresh running ===>");

                reloadData();//重新加载数据

            }
        },1000);

    }
}
