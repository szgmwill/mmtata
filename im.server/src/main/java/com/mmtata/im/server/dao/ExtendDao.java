package com.mmtata.im.server.dao;

import java.util.List;

import com.mmtata.im.server.bean.UserReport;

/**
 * 用户扩展操作DAO
 * @author 张伟锐
 *
 */
public interface ExtendDao {
	
	public void addNewUserReport(UserReport add);
	
	public List<UserReport> queryUserReport(UserReport query);
	
}
