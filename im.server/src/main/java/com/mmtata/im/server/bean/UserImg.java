/**
 * 深圳市塔塔互动
 * 作者:张伟锐
 * 2015年5月17日
 */
package com.mmtata.im.server.bean;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 用户相册
 */
public class UserImg {
	@JSONField(serialize = false)
	private long pid;
	
	@JSONField(serialize = false)
	private long user_id;
	
	/**
	 * 相片索引，默认第0张即是头像
	 */
	private int index;
	
	/**
	 * 相片上传后的url地址
	 */
	private String url;
	
	@JSONField(serialize = false)
	private Date create_time;
	
	@JSONField(serialize = false)
	private Date last_update_time;
	
	/**
	 * 是否删除：0-否；1-是
	 */
	@JSONField(serialize = false)
	private int del_flag = -1;

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

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public int getDel_flag() {
		return del_flag;
	}

	public void setDel_flag(int del_flag) {
		this.del_flag = del_flag;
	}

	@Override
	public String toString() {
		return "UserImg [pid=" + pid + ", user_id=" + user_id + ", index="
				+ index + ", url=" + url + ", create_time=" + create_time
				+ ", last_update_time=" + last_update_time + ", del_flag="
				+ del_flag + "]";
	}
	
	
}
