package com.tata.imta.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tata.imta.R;
import com.tata.imta.adapter.MyImgAdapter;
import com.tata.imta.adapter.UserDetailTabAdapter;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.app.ContentView;
import com.tata.imta.app.ViewInject;
import com.tata.imta.bean.ImgInfo;
import com.tata.imta.bean.ResultObject;
import com.tata.imta.bean.status.ServerAPI;
import com.tata.imta.constant.RequestCode;
import com.tata.imta.helper.LoadDataFromServer;
import com.tata.imta.helper.LogHelper;
import com.tata.imta.helper.MyDialog;
import com.tata.imta.helper.holder.SharePreferenceHolder;
import com.tata.imta.util.FileUtils;
import com.tata.imta.helper.img.QiniuCloudLoader;
import com.tata.imta.util.DateUtils;
import com.tata.imta.util.JsonUtils;
import com.tata.imta.util.ToastUtils;
import com.tata.imta.util.ViewInjectUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Will Zhang on 2015/6/5.
 * 登录用户自身详情页编辑
 * 更新个人信息页
 */
@ContentView(R.layout.ta_activity_my_detail)
public class MyDetailActivity extends BaseActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener{

    /**
     * 图片处理码
     */
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 图片裁剪后结果

    /**
     * 当前处理的头像图片索引
     */
    private int headIndex = 0;

    /**
     * 当前处理的头像图片名称
     */
    private String currImgName;

    /**
     * 七牛图片上传
     */
    private QiniuCloudLoader qiniuCloudLoader;

    //读出用户头像列表
    List<ImgInfo> headList = new ArrayList<>();

    //===================================

    /**
     * ================视图控件====start=============
     */

    //当前是否有改动过个人信息
    private boolean isUpdated = false;

    @ViewInject(R.id.ta_tv_topbar_title)
    private TextView mTitleTV;

    @ViewInject(R.id.ta_rl_topbar_back)
    private RelativeLayout mBackRL;

    @ViewInject(R.id.ta_rl_topbar_more)
    private RelativeLayout mMoreRL;

    @ViewInject(R.id.ta_my_detail_rl_nick)
    private RelativeLayout mNickRL;
    @ViewInject(R.id.ta_my_detail_tv_nick)
    private TextView mNickTV;

    @ViewInject(R.id.ta_my_detail_rl_birth)
    private RelativeLayout mBirthRL;
    @ViewInject(R.id.ta_my_detail_tv_birth)
    private TextView mBirthTV;

    @ViewInject(R.id.ta_my_detail_rl_city)
    private RelativeLayout mCityRL;
    @ViewInject(R.id.ta_my_detail_tv_city)
    private TextView mCityTV;

    //个性签名
    @ViewInject(R.id.ta_my_detail_rl_sign)
    private RelativeLayout mSignRL;
    @ViewInject(R.id.ta_my_detail_tv_sign)
    private TextView mSignTV;

    //职业
    @ViewInject(R.id.ta_my_detail_rl_career)
    private RelativeLayout mCareerRL;
    @ViewInject(R.id.ta_my_detail_tv_career)
    private TextView mCareerTV;

    //标签
    @ViewInject(R.id.ta_my_detail_rl_tab)
    private RelativeLayout mTabRL;

    //网络视图
    @ViewInject(R.id.ta_gv_my_detail_img)
    private GridView mHeadGV;

    //我的标签展示
    @ViewInject(R.id.ta_gv_my_detail_tab)
    private GridView mTabGV;

    //adapter
    private MyImgAdapter imgAdapter;

    private UserDetailTabAdapter tabAdapter;

    private MyDialog myDialog;
    //=================视图控件=====end=============

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //初始化视图
        ViewInjectUtils.inject(this);

        qiniuCloudLoader = new QiniuCloudLoader(this);

        myDialog = new MyDialog(this);

        //初始化视图
        initViews();

        //添加动作事件
        initEvents();

        //内容初始化
        initContent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initViews() {
    }

    @Override
    protected void initEvents() {
        //注册所有监听事件
        mBackRL.setOnClickListener(this);
        mMoreRL.setOnClickListener(this);

        mNickRL.setOnClickListener(this);
        mBirthRL.setOnClickListener(this);

        mCityRL.setOnClickListener(this);
        mSignRL.setOnClickListener(this);
        mCareerRL.setOnClickListener(this);

        mTabRL.setOnClickListener(this);
    }

    /**
     * 内容填充处理
     */
    private void initContent() {
        //根据当前登录用户显示相关内容

        if(loginUser != null) {
            LogHelper.debug(this, "initContent, loginUser:"+JsonUtils.toJson(loginUser));
            //处理标题栏
            mTitleTV.setText(loginUser.getNick());

            //显示保存按钮
            TextView saveTV = (TextView) mMoreRL.findViewById(R.id.ta_tv_topbar_more);
            saveTV.setVisibility(View.VISIBLE);
            saveTV.setText("保存");
            mMoreRL.findViewById(R.id.ta_iv_topbar_more).setVisibility(View.GONE);

            mNickTV.setText(loginUser.getNick());

            mBirthTV.setText(DateUtils.getDayStrFromDate(loginUser.getBirth()));

            mCityTV.setText(loginUser.getLocation());

            mSignTV.setText(loginUser.getSign());

            mCareerTV.setText(loginUser.getCareer());

            //按理来说,至少有一张头像以上才对
            if(loginUser.getImgList() != null && loginUser.getImgList().size() > 0) {
                headList.addAll(loginUser.getImgList());
            }
            //填充头像列表
            if(headList.size() > 0) {
                mHeadGV.setVisibility(View.VISIBLE);
                imgAdapter = new MyImgAdapter(this, headList);
                mHeadGV.setAdapter(imgAdapter);
            }

            //标签填充
            if(loginUser.getTabList() != null && loginUser.getTabList().size() > 0) {
                mTabGV.setVisibility(View.VISIBLE);
                tabAdapter = new UserDetailTabAdapter(this, loginUser.getTabList(), 0);
                mTabGV.setAdapter(tabAdapter);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ta_rl_topbar_back:
                this.back(v);
                break;
            case R.id.ta_rl_topbar_more:

                //头像更新
                if(headList.size() > 0) {
                    loginUser.getImgList().clear();
                    for(int i = 0; i < headList.size(); i++) {
                        ImgInfo head = headList.get(i);
                        if(head.getUrl() != null) {
                            if(i == 0) {
                                loginUser.setHead(head.getUrl());
                            }
                            loginUser.getImgList().add(head);
                        }
                    }
                }

                //将用户的信息提交后台修改更新
                LogHelper.debug(MyDetailActivity.this, "待提交用户信息:"+JsonUtils.toJson(loginUser));
                LoadDataFromServer updateUser = new LoadDataFromServer(this, ServerAPI.SERVER_API_EDIT_BASE, loginUser);
                updateUser.getData(new LoadDataFromServer.DataCallBack() {
                    @Override
                    public void onDataCallBack(ResultObject ro) {

                        //更新本地缓存
                        SharePreferenceHolder.getInstance().saveUserInfo2Local(loginUser);

                        showAutoFinishAlertDialog("更新资料", "保存成功", 1000);
                    }
                });



                break;
            case R.id.ta_my_detail_rl_nick:

                //编辑昵称
                goTextEditor("修改昵称", loginUser.getNick(), "好的昵称容易得到更多关注哦", RequestCode.Request_Code_Text_Edit_NICK);

                break;
            case R.id.ta_my_detail_rl_birth:

                //编辑生日
                myDialog.showDefaultDateDialog(this);

                break;
            case R.id.ta_my_detail_rl_city:

                //编辑常住地城市
                Intent goIntent = new Intent(MyDetailActivity.this, CitySelectorActivity.class);
                startActivityForResult(goIntent, RequestCode.Request_Code_City_Select);

                break;
            case R.id.ta_my_detail_rl_sign:

                //编辑个性签名
                goTextEditor("修改签名", loginUser.getSign(), "一句话描述你个性特点", RequestCode.Request_Code_Text_Edit_SIGN);

                break;
            case R.id.ta_my_detail_rl_career:

                //编辑职业
                goTextEditor("修改职业", loginUser.getCareer(), "职业信息将让别人更了解你", RequestCode.Request_Code_Text_Edit_CAREER);

                break;
            case R.id.ta_my_detail_rl_tab:

                //编辑用户标签
                Intent tabIntent = new Intent(MyDetailActivity.this, UserTabSelectorActivity.class);
                startActivityForResult(tabIntent, RequestCode.Request_Code_Tab_Select);

                break;
            default:
                break;

        }
    }

    private void goTextEditor(String title, String value, String tips, int code) {
        Intent goIntent = new Intent(this, TextEditorActivity.class);
        goIntent.putExtra("title", title);
        goIntent.putExtra("content", value);
        goIntent.putExtra("tips", tips);
        goIntent.putExtra("code", code);

        startActivityForResult(goIntent, code);
    }

    /**
     * 图片处理的各种回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogHelper.debug(this, "onActivityResult : requestCode[" + requestCode + "],resultCode[" + resultCode + "], " +
                "intend data:" + data);

        switch (requestCode) {

            case PHOTO_REQUEST_TAKEPHOTO:
                if(FileUtils.isImageFileExists(currImgName)) {

                    File upFile = FileUtils.getImgFileByName(currImgName);
                    if(upFile != null) {
                        startPhotoZoom(Uri.fromFile(upFile));
                    }
                }
                break;

            case PHOTO_REQUEST_GALLERY:
                if (data != null) {
                    Uri uri = data.getData();
                    if(uri != null) {
                        startPhotoZoom(uri);
                    }
                }
                break;

            /**
             * 图片裁剪后回调
             */
            case PHOTO_REQUEST_CUT:

                if(data != null) {
                    // BitmapFactory.Options options = new BitmapFactory.Options();
                    //
                    // /**
                    // * 最关键在此，把options.inJustDecodeBounds = true;
                    // * 这里再decodeFile()，返回的bitmap为空
                    // * ，但此时调用options.outHeight时，已经包含了图片的高了
                    // */
                    // options.inJustDecodeBounds = true;
//                    LogHelper.debug(this, "curImgName is:"+curImgName);
//                    Bitmap bitmap = BitmapFactory.decodeFile(LoadUserImg.LOCAL_IMG_PATH
//                            + curImgName);
//                    if(bitmap != null) {
//                        LogHelper.debug(this, "bitmap is not null===============");
//                    }

                    File upFile = FileUtils.getImgFileByName(currImgName);
                    //直接上传
                    uploadImg(upFile);
                }

                break;

            case RequestCode.Request_Code_Text_Edit_NICK :
                String nick = data.getStringExtra("content");
                loginUser.setNick(nick);
                mNickTV.setText(nick);
                break;
            case RequestCode.Request_Code_Text_Edit_SIGN :
                String sign = data.getStringExtra("content");
                loginUser.setSign(sign);
                mSignTV.setText(sign);
                break;
            case RequestCode.Request_Code_Text_Edit_CAREER :
                String career = data.getStringExtra("content");
                loginUser.setCareer(career);
                mCareerTV.setText(career);
                break;
            case RequestCode.Request_Code_City_Select :
                if(data != null) {
                    String citySelect = data.getStringExtra("content");
                    loginUser.setLocation(citySelect);
                    mCityTV.setText(citySelect);
                    ToastUtils.showShortToast(MyDetailActivity.this, "当前城市选择:"+citySelect);
                }

                break;

            case RequestCode.Request_Code_Tab_Select :

                //从标签设置页回传的结果
                if(data != null) {
                    Bundle bundle = data.getExtras();

                    ArrayList tabList = bundle.getParcelableArrayList("tab_list");
                    if(tabList != null) {
                        LogHelper.debug(this, "onResult for Tab set:"+ JsonUtils.toJson(tabList));
                        loginUser.getTabList().clear();
                        loginUser.getTabList().addAll(tabList);

                        tabAdapter.refresh(loginUser.getTabList());
                    }
                }


                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 发起图片裁剪处理
     */
    private void startPhotoZoom(Uri uri) {
        //裁剪的大小固定为300*300吧
        int cutSize = 300;

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", cutSize);
        intent.putExtra("outputY", cutSize);
        intent.putExtra("return-data", false);
        //指定图片裁剪后的存储路径
        Uri saveUri = Uri.fromFile(new File(FileUtils.genImageFilePath(currImgName)));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, saveUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    /**
     * 上传本地图片
     */
    private void uploadImg(File imageFile) {
        //将本地最终处理完的图片上传七牛云存储
        qiniuCloudLoader.uploadImg(imageFile, currImgName, new QiniuCloudLoader.QiniuUploadedCallBack() {
            @Override
            public void onUploaded(String key) {
                LogHelper.debug(MyDetailActivity.this, "上传七牛成功回调=================");

                //重置头像列表然后重新显示,因为list是引用方式,所以直接改即可
                headList.get(headIndex).setUrl(currImgName);
                imgAdapter.refresh();//通知刷新界面
                ToastUtils.showShortToast(MyDetailActivity.this, "上传成功");
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        Calendar calendar = Calendar.getInstance();

        calendar.set(year, monthOfYear, dayOfMonth);

        Date birth = calendar.getTime();

        //修改用户生日
        loginUser.setBirth(birth);

        //展示生日
        mBirthTV.setText(DateUtils.getDayStrFromDate(birth));
    }

    public void setHeadIndex(int headIndex) {
        this.headIndex = headIndex;
    }

    public void setCurrImgName(String currImgName) {
        this.currImgName = currImgName;
    }


}
