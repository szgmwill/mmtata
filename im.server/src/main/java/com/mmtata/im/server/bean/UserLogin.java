/**
 * 深圳市塔塔互动
 * 作者:张伟锐
 * 2015年5月16日
 */
package com.mmtata.im.server.bean;

import java.util.Date;

import com.mmtata.im.server.status.LoginStatus;

/**
 * 登录信息
 */
public class UserLogin {
	private long pid;
	
	private long user_id;
	
	/**
	 * 1-登录，2-登出
	 */
	private LoginStatus status;
	
	/**
	 * 创建时间,即最近一次登录时间
	 */
	private Date create_time;

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

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public LoginStatus getStatus() {
		return status;
	}

	public void setStatus(LoginStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "UserLogin [pid=" + pid + ", user_id=" + user_id + ", status="
				+ status + ", create_time=" + create_time + "]";
	}

}
