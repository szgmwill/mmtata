package com.tata.imta.page;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tata.imta.R;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.bean.ImgInfo;
import com.tata.imta.helper.img.LoadUserImg;
import com.tata.imta.view.photo.PhotoView;
import com.tata.imta.view.photo.PhotoViewAttacher;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Will Zhang on 2015/6/10.
 * 图片浏览活动页
 * 多图片查看器
 */
public class DisplayMultiImageActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private LayoutInflater inflater;

    /**
     * 展示的图片列表
     */
    private List<ImgInfo> imageList;

    private List<View> showList = new ArrayList<>();

    /**
     * viewpage
     */
    private ViewPager viewPager;

    /**
     * 当前图片浏览页码
     */
    private int currentIndex = 0;

    /**
     * 图片标题索引
     */
    private TextView mIndexTV;

    /**
     * 返回
     */
    private RelativeLayout mBackRL;

    /**
     * 图片加载器
     */
    private LoadUserImg imgloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ta_activity_display_image);

        inflater = getLayoutInflater();
        imgloader = new LoadUserImg();

        initViews();

        initEvents();
    }

    @Override
    protected void onDestroy() {
        if(imgloader != null) {
            imgloader = null;
        }

        super.onDestroy();
    }

    @Override
    protected void initViews() {

        viewPager = (ViewPager) findViewById(R.id.ta_vp_display_image);

        mIndexTV = (TextView) findViewById(R.id.ta_tv_topbar_title);

        mBackRL = (RelativeLayout) findViewById(R.id.ta_rl_topbar_back);

        //初始化图片展示列表
        initImageViewList(getIntent());

    }

    @Override
    protected void initEvents() {
        mBackRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 如果有新的intent过来的话
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            initImageViewList(intent);
        }
    }

    private void initImageViewList(Intent intent) {
        //从前面传过来的图片
        Bundle bundle = intent.getExtras();

        ArrayList intentList = bundle.getParcelableArrayList("head_list");
        if(intentList != null && intentList.size() > 0) {
            //有新的展示列表进来,重绘
            if(imageList != null) {
                imageList = null;
            }

            imageList = intentList;
        }

        if(imageList != null && imageList.size() > 0) {
            showList.clear();//清空上一次的
            for(ImgInfo imgInfo : imageList) {
                String imageUrl = imgInfo.getUrl();
                if(imageUrl != null) {
//                    Bitmap bitmap = FileUtil.getBigMapByImageName(imageUrl);

                    //生成视图
                    View showView = inflater.inflate(R.layout.ta_item_display_image, null);
                    PhotoView iv = (PhotoView) showView.findViewById(R.id.ta_display_photoview);
                    iv.setTag(imageUrl);

                    //单击退出事件
                    iv.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                        @Override
                        public void onPhotoTap(View view, float x, float y) {
                            finish();
                        }
                    });

                    //加载图片
                    imgloader.showImgView(iv, imageUrl);

                    showList.add(showView);

                }

            }

        }

        viewPager.setAdapter(new DisplayImageAdapter());
        viewPager.setOnPageChangeListener(this);

        //当前选中第几页
        int displayIndex = intent.getIntExtra("index", 0);
        onPageSelected(displayIndex);
        //定位页
        viewPager.setCurrentItem(displayIndex);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if(position >= 0 && position < showList.size()) {
            //改变一下展示的图片索引
            String currTV = String.valueOf(position + 1) + "/" + String.valueOf(showList.size());
            mIndexTV.setText(currTV);
            currentIndex = position;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    class DisplayImageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if(showList != null) {
                return showList.size();
            }
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        //删除元素
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView(showList.get(position));

//        super.destroyItem(container, position, object);
        }

        //初始化某个位置的view
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View currView = showList.get(position);
            ((ViewPager)container).addView(currView);

            return currView;
        }
    }
}
