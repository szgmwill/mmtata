package com.tata.imta.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tata.imta.R;
import com.tata.imta.app.BaseActivity;
import com.tata.imta.app.BizInfoHolder;
import com.tata.imta.constant.RequestCode;
import com.tata.imta.other.widget.OnWheelChangedListener;
import com.tata.imta.other.widget.WheelView;
import com.tata.imta.other.widget.adapter.ArrayWheelAdapter;

import java.util.Map;

/**
 * Created by Will Zhang on 2015/6/17.
 * 城市选择列表页面
 */
public class CitySelectorActivity extends BaseActivity implements View.OnClickListener, OnWheelChangedListener {

    private WheelView mViewProvince;
    private WheelView mViewCity;
    private WheelView mViewDistrict;
    private Button mBtnConfirm;
    private Button mBtnCancel;

    //数据
    /**
     * 所有省
     */
    private String[] mProvinceDatas;
    /**
     * key - 省 value - 市
     */
    private Map<String, String[]> mCitisDatasMap;
    /**
     * key - 市 values - 区
     */
    private Map<String, String[]> mDistrictDatasMap;

    /**
     * key - 区 values - 邮编
     */
    private Map<String, String> mZipcodeDatasMap;

    /**
     * 当前省的名称
     */
    private String mCurrentProviceName;
    /**
     * 当前市的名称
     */
    protected String mCurrentCityName;
    /**
     * 当前区的名称
     */
    private String mCurrentDistrictName;

    /**
     * 当前区的邮政编码
     */
    protected String mCurrentZipCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ta_activity_city_selector);

        loadData();

        setUpViews();
        setUpListener();
        setUpData();
    }

    private void loadData() {
        mProvinceDatas = BizInfoHolder.getInstance().getCityHolder().mProvinceDatas;
        mCitisDatasMap = BizInfoHolder.getInstance().getCityHolder().mCitisDatasMap;
        mDistrictDatasMap = BizInfoHolder.getInstance().getCityHolder().mDistrictDatasMap;
        mZipcodeDatasMap = BizInfoHolder.getInstance().getCityHolder().mZipcodeDatasMap;
        mCurrentProviceName = BizInfoHolder.getInstance().getCityHolder().mCurrentProviceName;
        mCurrentCityName = BizInfoHolder.getInstance().getCityHolder().mCurrentCityName;
        mCurrentDistrictName = BizInfoHolder.getInstance().getCityHolder().mCurrentDistrictName;
        mCurrentZipCode = BizInfoHolder.getInstance().getCityHolder().mCurrentZipCode;
    }

    private void setUpViews() {
        mViewProvince = (WheelView) findViewById(R.id.id_province);
        mViewCity = (WheelView) findViewById(R.id.id_city);
//        mViewDistrict = (WheelView) findViewById(R.id.id_district);
        mBtnConfirm = (Button) findViewById(R.id.btn_confirm);
        mBtnCancel = (Button) findViewById(R.id.btn_cancel);
    }

    private void setUpListener() {
        // 添加change事件
        mViewProvince.addChangingListener(this);
        // 添加change事件
        mViewCity.addChangingListener(this);
        // 添加change事件
//        mViewDistrict.addChangingListener(this);
        // 添加onclick事件
        mBtnConfirm.setOnClickListener(this);

        mBtnCancel.setOnClickListener(this);
    }

    private void setUpData() {
        //初始化后
        mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(this, mProvinceDatas));
        // 设置可见条目数量
        mViewProvince.setVisibleItems(7);
        mViewCity.setVisibleItems(7);
//        mViewDistrict.setVisibleItems(7);
        updateCities();
        updateAreas();
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        // TODO Auto-generated method stub
        if (wheel == mViewProvince) {
            updateCities();
        } else if (wheel == mViewCity) {
            updateAreas();
        } else if (wheel == mViewDistrict) {
            mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
            mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
        }
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {
        int pCurrent = mViewCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);

        if (areas == null) {
            areas = new String[] { "" };
        }
//        mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
//        mViewDistrict.setCurrentItem(0);
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = mViewProvince.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[] { "" };
        }
        mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
        mViewCity.setCurrentItem(0);
        updateAreas();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                Intent toIntent = new Intent();
                toIntent.putExtra("content", mCurrentProviceName+" "+mCurrentCityName);
                setResult(RequestCode.Request_Code_City_Select, toIntent);
                finish();
                break;
            case R.id.btn_cancel:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initEvents() {

    }

}
