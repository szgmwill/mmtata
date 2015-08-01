package com.tata.imta.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tata.imta.R;

import java.io.IOException;
import java.util.List;


public class FaceGridViewAdapter extends BaseAdapter {
	private List<String> list;
	private Context mContext;

	/**
	 * 表情图标的存放路径
	 */
	private final String FACE_ASSETS_PATH = "face/png/";

	public FaceGridViewAdapter(List<String> list, Context mContext) {
		super();
		this.list = list;
		this.mContext = mContext;
	}

	public void clear() {
		this.mContext = null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHodler hodler;
		if (convertView == null) {
            //网格视图里的单个表情视图布局
			hodler = new ViewHodler();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.face_image, null);
			hodler.iv = (ImageView) convertView.findViewById(R.id.face_img);
			hodler.tv = (TextView) convertView.findViewById(R.id.face_text);
			convertView.setTag(hodler);
		} else {
			hodler = (ViewHodler) convertView.getTag();
		}
		try {
            //根据名称读取表情图标
			Bitmap mBitmap = BitmapFactory.decodeStream(mContext.getAssets().open(FACE_ASSETS_PATH + list.get(position)));
			hodler.iv.setImageBitmap(mBitmap);
		} catch (IOException e) {
			e.printStackTrace();
		}
        //表情配对的文字说明,暂不显示
		hodler.tv.setText(FACE_ASSETS_PATH + list.get(position));

		return convertView;
	}

	class ViewHodler {
		ImageView iv;
		TextView tv;
	}
}
