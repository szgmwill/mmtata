package com.tata.imta.task;

import android.os.AsyncTask;

import com.tata.imta.bean.User;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.helper.UserHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Will Zhang on 2015/6/3.
 * 获取用户详细信息异步任务
 */
public class GetUserDetailTask extends AsyncTask<List<Long>, Void, Void> {

    /**
     * 业务回调
     */
    private GetUserDetailCallBack callBack;

    /**
     * 查询完后的结果
     */
    private Map<Long, User> resultMap;

    public GetUserDetailTask(GetUserDetailCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    protected Void doInBackground(List<Long>... params) {
        LogHelper.debug(this, "doInBackground============");

        resultMap = new HashMap<>();
        if(params != null && params.length > 0) {
            List<Long> idList = params[0];
            if(idList.size() > 0) {
                LogHelper.debug(this, "idList size:"+idList.size());

                //开始处理用户列表的详情信息
                for (Long userId : idList) {
                    User user = UserHelper.queryUserDetailFromId(userId);
                    if(user != null) {
                        resultMap.put(userId, user);
                    }
                }

            }

        }



        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        //处理完后回调业务的方法
        if(callBack != null) {
            callBack.onCallBack(resultMap);
        }
    }

    /**
     * 查询后回调
     */
    public interface GetUserDetailCallBack {
        void onCallBack(Map<Long, User> resultMap);
    }
}
