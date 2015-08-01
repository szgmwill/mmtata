package com.tata.imta.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.gotye.api.GotyeAPI;
import com.gotye.api.GotyeChatTarget;
import com.gotye.api.GotyeMessage;
import com.tata.imta.activity.SingleChatActivity;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.util.BitmapUtil;
import com.tata.imta.util.ToastUtils;

import java.io.File;

/**
 * 异步处理发送聊天图片
 */
public class SendImageMessageTask extends AsyncTask<String, String, String> {

	//图片大小不要超过限制
	public static final int IMAGE_MAX_SIZE_LIMIT = 1000;
	private GotyeMessage createMessage;
	private GotyeChatTarget target;
	private SingleChatActivity activity;

	public SendImageMessageTask(SingleChatActivity activity, GotyeChatTarget target) {
		this.target = target;
		this.activity = activity;
	}

	@Override
	protected String doInBackground(String... arg0) {
		File f = new File(arg0[0]);
		if (!f.exists()) {
			return null;
		}
		//校验图片特性,如果格式不是jpg或者图片太大都会先压缩后再发送处理
		if (f.length() < IMAGE_MAX_SIZE_LIMIT) {
			if (BitmapUtil.checkCanSend(f.getAbsolutePath())) {
				return f.getAbsolutePath();
			} else {
				return BitmapUtil.saveBitmapFile(BitmapUtil.getSmallBitmap(
						f.getAbsolutePath(), 500, 500));
			}
		} else {
			Bitmap bmp = BitmapUtil.getSmallBitmap(f.getAbsolutePath(), 500,
					500);
			if (bmp != null) {
				return BitmapUtil.saveBitmapFile(bmp);
			}
		}

		return null;
	}

	/**
	 * 注意： API允许发送的原始图片的文件最大为6MB。
	 * 发送图片时，API会生成一张原始图片的缩略图，该缩略图的文件大小被限制在6KB以内，
	 * 随同消息一同到达接收方。而原始图片则被上传到服务器，接收端在收到消息后需要调用下载接口才能获取到原始图片。
	 */
	private void sendImageMessage(String imagePath) {
		createMessage = GotyeMessage.createImageMessage(target, imagePath);
//		createMessage = GotyeMessage.createImageMessage(GotyeAPI.getInstance()
//				.getLoginUser(), target, imagePath);
		LogHelper.debug(this, "sendImageMessage ====> path:"+createMessage.getMedia().getPath());
		//回调处理更新UI onSendMessage()
		GotyeAPI.getInstance().sendMessage(createMessage);
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		if (result == null) {
			ToastUtils.showShortToast(activity, "请发送jpg图片");
			return;
		}
		sendImageMessage(result);
		if (createMessage == null) {
            ToastUtils.showShortToast(activity, "图片消息发送失败");
		} else {
			//发送完后回调主页面
			activity.callBackSendImageMessage(createMessage);
		}
		super.onPostExecute(result);
	}
}
