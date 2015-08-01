/**
 * 深圳市塔塔互动
 * 作者:张伟锐
 * 2015年5月17日
 */
package com.mmtata.im.server.bean;

import java.util.Date;

/**
 * 用户注册信息
 */
public class UserRegister {
	/**
	 * 用户平台唯一id
	 */
	private long user_id;
	/**
	 * 用户微信账号的openid
	 */
	private String wx_open_id;
	
	/**
	 * 用户注册手机号,手机登录用户
	 */
	private String mobile;
	
	/**
	 * 用户密码,以密文存在
	 */
	private String password;
	
	private Date create_time;

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public String getWx_open_id() {
		return wx_open_id;
	}

	public void setWx_open_id(String wx_open_id) {
		this.wx_open_id = wx_open_id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	@Override
	public String toString() {
		return "UserRegister [user_id=" + user_id + ", wx_open_id="
				+ wx_open_id + ", mobile=" + mobile + ", password=" + password
				+ ", create_time=" + create_time + "]";
	}
	
	
}
