package com.tata.imta.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.gotye.api.GotyeDelegate;
import com.gotye.api.GotyeUser;
import com.tata.imta.R;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.app.BizInfoHolder;
import com.tata.imta.bean.ResultObject;
import com.tata.imta.bean.status.ServerAPI;
import com.tata.imta.component.VersionUpdateService;
import com.tata.imta.helper.LoadDataFromServer;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.helper.LoginHelper;
import com.tata.imta.helper.ServerAPIHelper;
import com.tata.imta.util.NetworkUtils;
import com.tata.imta.util.ToastUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by will on 2015/4/28.
 * 演示首次进入app后的展示页,停留一段时间进行一些初始化工作
 */
public class WelcomeActivity extends BaseActivity {

    private static final int GO_MAIN = 1000;
    private static final int GO_GUIDE = 1001;

    //延迟多少秒间隔展示
    private static final long SHOW_DELAY = 3000;

    //本地版本号
    private int localVersion;
    //服务端版本号
    private int serverVersion;

    //回调处理
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_MAIN :
                    //已经有登录用户信息了,直接跳转吧
                    LoginHelper.loginGotye(WelcomeActivity.this, loginUser);
//                    LoginHelper.loginServer(WelcomeActivity.this, loginUser);
                    break;
                case GO_GUIDE :
                    //第一次进入用户,图片引导页登录
                    startActivity(GuideActivity.class);
                    finish();
                    break;
            }

            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogHelper.debug(this, "onCreate========:" + Thread.currentThread().getId());
        //绑定主布局
        setContentView(R.layout.ta_activity_welcome);

        gotyeAPI.addListener(delegate);

        //初始化一些启动参数
        init();
    }

    @Override
    protected void onDestroy() {
        gotyeAPI.removeListener(delegate);
        super.onDestroy();
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initEvents() {

    }

    /**
     * 全局初始化相关环境信息,业务信息等
     */
    private void init() {

        //各业务初始化
        //TO DO
        initFileDir();

        //判断版本升级
        initVersion();
    }

    private void go() {
        //读取设备中是否已经有用户信息了
        if(loginUser == null) {
            //首次进入，要播图
            handler.sendEmptyMessageDelayed(GO_GUIDE, SHOW_DELAY);
        } else {
            LogHelper.debug(this, "本地用户[" + loginUser.getUserId() + "]直接登录");
            handler.sendEmptyMessageDelayed(GO_MAIN, SHOW_DELAY);
        }
    }

    /**
     * 检测当前客户端版本信息,判断是否需要升级
     */
    private void checkVersion(final String apk) {
        //先判断当前网络是否可用
        if (NetworkUtils.isNetworkConnected(WelcomeActivity.this)) {
            LogHelper.debug(this, "开始检测更新");
            //判断本地和服务器版本是否一致
            LogHelper.debug(this, "verion ==> local["+localVersion+"], remote["+serverVersion+"]");
            if(localVersion != serverVersion) {
                //版本需要升级
                // 发现新版本，提示用户更新

                AlertDialog.Builder alert = new AlertDialog.Builder(WelcomeActivity.this);
                alert.setTitle("软件升级").setMessage("发现新版本,建议立即更新使用.")
                        .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 开启更新服务UpdateService
                                Intent updateIntent = new Intent(WelcomeActivity.this, VersionUpdateService.class);
                                if(!TextUtils.isEmpty(apk)) {
                                    updateIntent.putExtra("fileName", apk);
                                }

                                startService(updateIntent);

                                go();
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        go();
                    }
                });
                alert.create().show();
            } else {
                LogHelper.debug(this, "没有新版本，无需更新");
                go();
            }
        }
    }

    public void initVersion() {
        LogHelper.debug(this, "initVersion ==> ");
        localVersion = getResources().getInteger(R.integer.app_version_code);
        LogHelper.debug(this, "local version is:" + localVersion);

        //读取后台的版本号
        Map<String, String> param = new HashMap<>();
        param.put("test", "test");
        LoadDataFromServer versionTask = new LoadDataFromServer(this,
                ServerAPI.SERVER_API_VERSION_INFO, param);

        versionTask.getData(new LoadDataFromServer.DataCallBack() {
            @Override
            public void onDataCallBack(ResultObject result) {
                JSONObject json = ServerAPIHelper.handleServerResult(WelcomeActivity.this, result);
                if(json != null && json.containsKey("version")) {
                    json = (JSONObject) json.get("version");
                    LogHelper.debug(BizInfoHolder.class, "server version:" + json.toJSONString());
                    serverVersion = json.getInteger("version_code");
                    String apkFileName = json.getString("apk_file_name");

                    checkVersion(apkFileName);
                } else {
                    //后台服务器出错,不更新
                    LogHelper.debug(WelcomeActivity.this, "服务器版本校验出错");
                    ToastUtils.showShortToast(WelcomeActivity.this, "服务器版本校验出错");
                    go();
                }
            }
        });
    }

    /**
     * 初始化app自身需要的文件目录
     */
    public void initFileDir() {

        File dir = new File("/sdcard/mmtata");

        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    //亲加服务回调统一处理,只重写你要处理的回调方法
    private GotyeDelegate delegate = new GotyeDelegate() {
        @Override
        public void onLogin(int code, GotyeUser currentLoginUser) {
            LogHelper.debug(this, "onLogin ==> code["+code+"],user["+currentLoginUser.getName()+"]");

            LoginHelper.onGotyeLoginCallback(WelcomeActivity.this, code, currentLoginUser);
        }

        @Override
        public void onGetFriendList(int code, List<GotyeUser> mList) {
            super.onGetFriendList(code, mList);
            if(mList != null) {
                LogHelper.debug(this, "down load friend list from gotye OK:"+mList.size());
            }

        }

        @Override
        public void onGetBlockedList(int code, List<GotyeUser> mList) {
            super.onGetBlockedList(code, mList);
            if(mList != null) {
                LogHelper.debug(this, "down load block list from gotye OK:" + mList.size());
            }

        }
    };
}
