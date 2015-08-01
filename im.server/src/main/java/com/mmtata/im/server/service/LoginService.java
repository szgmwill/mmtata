/**
 * 深圳市塔塔互动
 * 作者:张伟锐
 * 2015年5月17日
 */
package com.mmtata.im.server.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mmtata.im.server.bean.UserLogin;
import com.mmtata.im.server.bean.UserRegister;
import com.mmtata.im.server.common.ResultObject;
import com.mmtata.im.server.constants.ErrorCode;
import com.mmtata.im.server.dao.UserBaseDao;
import com.mmtata.im.server.status.LoginStatus;
import com.mmtata.im.server.util.CheckHelper;
import com.mmtata.im.server.util.JsonHelper;
/**
 * 登录相关操作
 */
@Service
public class LoginService {
	
	private static final Logger logger = Logger.getLogger(LoginService.class);
	
	@Autowired
	private UserBaseDao userDao;
	
	/**
	 * 保存用户的身份认证token
	 */
	private Map<Long, String> tokenMap = new HashMap<Long, String>();
	
	/**
	 * 登录登出
	 * @param userId
	 * @param status
	 */
	public void login(long userId, LoginStatus status) {
		
		if(userId > 0 && status != null) {
			if(userDao.queryUserBase(userId) != null) {
				//添加一条新的登录记录
				UserLogin login = new UserLogin();
				login.setUser_id(userId);
				login.setStatus(status);
				userDao.addLoginLog(login);
			}
		}
	}
	
	/**
	 * 查询用户最近一次登录登出信息
	 */
	public UserLogin queryLastestLogin(long userId, LoginStatus status) {
		
		return userDao.queryLatestLoginByUserId(userId, status);
	}
	
	/**
	 * 根据用户id查询用户的openid作为token
	 */
	public String queryTokenByUserId(Long userId) {
		String token = "";
		if(userId != null && userId > 0) {
			if(tokenMap.containsKey(userId)) {
				token = tokenMap.get(userId);
			} else {
				//查库
				UserRegister reg = userDao.getRegisterUserByUserId(userId);
				if(reg != null && reg.getWx_open_id() != null) {
					token = reg.getWx_open_id();
					logger.debug("queryTokenByUserId,userId["+userId+"],token["+token+"]");
					//加入缓存
					tokenMap.put(userId, token);//多线程的话直接覆盖也可以
				}
			}
		}
		
		return token;
	}
	
	/**
	 * 手机用户登录
	 */
	@Transactional
	public void mobileLogin(String mobile, String pwd, ResultObject ro) throws Exception {
		Map<String, Object> retMap = new HashMap<String, Object>();
		//先查询是否有这个手机的信息
		UserRegister register = userDao.getRegisterUser(null, mobile);
		if(register == null) {
			ro.setCode(ErrorCode.FAIL_RET_EMPTY);
			ro.setMsg("该用户未注册");
			return;
		} else {
			logger.debug("mobileLogin ==> 手机号["+mobile+"],用户["+JsonHelper.toJson(register)+"]");
			
			//校验一下密码
			String dbPwd = register.getPassword();
			logger.debug("dbpwd["+dbPwd+"]==>pwd["+pwd+"]");
			if(!CheckHelper.isValidUserPwd(mobile, register.getPassword(), pwd)) {
				ro.setCode(ErrorCode.FAIL_PARAM);
				ro.setMsg("密码错误");
				return;
			}
		}
		
		//登记登录
		login(register.getUser_id(), LoginStatus.IN);
		
		retMap.put("user_id", register.getUser_id());
		ro.setData(retMap);
		
		return;
	}
}
