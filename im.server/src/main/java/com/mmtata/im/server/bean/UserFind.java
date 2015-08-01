package com.mmtata.im.server.bean;

import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.mmtata.im.server.status.FeeUnit;
import com.mmtata.im.server.status.Sex;

/**
 * 在线用户基本信息
 * @author 张伟锐
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
	
//	@JSONField(serialize = false)
	private BigDecimal fee;
	
	@JSONField(serialize = false)
	private String feeUnit;
	
	@JSONField(serialize = false)
	private int feeDuration;
	
	/**
	 * 最近一次登录时间
	 */
	@JSONField(name = "last_login_time")
	private Date lastLoginTime;
	
	@JSONField(name = "fee_price")
	private String price;
	
	@JSONField(name = "show_unit")
	private String showUnit;
	
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

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public String getFeeUnit() {
		return feeUnit;
	}

	public void setFeeUnit(String feeUnit) {
		this.feeUnit = feeUnit;
	}

	public int getFeeDuration() {
		return feeDuration;
	}

	public void setFeeDuration(int feeDuration) {
		this.feeDuration = feeDuration;
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
		if(fee != null && feeUnit != null && feeDuration > 0) {
			FeeUnit unit = FeeUnit.getUnit(feeUnit);
			if(unit != null) {
				if(feeDuration > 1) {
					return ""+fee.intValue()+"/"+feeDuration+unit.getDesc();
				} else {
					return ""+fee.intValue()+"/"+unit.getDesc();
				}
			}
		}
		
		return "免费";
	}

	public void setPrice(String price) {
		this.price = price;
	}
	
	public String getShowUnit() {
		if(fee != null && feeUnit != null && feeDuration > 0) {
			FeeUnit unit = FeeUnit.getUnit(feeUnit);
			if(unit != null) {
				if(feeDuration > 1) {
					return "/"+feeDuration+unit.getDesc();
				} else {
					return "/"+unit.getDesc();
				}
			}
		}
		return "";
	}

	public void setShowUnit(String showUnit) {
		this.showUnit = showUnit;
	}

	@Override
	public String toString() {
		return "UserFind [userId=" + userId + ", nick=" + nick + ", head="
				+ head + ", sex=" + sex + ", birth=" + birth + ", sign=" + sign
				+ ", fee=" + fee + ", feeUnit=" + feeUnit + ", feeDuration="
				+ feeDuration + ", lastLoginTime=" + lastLoginTime + ", price="
				+ price + ", showUnit=" + showUnit + "]";
	}

	
	
}
