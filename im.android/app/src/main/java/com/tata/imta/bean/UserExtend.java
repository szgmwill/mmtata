package com.tata.imta.bean;

import com.tata.imta.bean.status.FeeUnit;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Will Zhang on 2015/6/8.
 * 用户扩展信息
 */
public class UserExtend implements Serializable {

    private long user_id;

    //聊天服务费价格
    private BigDecimal fee;

    //服务费计算标准
    private FeeUnit fee_unit;

    //服务费单位计费时长
    private int fee_duration;

    //是否允许未付费用户搭讪聊天
    private int allow_free_chat;

    //用户绑定手机号,也是注册时手机号
    private String bind_mobile;

    //用户绑定微信支付账号
    private String wx_acct;

    //用户绑定支付定账号
    private String zfb_acct;

    //支付宝昵称
    private String zfb_nick;

    /**
     * 用户评价绩点数
     * 用户评价绩点数，是别人对该用户评价的平均值
     */
    //用户评价绩点数，是别人对该用户评价的平均值
    private Float feedback;

    /***
     * 翻译后的聊天资费单位
     */
    private String show_unit;

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public FeeUnit getFee_unit() {
        return fee_unit;
    }

    public void setFee_unit(FeeUnit fee_unit) {
        this.fee_unit = fee_unit;
    }

    public int getFee_duration() {
        return fee_duration;
    }

    public void setFee_duration(int fee_duration) {
        this.fee_duration = fee_duration;
    }

    public int getAllow_free_chat() {
        return allow_free_chat;
    }

    public void setAllow_free_chat(int allow_free_chat) {
        this.allow_free_chat = allow_free_chat;
    }

    public String getBind_mobile() {
        return bind_mobile;
    }

    public void setBind_mobile(String bind_mobile) {
        this.bind_mobile = bind_mobile;
    }

    public String getWx_acct() {
        return wx_acct;
    }

    public void setWx_acct(String wx_acct) {
        this.wx_acct = wx_acct;
    }

    public String getZfb_acct() {
        return zfb_acct;
    }

    public void setZfb_acct(String zfb_acct) {
        this.zfb_acct = zfb_acct;
    }

    public String getZfb_nick() {
        return zfb_nick;
    }

    public void setZfb_nick(String zfb_nick) {
        this.zfb_nick = zfb_nick;
    }

    public Float getFeedback() {
        return feedback;
    }

    public void setFeedback(Float feedback) {
        this.feedback = feedback;
    }

    public String getShow_unit() {
        return show_unit;
    }

    public void setShow_unit(String show_unit) {
        this.show_unit = show_unit;
    }

}
