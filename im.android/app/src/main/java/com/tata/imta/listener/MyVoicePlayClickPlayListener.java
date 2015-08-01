package com.tata.imta.listener;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeDelegate;
import com.gotye.api.GotyeMessage;
import com.tata.imta.R;
import com.tata.imta.activity.SingleChatActivity;
import com.tata.imta.adapter.SingleChatAdapter;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.util.JsonUtils;
import com.tata.imta.util.ToastUtils;

import java.io.File;

/**
 * 语音聊天事件监听处理
 */
public class MyVoicePlayClickPlayListener implements View.OnClickListener {

	/**
	 * 当前处理的语音消息
	 */
	private GotyeMessage message;
	//语音图标
	private ImageView voiceIconView;

	private AnimationDrawable voiceAnimation = null;
	private SingleChatActivity activity;
	private BaseAdapter adapter;

	//当前是否有语音在播放
	public static boolean isPlaying = false;

    //单例模式
	public static MyVoicePlayClickPlayListener currentPlayListener = null;

	public MyVoicePlayClickPlayListener(GotyeMessage message, ImageView v,
										BaseAdapter adapter, SingleChatActivity activity) {
		this.message = message;
		this.adapter = adapter;
		voiceIconView = v;
		this.activity = activity;
	}

	public void stopPlayVoice(boolean byclick) {
		voiceAnimation.stop();//停止动画

		//恢复静态图标
		if (getDirect(message) == SingleChatAdapter.MESSAGE_DIRECT_RECEIVE) {
			voiceIconView.setImageResource(R.drawable.ta_chat_recv_voice_playing);
		} else {
			voiceIconView.setImageResource(R.drawable.ta_chat_send_voice_playing);
		}

		isPlaying = false;
		activity.setPlayingId(-1);
		adapter.notifyDataSetChanged();
		//是否手工点击停止的?
		if(byclick) {
			GotyeAPI.getInstance().stopPlay();
		}
	}

	private int getDirect(GotyeMessage message) {
		if (message.getSender().getName().equals(GotyeAPI.getInstance()
				.getLoginUser().getName())) {
			return SingleChatAdapter.MESSAGE_DIRECT_SEND;
		} else {
			return SingleChatAdapter.MESSAGE_DIRECT_RECEIVE;
		}
	}

	/**
	 * 播放本地的语音文件
	 * 输入文件路径
	 */
	public void playVoice(String filePath) {
		if (!(new File(filePath).exists())) {
			return;
		}
		//播放这条语音
		GotyeAPI.getInstance().playMessage(message);//对应回调GotyeDelegate中的onPlayStart，onPlaying和onPlayStop

		//重置当前语音id
		activity.setPlayingId(message.getDbId());
		isPlaying = true;

		//播放动画
		showAnimation();
	}

	// show the voice playing animation
	private void showAnimation() {
		// play voice, and start animation
		if (getDirect(message) == SingleChatAdapter.MESSAGE_DIRECT_RECEIVE) {
			voiceIconView.setImageResource(R.drawable.ta_voice_from_playing);
		} else {
			voiceIconView.setImageResource(R.drawable.ta_voice_to_playing);
		}
		//start playing
		voiceAnimation = (AnimationDrawable) voiceIconView.getDrawable();
		voiceAnimation.start();
	}

	@Override
	public void onClick(View v) {
		if (activity.onRealTimeTalkFrom >= 0) {
			ToastUtils.showShortToast(activity, "正在实时通话中");
			return;
		}
		//当播放语音的时候点击则停止播放
		if (isPlaying) {
			//如果有正在播放的情况,currentPlayListener实例肯定是上一次点击播放的实例
			if (activity.getPlayingId() == message.getDbId()) {//如果当前点击的消息就是正在播放的消息,则只需要停止即可
				currentPlayListener.stopPlayVoice(true);
				return;
			}
			currentPlayListener.stopPlayVoice(true);//先停止上一条,再重新播入下一条新的
		}

		//在播放一条新语音前,切换当前实例和添加亲加监听
		currentPlayListener = this;
		GotyeAPI.getInstance().addListener(delegate);

		//语音文件在本地的存放路径
		String path = message.getMedia().getPath();
		File file = new File(path);
		if (file.exists() && file.isFile()) {//如果已经下载好了,直接播放
			playVoice(path);
		} else {
			//没有需要下载
            ToastUtils.showShortToast(activity, "正在下载语音，稍后点击");
			//下载后回调方法里处理
			GotyeAPI.getInstance().downloadMediaInMessage(message);
		}
	}

	//亲加回调接口
	private GotyeDelegate delegate = new GotyeDelegate() {

		@Override
		public void onDownloadMediaInMessage(int code, GotyeMessage message) {
			super.onDownloadMediaInMessage(code, message);

			LogHelper.debug(MyVoicePlayClickPlayListener.this, "onDownloadMediaInMessage --> [" + JsonUtils.toJson(message) + "]");
			//如果是当前这条语音消息下载完了,直接播放
			if(message.getId() == currentPlayListener.message.getId()) {
				// 语音播放
				String path = message.getMedia().getPath();
				currentPlayListener.playVoice(path);
			}


		}

		@Override
		public void onPlayStart(int code, GotyeMessage message) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onPlaying(int code, int position) {
			// TODO Auto-generated method stub


		}

		@Override
		public void onPlayStop(int code) {
			stopPlayVoice(false);
			GotyeAPI.getInstance().removeListener(this);//播放停止时移除当前实例监听
		}

	};
}