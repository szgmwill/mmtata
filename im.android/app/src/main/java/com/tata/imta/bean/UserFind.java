package com.tata.imta.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.tata.imta.bean.status.Sex;

import java.math.BigDecimal;
import java.util.Date;


/**
 * 在线用户基本信息
 *
 */
public class UserFind {
	/**
	 * 用户id
	 */
	@JSONField(name = "user_id")
	private long userId;
	
	private String nick;
	
	private String head;
	
	private Sex sex;
	
	@JSONField(format = "yyyy-MM-dd")
	private Date birth;
	
	private String sign;

	private BigDecimal fee;

	@JSONField(name = "show_unit")
	private String showUnit;

	/**
	 * 最近一次登录时间
	 */
	@JSONField(name = "last_login_time")
	private Date lastLoginTime;
	
	@JSONField(name = "fee_price")
	private String price;
	
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
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

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public Date getBirth() {
		return birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	
	/**
	 * 获取聊天服务资费信息
	 * e.g: 20元/天
	 */
	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public String getShowUnit() {
		return showUnit;
	}

	public void setShowUnit(String showUnit) {
		this.showUnit = showUnit;
	}

	@Override
	public String toString() {
		return "UserFind{" +
				"userId=" + userId +
				", nick='" + nick + '\'' +
				", head='" + head + '\'' +
				", sex=" + sex +
				", birth=" + birth +
				", sign='" + sign + '\'' +
				", fee=" + fee +
				", showUnit='" + showUnit + '\'' +
				", lastLoginTime=" + lastLoginTime +
				", price='" + price + '\'' +
				'}';
	}
}
