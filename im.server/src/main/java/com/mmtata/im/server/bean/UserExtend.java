package com.mmtata.im.server.bean;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.mmtata.im.server.status.FeeUnit;

/**
 * 用户扩展信息
 * @author 张伟锐
 *
 */
public class UserExtend {
	@JSONField(serialize = false)
	private long pid;
	
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
	
	//用户评价绩点数，是别人对该用户评价的平均值
	private Float feedback;
	
	//用户已经服务完成的订单总数
	private int order_num;
	
	//创建时间
	@JSONField(serialize = false)
	private Date create_time;
	
	
	//最后修改时间
	@JSONField(serialize = false)
	private Date last_update_time;
	
	/***
	 * 翻译后的聊天资费单位
	 */
	private String show_unit;
	
	public long getPid() {
		return pid;
	}


	public void setPid(long pid) {
		this.pid = pid;
	}


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


	public Date getCreate_time() {
		return create_time;
	}


	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}


	public Date getLast_update_time() {
		return last_update_time;
	}


	public void setLast_update_time(Date last_update_time) {
		this.last_update_time = last_update_time;
	}


	public String getShow_unit() {
		if(fee != null && fee_unit != null && fee_duration > 0) {
			if(fee_duration > 1) {
				return "/"+fee_duration+fee_unit.getDesc();
			} else {
				return "/"+fee_unit.getDesc();
			}
		}
		return "";
	}


	public void setShow_unit(String show_unit) {
		this.show_unit = show_unit;
	}

	public int getOrder_num() {
		return order_num;
	}

	public void setOrder_num(int order_num) {
		this.order_num = order_num;
	}


	@Override
	public String toString() {
		return "UserExtend [pid=" + pid + ", user_id=" + user_id + ", fee="
				+ fee + ", fee_unit=" + fee_unit + ", fee_duration="
				+ fee_duration + ", allow_free_chat=" + allow_free_chat
				+ ", bind_mobile=" + bind_mobile + ", wx_acct=" + wx_acct
				+ ", zfb_acct=" + zfb_acct + ", zfb_nick=" + zfb_nick
				+ ", feedback=" + feedback + ", create_time=" + create_time
				+ ", last_update_time=" + last_update_time + "]";
	}
	
	
}
