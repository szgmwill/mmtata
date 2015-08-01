package com.tata.imta.page;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.gotye.api.GotyeDelegate;
import com.gotye.api.GotyeMessage;
import com.gotye.api.GotyeMessageType;
import com.tata.imta.R;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.util.BitmapUtil;
import com.tata.imta.view.photo.PhotoView;
import com.tata.imta.view.photo.PhotoViewAttacher;

import java.io.File;

/**
 * 聊天图片(大图)查看器
 * 单图
 * 注意该活动页的启动方式最好是singleTop,因为要下载大图,网络延迟较大,下载完成后,通过onNewIntent来更新UI
 */
public class DisplayBigImageActivity extends BaseActivity {

	private PhotoView image;

	/**
	 * 加载进度
	 */
	private ProgressBar progressBar;

	/**
	 * 默认图片
	 */
	private int default_res = R.drawable.ic_launcher;

	/**
	 * 要显示的图片(大图)
	 */
	private Bitmap bitmap;

	/**
	 * 当前显示的图片的本地path
	 */
	private String iv_path;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//全屏显示
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ta_activity_show_big_image);

		//添加亲加回调,以处理大图下载完成后的回调更新UI
		gotyeAPI.addListener(delegate);

		image = (PhotoView) findViewById(R.id.ta_pv_show_big_image);

		progressBar = (ProgressBar) findViewById(R.id.ta_pb_loading);

		//当单击的时候退出
		image.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
			@Override
			public void onPhotoTap(View view, float x, float y) {
				finish();
			}
		});

		show(getIntent());
	}

	@Override
	protected void onDestroy() {

		gotyeAPI.removeListener(delegate);

		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}

		super.onDestroy();
	}

	@Override
	protected void initViews() {

	}

	@Override
	protected void initEvents() {

	}

	/**
	 * 控制单击时关闭
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 当下载完大图后会调用到这里来
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent != null) {
			show(intent);
		}
	}

	private void show(Intent intent) {
		Uri uri = intent.getParcelableExtra("uri");
		//如果有传入默认图的话,代表当前并不是要最终展示的大图,可能是小图,
		// 所以要显示进度,待后续大图下载完后再调用该活动页时处理
		boolean isTemp = getIntent().getBooleanExtra("isTemp", false);//是否是临时缩略图

		if (uri != null && new File(uri.getPath()).exists()) {
			iv_path = uri.getPath();
//			byte[] bitmapData = FileUtil.getBytes(path);
//			bitmap = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);

			bitmap = BitmapUtil.getBitmap(iv_path);

			if (bitmap != null) {
				image.setImageBitmap(bitmap);
				if(isTemp) {
					progressBar.setVisibility(View.VISIBLE);
				}
			}
		} else {
			//显示默认图
			image.setImageResource(default_res);
		}
	}

	//亲加服务回调统一处理,只重写你要处理的回调方法
	private GotyeDelegate delegate = new GotyeDelegate() {
		/**
		 * 处理图片的下载后回调
		 */
		@Override
		public void onDownloadMediaInMessage(int code, GotyeMessage message) {
			super.onDownloadMediaInMessage(code, message);
			if(message.getType() == GotyeMessageType.GotyeMessageTypeImage) {//只处理图片类型消息
				String path = message.getMedia().getPath();
				LogHelper.debug(this, "onDownloadMediaInMessage ==> code[" + code + "], path[" + path + "],iv_path["+iv_path+"]");

				//如果当前下载完的图片路径与当前展示图片一致并且当前显示的图片并非最终图(有进度),则更新当前UI
				if(path != null && path.equals(iv_path) && progressBar.getVisibility() == View.VISIBLE) {
					String pathEx = message.getMedia().getPathEx();

					if(pathEx != null) {
						Bitmap bigOne = BitmapUtil.getBitmap(pathEx);
						if(bigOne != null) {
							bitmap = bigOne;
							progressBar.setVisibility(View.INVISIBLE);
							image.setImageBitmap(bitmap);
						}
					}

				}
			}
		}
	};
}
