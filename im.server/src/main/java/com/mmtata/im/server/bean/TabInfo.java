/**
 * 深圳市塔塔互动
 * 作者:张伟锐
 * 2015年5月17日
 */
package com.mmtata.im.server.bean;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 用户标签信息
 */
public class TabInfo {
	
	@JSONField(serialize = false)//json序列化时不处理该字段
	private long tab_id;
	/**
	 * 标签名称:“做你女朋友”,”叫你起床”
	 */
	private String tab_name;
	/**
	 * 标签类别：1-用户类型；2-语言；3-能力；
	 */
	private int tab_type;
	
	/**
	 * 是否标记删除 0否 1是
	 */
	@JSONField(serialize = false)
	private int del_flag = -1;
	
	@JSONField (format = "yyyy-MM-dd HH:mm:ss")//json输出时格式转换
	private Date create_time;

	public long getTab_id() {
		return tab_id;
	}

	public void setTab_id(long tab_id) {
		this.tab_id = tab_id;
	}

	public String getTab_name() {
		return tab_name;
	}

	public void setTab_name(String tab_name) {
		this.tab_name = tab_name;
	}

	public int getTab_type() {
		return tab_type;
	}

	public void setTab_type(int tab_type) {
		this.tab_type = tab_type;
	}

	public int getDel_flag() {
		return del_flag;
	}

	public void setDel_flag(int del_flag) {
		this.del_flag = del_flag;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	@Override
	public String toString() {
		return "TabInfo [tab_id=" + tab_id + ", tab_name=" + tab_name
				+ ", tab_type=" + tab_type + ", del_flag=" + del_flag
				+ ", create_time=" + create_time + "]";
	}
	
	
}
