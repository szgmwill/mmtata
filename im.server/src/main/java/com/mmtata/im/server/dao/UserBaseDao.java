package com.mmtata.im.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mmtata.im.server.bean.TabInfo;
import com.mmtata.im.server.bean.UserBase;
import com.mmtata.im.server.bean.UserExtend;
import com.mmtata.im.server.bean.UserFind;
import com.mmtata.im.server.bean.UserImg;
import com.mmtata.im.server.bean.UserLogin;
import com.mmtata.im.server.bean.UserRegister;
import com.mmtata.im.server.status.LoginStatus;
import com.mmtata.im.server.status.Sex;

/**
 * 
 * @author 张伟锐
 * 用户基础信息DB操作
 *
 */
public interface UserBaseDao {
	public void registerUser(UserRegister register);
	
	public UserRegister getRegisterUser(@Param("openid") String openid, @Param("mobile") String mobile);
	
	public UserRegister getRegisterUserByUserId(@Param("user_id") Long userId);
	
	public void addNewUser(UserBase user);
	
	/**
	 * 修改用户基本信息
	 */
	public int updateUser(UserBase user);
	
	public void addLoginLog(UserLogin login);
	
	public UserLogin queryLatestLoginByUserId(@Param("user_id") long user_id, @Param("status") LoginStatus status);
	
	public void addUserTab(@Param("user_id") long user_id, @Param("tab") TabInfo tab);
	
	public int updateUserTab(@Param("user_id") long user_id, @Param("tab") TabInfo tab);
	
	//物理删除用户标签
	public int deleteUserTab(@Param("user_id") long user_id);
	
	public List<TabInfo> queryUserTab(long userId);
	
	public void addUserImg(UserImg img);
	
	public int updateUserImg(UserImg img);
	
	//物理删除用户头像列表
	public int deleteUserImg(@Param("user_id") long user_id);
	
	public List<UserImg> queryUserImg(long userId);
	
	/**
	 * 查询用户基础信息
	 * @param userId
	 * @return
	 */
	public UserBase queryUserBase(long userId);
	
	/**
	 * 查询在线用户列表(发现模块)
	 */
	public List<UserFind> findOnlineUserList(@Param("sex") Sex sex, @Param("offset") int offset, @Param("limit") int limit);
	
	
	/**
	 * 新增用户扩展信息
	 */
	public void addNewUserExtend(UserExtend extend);
	
	/**
	 * 更新扩展信息
	 */
	public int updateUserExtend(UserExtend extend);
	
	/**
	 * 查询扩展信息
	 */
	public UserExtend queryUserExtendByUserId(@Param("user_id") long userId);
}
