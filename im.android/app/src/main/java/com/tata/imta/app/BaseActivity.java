package com.tata.imta.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeUser;
import com.tata.imta.R;
import com.tata.imta.activity.GuideActivity;
import com.tata.imta.activity.MobileLoginActivity;
import com.tata.imta.activity.MobileRegisterActivity;
import com.tata.imta.activity.WelcomeActivity;
import com.tata.imta.bean.User;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.wxapi.WXEntryActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by will on 2015/4/30.
 * 所有活动的基类
 * 可以将一些常用的方法封装在这里,以方便子类调用
 * 基类同时实现第三方im亲加API
 *
 */
public abstract class BaseActivity extends FragmentActivity {

    //对话框
    private ProgressDialog progressDialog;

    /**
     * 异步任务队列
     * 所有子类自动创建一个异步任务队列,方便子类使用
     */
    protected List<AsyncTask<String, Void, Void>> mAsyncTasks = new ArrayList<AsyncTask<String, Void, Void>>();

    /**
     * 亲加第三方API实例
     *
     */
    protected GotyeAPI gotyeAPI = GotyeAPI.getInstance();

    /**
     * 当前登录用户
     */
    protected User loginUser;

    /**
     * 当前登录用户(亲加)
     */
    protected GotyeUser gotyeUser = GotyeAPI.getInstance().getLoginUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //自定义管理活动实例,新增活动入栈
        ActivityManager.getInstance().pushActivity(this);

        loginUser = BizInfoHolder.getInstance().getLoginUser();

        if(loginUser == null) {

            //主页和引导页是不需要登录信息的
            String runTimeClass = this.getClass().getSimpleName();

            if(runTimeClass.equals(WXEntryActivity.class.getSimpleName())) {
                //微信登录页
            } else if(runTimeClass.equals(WelcomeActivity.class.getSimpleName())) {
                //加载页
            } else if(runTimeClass.equals(GuideActivity.class.getSimpleName())) {
                //引导页
            } else if(runTimeClass.equals(MobileLoginActivity.class.getSimpleName()) ) {
                //手机登录页
            } else if(runTimeClass.equals(MobileRegisterActivity.class.getSimpleName()) ){
                //手机注册页
            }
            else {
                //其它没有登录态的都统一导向引导页去
                startActivity(GuideActivity.class);
                finish();
            }
        } else {
            LogHelper.debug(this, "Current Login User["+loginUser.getUserId()+"]["+loginUser.getNick()+"]");

            if(gotyeUser == null || !gotyeUser.getName().equals(""+loginUser.getUserId())) {
                LogHelper.error(this, "Current ta_activity_mobile_login user not equals gotye ta_activity_mobile_login user!!!");
            }
        }
    }

    /**
     * 当该活动页又重新回到用户前台展示时
     */
    @Override
    protected void onResume() {
        super.onResume();

        // onresume时，取消notification显示
        //TO DO
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        /**
         * 任何activity销毁时都清理一下异步任务
         */
        clearAsyncTask();

        /**
         * 相关资源的释放
         */
        //TO DO

        //清理掉当前自定义活动栈里的该实例对象
//        ActivityManager.getInstance().popActivity(this);

        super.onDestroy();
    }

    /**
     * 回退键操作
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    /**
     * 返回
     *
     * @param view
     */
    public void back(View view) {
        finish();//将当前活动页结束,相当于返回上一个活动页?
    }

    @Override
    public void finish() {

        //当手工关闭该活动时也清理一下自定义栈
        ActivityManager.getInstance().popActivity(this);

        super.finish();
    }

    /**
     * 实现再按一次退出程序,必须连续两次点击回退,而且时间差在2秒内
     * 防止用户误退出
     */
    //标记退出的时间点差
    private long exitTime = 0;
    protected boolean exitByDoubleClick(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()- exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                //先把该实例之外的所有活动关闭掉
                ActivityManager.getInstance().popAllActivityExceptOne(this.getClass());
                finish();//关闭自身
                System.exit(0);//程序退出
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 将Activity的处理标准化处理
     */
    /** 初始化视图 **/
    protected abstract void initViews();

    /** 初始化事件 **/
    protected abstract void initEvents();

    /**
     * 以下为一些方便使用的公共方法,方便子类调用
     */

    protected void putAsyncTask(AsyncTask<String, Void, Void> asyncTask, String param) {
        mAsyncTasks.add(asyncTask.execute(param));
    }

    protected void clearAsyncTask() {
        Iterator<AsyncTask<String, Void, Void>> iterator = mAsyncTasks
                .iterator();
        while (iterator.hasNext()) {
            AsyncTask<String, Void, Void> asyncTask = iterator.next();
            if (asyncTask != null && !asyncTask.isCancelled()) {
                asyncTask.cancel(true);
            }
        }
        mAsyncTasks.clear();
    }


    /** 通过Class跳转界面 **/
    protected void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /** 含有Bundle通过Class跳转界面 **/
    protected void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /** 通过Action跳转界面 **/
    protected void startActivity(String action) {
        startActivity(action, null);
    }

    /** 含有Bundle通过Action跳转界面 **/
    protected void startActivity(String action, Bundle bundle) {
        Intent intent = new Intent();
        intent.setAction(action);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 进度提示对话框
     */
    public void showProgressDialog(String message) {
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage(message);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /** 含有标题和内容的对话框 **/
    public void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(this).setTitle(title)
                .setMessage(message).show();
    }

    /**
     * 自动关闭对话框
     */
    Handler handler = new Handler();
    public void showAutoFinishAlertDialog(String title, String message, long delay) {
        new AlertDialog.Builder(this).setTitle(title)
                .setMessage(message).show();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, delay);
    }

    /** 只含有内容的对话框 **/
    public void showAlertDialog(String message) {
        new AlertDialog.Builder(this).setMessage(message).show();
    }

    /**
     * 标准确认框
     * 确认和取消
     */
    public void showConfirmDialog(String title, DialogInterface.OnClickListener confirm) {
        new AlertDialog.Builder(this).setTitle(title)
                .setPositiveButton(R.string.confirm, confirm)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();//标准取消
                    }
                }).show();
    }

}
