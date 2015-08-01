package com.tata.imta.adapter;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tata.imta.R;
import com.tata.imta.activity.MyDetailActivity;
import com.tata.imta.bean.ImgInfo;
import com.tata.imta.component.DialogItem;
import com.tata.imta.helper.MyDialog;
import com.tata.imta.util.FileUtils;
import com.tata.imta.helper.img.LoadUserImg;
import com.tata.imta.util.ToastUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Will Zhang on 2015/6/8.
 * 我的详情页头像图片填充器
 */
public class MyImgAdapter extends MyAdapter implements View.OnClickListener {

    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final String CONTENT_TAKEPHOTO = "拍照";
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final String CONTENT_GALLERY = "相册";
    private static final String CONTENT_DELETE = "删除";

    /**
     * 当前处理的活动页
     */
    private MyDetailActivity activity;

    /**
     * 用户头像列表
     * 一般不超过8张
     */
    private List<ImgInfo> headList;

    private LayoutInflater inflater;

    /*
     * 图片加载器
     */
    private LoadUserImg imgLoader;

    /**
     * 当前图片新名称
     */
    private String currImageName;

    /**
     * 当前处理图片索引
     */
    private int headIndex;

    /**
     * 对话框
     */
    private MyDialog dialog;

    public MyImgAdapter(MyDetailActivity activity, List<ImgInfo> headList) {
        this.headList = headList;

        addImgIcon();

        this.activity = activity;

        inflater = LayoutInflater.from(activity);

        imgLoader = new LoadUserImg();

        dialog = new MyDialog(activity);
    }

    /**
     * 重置头像列表
     */
    private void addImgIcon() {
        if(headList != null && headList.size() < 8) {//如果当前图片少于8张,则追加添加图片icon
            if(headList.get(headList.size() - 1).getUrl() != null) {
                ImgInfo imgAddIcon = new ImgInfo();
                imgAddIcon.setUserId(headList.get(0).getUserId());
                imgAddIcon.setUrl(null);

                headList.add(imgAddIcon);
            }
        }
    }

    public void refresh() {
        addImgIcon();

        //更新界面
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(headList != null) {
            return headList.size();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if(headList != null) {
            return headList.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        HeadHolder holder = null;
        if(convertView == null) {
            holder = new HeadHolder();

            convertView = inflater.inflate(R.layout.ta_item_user_avatar, null);
            holder.headIV = (ImageView) convertView.findViewById(R.id.ta_item_iv_avatar);

            convertView.setTag(holder);
        } else {
            holder = (HeadHolder) convertView.getTag();
        }

        //读出当前的头像
        final ImgInfo headInfo = headList.get(position);

        if(headInfo.getUrl() == null) {
            holder.headIV.setImageResource(R.drawable.ta_user_head_add_icon);
        } else {
            holder.headIV.setTag(headInfo.getUrl());
            //展示图片
            imgLoader.showImgView(holder.headIV, headInfo.getUrl());
        }

        holder.headIV.setId(position);

        //点击事件
        holder.headIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headIndex = v.getId();
                showPhotoDialog(headInfo);
            }
        });

        return convertView;
    }

    /**
     * 对头像进行编辑
     */
    private void showPhotoDialog(final ImgInfo userHead) {

        //生成图片存储名称,后面都以这个名称来气操作图片头像等
        currImageName = userHead.getUserImgFileName(headIndex);

        DialogItem item_camera = new DialogItem(CONTENT_TAKEPHOTO, this);
        DialogItem item_photo = new DialogItem(CONTENT_GALLERY, this);
        DialogItem item_del = null;
        if(headIndex > 0 && headIndex < (headList.size() - 1)) {
            item_del = new DialogItem(CONTENT_DELETE, this);
        }


        dialog.showContentChooseDialog("请选择", item_camera, item_photo, item_del);

    }

    @Override
    public void onClick(View v) {
        //通过setTag区分出元素
        String content = (String)v.getTag();
        switch (content) {
            case CONTENT_TAKEPHOTO :
                //先确认有权限
                if (FileUtils.isExternalStorageWritable()) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // 指定调用相机拍照后照片的储存路径
                    Uri uri = Uri.fromFile(new File(FileUtils.genImageFilePath(currImageName)));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                    //通知原活动页当前处理图片索引
                    activity.setHeadIndex(headIndex);
                    activity.setCurrImgName(currImageName);

                    activity.startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                    dialog.cancelAlertDialog();
                } else {
                    ToastUtils.showShortToast(activity, "请确认SDK卡可用且授权");
                }

                break;
            case CONTENT_GALLERY :
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

                //通知原活动页当前处理图片索引
                activity.setHeadIndex(headIndex);
                activity.setCurrImgName(currImageName);

                activity.startActivityForResult(intent, PHOTO_REQUEST_GALLERY);

                dialog.cancelAlertDialog();

                break;

            case CONTENT_DELETE :
                //删除掉当前的图片
                headList.remove(headIndex);

                refresh();

                dialog.cancelAlertDialog();

                break;

            default:
                break;
        }
    }

    private static class HeadHolder {
        private ImageView headIV;
    }
}
