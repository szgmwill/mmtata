/**
 * 深圳市塔塔互动
 * 作者:张伟锐
 * 2015年5月16日
 */
package com.mmtata.im.server.bean;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.mmtata.im.server.status.Sex;

/**
 * 用户基础信息
 */
public class UserBase {
	/**
	 * 用户ID
	 */
	private long user_id;
	
	/**
	 * 用户昵称
	 */
	private String nick;
	
	/**
	 * 用户头像url
	 */
	private String head;
	
	/**
	 * 性别：男BOY、女GIRL
	 */
	private Sex sex;
	
	/**
	 * 出生日期
	 */
	@JSONField(format = "yyyy-MM-dd")
	private Date birth;
	
	/**
	 * 职业
	 */
	private String career;
	
	/**
	 * 所在城市
	 */
	private String location;
	
	/**
	 * 个性签名 
	 */
	private String sign;
	
	/**
	 * 创建时间,即用户的注册时间
	 */
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date create_time;
	
	/**
	 * 用户最近的一次登录时间
	 */
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date last_login_time;
	
	/**
	 * 是否删除 
	 * @return
	 */
	@JSONField(serialize = false)
	private int del_flag = -1;
	
	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
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

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public Date getLast_login_time() {
		return last_login_time;
	}

	public void setLast_login_time(Date last_login_time) {
		this.last_login_time = last_login_time;
	}
	
	public int getDel_flag() {
		return del_flag;
	}

	public void setDel_flag(int del_flag) {
		this.del_flag = del_flag;
	}

	@Override
	public String toString() {
		return "UserBase [user_id=" + user_id + ", nick=" + nick + ", head="
				+ head + ", sex=" + sex + ", birth=" + birth + ", career="
				+ career + ", location=" + location + ", sign=" + sign
				+ ", create_time=" + create_time + ", last_login_time="
				+ last_login_time + "]";
	}

	
}
