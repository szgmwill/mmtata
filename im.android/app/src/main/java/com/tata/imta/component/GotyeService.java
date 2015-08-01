package com.tata.imta.component;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeDelegate;
import com.gotye.api.GotyeMessage;
import com.gotye.api.GotyeNotify;
import com.tata.imta.R;
import com.tata.imta.activity.MainActivity;
import com.tata.imta.bean.User;
import com.tata.imta.helper.ChatHelper;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.helper.holder.SharePreferenceHolder;
import com.tata.imta.util.AppUtil;
import com.tata.imta.util.NotificationUtils;

/**
 * 第三方IM亲加通迅云后台消息监测服务
 * 主要处理与亲加平台保持的长连接传输处理
 * NotifyListener主要处理各类消息通知：什么加群，退群,踢人,邀请这种的
 */
public class GotyeService extends Service {

    public static final String ACTION_INIT = "gotyeim.init";
	public static final String ACTION_LOGIN = "gotyeim.ta_activity_mobile_login";

    /**
     * 亲加平台的开发者应用appkey
     */
    public static final String gotyeAppKey = "84d1a6b2-c278-47a0-8361-be2c30c45ccb";

    @Override
	public IBinder onBind(Intent arg0) {
		LogHelper.debug(this, "onBind====>");
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		//开始接收离线消息
		GotyeAPI.getInstance().beginReceiveOfflineMessage();
		//添加推送消息监听
		GotyeAPI.getInstance().addListener(delegate);

        /**
         * 提醒:在做对应操作前请先添加Listener以免无法响应对应操作回调。
         * 提醒:当监听以后不再使用可以主动删除以免造成多个地方响应
         */

        /**
         * 为了简化使用，API的所有异步回调都是在UI主线程被执行，
         * 值得注意的是，API的接口本身并非线程安全的，
         * 在不同的线程去调用API接口，极有可能导致无法预料的运行结果，
         * 因此我们强烈推荐：仅在UI主线程去调用API接口。
         */
	}

    /**
     * 开启service服务后调用该方法进行任务处理
     * 可根据intent 中的action 来判断开启来源方
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//如果是其它主动发起过来的情况
		if (intent != null) {
			if (ACTION_LOGIN.equals(intent.getAction())) {//如果是要求登录的话
				String userid = intent.getStringExtra("user_id");
                if(!TextUtils.isEmpty(userid)) {
                    //登录亲加服务端
                    int code = GotyeAPI.getInstance().login(userid, null);//无密登录
                    LogHelper.info(this, "####### start service to ta_activity_mobile_login user:"+userid+",code:"+code);
                }
			} else if (ACTION_INIT.equals(intent.getAction())) {//如果是要求初始化
                //如果是初始化事件
				GotyeAPI.getInstance().setNetConfig("", -1);
				int code = GotyeAPI.getInstance().init(getBaseContext(), gotyeAppKey);
				LogHelper.info(this, "####### start service to init gotye api, code:"+code);
			}
		} else {//系统自启动情况
            //如果当前有登录用户信息的话:尝试重登录第三方,这样的话,后台任务会一直试图重新登录第三方im服务器,保证聊天不会断线
			User user = SharePreferenceHolder.getInstance().getLoginUserInfo();
			if (user != null && user.getUserId() > 0) {
				//登陆,注意code返回状态，-1表示正在登陆，1表示已经登陆或者正在登陆
				int code = GotyeAPI.getInstance().login(String.valueOf(user.getUserId()), null);//无密登录
				LogHelper.debug(this, "####### service running relogin user:"+user.getUserId());

				//会回调onLogin方法
				//如果业务后续对登录事件感兴趣的话,自己实现父类的onLogin方法即可
			}
		}

        /**
         * START_STICKY：如果service进程被kill掉，保留service的状态为开始状态，
         * 但不保留递送的intent对象。随后系统会尝试重新创建service，由于服务状态为开始状态，
         * 所以创建服务后一定会调用onStartCommand(Intent,int,int)方法。
         * 如果在此期间没有任何启动命令被传递到service，那么参数Intent将为null。
         */
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		LogHelper.debug(this, "gotye service is onDestroy...");
		GotyeAPI.getInstance().removeListener(delegate);
		Intent reIntent = new Intent();
		reIntent.setClass(this, GotyeService.class); //每次销毁又重新启动,这样service将一直运行
		this.startService(reIntent);
		super.onDestroy();
	}

    /**
     * 判断一下,如果推送消息到来的时候,该app正在运行,并且当前显示的是app和界面
     * 则不进行推送了
     * 通过包名判断
     */
    private boolean isNeedNotify() {
        boolean ret = true;
        String currentPackageName = AppUtil.getTopAppPackage(getBaseContext());
        String myPackageName = getPackageName();
		LogHelper.debug(this, "currentPackageName["+currentPackageName+"], myPackageName["+myPackageName+"]");
		if (currentPackageName.equals(myPackageName)) {
            ret = false;
        }

        return ret;
    }

	//申明一个监听器，在这个里面，重写那些您感兴趣的回调函数
	private GotyeDelegate delegate = new GotyeDelegate() {

		/**
		 * 消息通知
		 */
		@Override
		public void onReceiveMessage(GotyeMessage message) {
			super.onReceiveMessage(message);

			LogHelper.info(this, "onReceiveMessage >>>>>:" + message.getText());

			String showMsg = ChatHelper.showNotifyMsg(message);

//			final long senderId = ShowUtils.parseLong(message.getSender().getName());
//			if(senderId > 0) {
//				//查询一下发送方的用户信息
//				GetUserDetailTask senderTask = new GetUserDetailTask(new GetUserDetailTask.GetUserDetailCallBack() {
//					@Override
//					public void onCallBack(Map<Long, User> resultMap) {
//						if(resultMap != null && resultMap.containsKey(senderId)) {
//							String msg = resultMap.get(senderId).getNick() + " : " + ;
//						}
//					}
//				});
//
//			} else {
//				//有可能是admin发的消息
//
//			}

			if(isNeedNotify()) {
				NotificationUtils.notify(GotyeService.this, MainActivity.class, getString(R.string.app_name), showMsg);
			}

		}

		/**
		 * 监听一些特别通知,比如入群,踢人等
		 */
		@Override
		public void onReceiveNotify(GotyeNotify notify) {
			super.onReceiveNotify(notify);
			LogHelper.debug(GotyeService.this, "onReceiveNotify  ==> ");
		}
	};
}
