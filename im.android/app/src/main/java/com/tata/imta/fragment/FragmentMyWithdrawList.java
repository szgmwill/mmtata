package com.tata.imta.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tata.imta.R;
import com.tata.imta.adapter.MyWithdrawListAdapter;
import com.tata.imta.app.BaseFragment;
import com.tata.imta.bean.AcctWithdraw;
import com.tata.imta.bean.ResultObject;
import com.tata.imta.bean.status.ServerAPI;
import com.tata.imta.helper.LoadDataFromServer;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.helper.ServerAPIHelper;
import com.tata.imta.util.JsonUtils;
import com.tata.imta.view.PaginationListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Will Zhang on 2015/6/29.
 * 我的提现明细模块
 */
public class FragmentMyWithdrawList extends BaseFragment {

    /**
     * 面板主布局
     */
    private View mFragmentRootView;

    private PaginationListView mPageListView;

    private MyWithdrawListAdapter adapter;

    /**
     * 订单列表
     */
    private List<AcctWithdraw> withdrawList;

    /**
     * 当前接取数据的页码数
     */
    private int pageIndex = 1;

    /**
     * 每次翻页拉取的数量(固定值)
     */
    private int pageSize = 20;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //从后台加载订单数据
        loadData();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * 这里要注意fragment销毁的时候,要和上一次的rootview绑定,不能重新生成一个新的rootview
         */
        if(mFragmentRootView == null) {
            mFragmentRootView = inflater.inflate(R.layout.ta_fragment_listview, container, false);
        }
        return mFragmentRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPageListView = (PaginationListView) getView().findViewById(R.id.ta_lv_trade_list);

        mPageListView.setOnLoadListener(new PaginationListView.OnLoadListener() {
            @Override
            public void onLoad() {
                loadData();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if(mFragmentRootView != null) {
            //从viewpager里暂时移除掉
            ((ViewGroup) mFragmentRootView.getParent()).removeView(mFragmentRootView);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 从后台加载数据
     */
    private void loadData() {
        Map<String, Object> param = new HashMap<>();
        param.put("page_index", pageIndex);
        param.put("page_size", pageSize);
        param.put("user_id", loginUser.getUserId());
//        param.put("status", "pass");//拉取全部

        LoadDataFromServer queryWithdrawList = new LoadDataFromServer(getActivity(), ServerAPI.SERVER_API_QUERY_MY_WITHDRAW_LIST, param);

        queryWithdrawList.getData(new LoadDataFromServer.DataCallBack() {
            @Override
            public void onDataCallBack(ResultObject result) {
                JSONObject dataJson = ServerAPIHelper.handleServerResult(getActivity(), result);

                if (dataJson != null) {

                    JSONArray array = dataJson.getJSONArray("data_list");

                    if (array != null && array.size() > 0) {
                        LogHelper.debug(this, "server side ret list:" + array.size());

                        if (withdrawList == null) {
                            withdrawList = new ArrayList<AcctWithdraw>();
                        }

                        for (Object json : array) {
                            AcctWithdraw withdraw = JsonUtils.json2Obj(json.toString(), AcctWithdraw.class);
                            if (withdraw != null) {
                                //添加
                                withdrawList.add(withdraw);
                            }
                        }

                        pageIndex++;//自动翻页
                    }
                }

                //刷新UI
                refresh();
            }
        });
    }

    @Override
    public void refresh() {

        //关闭提示加载完成
        mPageListView.loadComplete();

        if(adapter == null) {
            adapter = new MyWithdrawListAdapter(getActivity(), withdrawList);
            mPageListView.setAdapter(adapter);
        } else {
            adapter.refreshDataList(null);
        }
    }
}
