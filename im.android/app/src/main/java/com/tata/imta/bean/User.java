package com.tata.imta.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.tata.imta.bean.status.Sex;
import com.tata.imta.bean.status.TabInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by will on 2015/4/30.
 *
 * 用户信息封装类
 *
 */
public class User implements Serializable {
    //自己平台生成的唯一id
    @JSONField(name = "user_id")
    private long userId;

    //用户微信账号的openid
    @JSONField(name = "openid")
    private String wxOpenId;

    //用户注册手机号或绑定手机号
    private String mobile;

    //密码,加密后的密文
    @JSONField(name = "password")
    private String pwd;

    //用户昵称
    private String nick;

    private String head;

    //性别：男boy、女girl
    private Sex sex;

    //出生日期
    @JSONField(format = "yyyy-MM-dd")
    private Date birth;

    //职业
    private String career = "陪聊专员";

    //所在城市
    private String location = "深圳";

    //个性签名
    private String sign = "陪你聊天哦";

    //用户信息入库时间
    @JSONField(serialize = false)
    private String dbTimeStamp;

    //用户标签
    @JSONField(name = "tab_list")
    private List<TabInfo> tabList = new ArrayList<>();

    //用户相册
    @JSONField(name = "img_list")
    private List<ImgInfo> imgList = new ArrayList<>();

    //用户扩展信息
    @JSONField(name = "extend")
    private UserExtend extend;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getWxOpenId() {
        return wxOpenId;
    }

    public void setWxOpenId(String wxOpenId) {
        this.wxOpenId = wxOpenId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<TabInfo> getTabList() {
        return tabList;
    }

    public void setTabList(List<TabInfo> tabList) {
        this.tabList = tabList;
    }

    public String getDbTimeStamp() {
        return dbTimeStamp;
    }

    public void setDbTimeStamp(String dbTimeStamp) {
        this.dbTimeStamp = dbTimeStamp;
    }

    public List<ImgInfo> getImgList() {
        return imgList;
    }

    public void setImgList(List<ImgInfo> imgList) {
        this.imgList = imgList;
    }

    public UserExtend getExtend() {
        return extend;
    }

    public void setExtend(UserExtend extend) {
        this.extend = extend;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", wxOpenId='" + wxOpenId + '\'' +
                ", mobile='" + mobile + '\'' +
                ", pwd='" + pwd + '\'' +
                ", nick='" + nick + '\'' +
                ", head='" + head + '\'' +
                ", sex=" + sex +
                ", birth=" + birth +
                ", career='" + career + '\'' +
                ", location='" + location + '\'' +
                ", sign='" + sign + '\'' +
                ", dbTimeStamp='" + dbTimeStamp + '\'' +
                ", tabList=" + tabList +
                ", imgList=" + imgList +
                ", extend=" + extend +
                '}';
    }
}
