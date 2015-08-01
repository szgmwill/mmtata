package com.mmtata.im.server.bean;

import java.util.Date;

/**
 * 用户举报
 * @author 张伟锐
 *
 */
public class UserReport {
	private long pid;
	
	private long userId;
	
	private long targetId;
	
	private int type;
	
	private int status;
	
	private String remark;
	
	private Date createTime;
	
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getTargetId() {
		return targetId;
	}

	public void setTargetId(long targetId) {
		this.targetId = targetId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public long getPid() {
		return pid;
	}

	public void setPid(long pid) {
		this.pid = pid;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "UserReport [pid=" + pid + ", userId=" + userId + ", targetId="
				+ targetId + ", type=" + type + ", status=" + status
				+ ", remark=" + remark + ", createTime=" + createTime + "]";
	}
	
	
}
