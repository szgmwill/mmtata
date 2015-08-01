/**
 * 深圳市塔塔互动
 * 作者:张伟锐
 * 2015年5月17日
 */
package com.mmtata.im.server.service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mmtata.im.server.bean.AcctInfo;
import com.mmtata.im.server.bean.TabInfo;
import com.mmtata.im.server.bean.UserBase;
import com.mmtata.im.server.bean.UserExtend;
import com.mmtata.im.server.bean.UserFind;
import com.mmtata.im.server.bean.UserImg;
import com.mmtata.im.server.bean.UserLogin;
import com.mmtata.im.server.bean.UserRegister;
import com.mmtata.im.server.common.CacheObject;
import com.mmtata.im.server.common.ResultObject;
import com.mmtata.im.server.constants.ErrorCode;
import com.mmtata.im.server.dao.TradeDao;
import com.mmtata.im.server.dao.UserBaseDao;
import com.mmtata.im.server.status.AcctStatus;
import com.mmtata.im.server.status.LoginStatus;
import com.mmtata.im.server.status.Sex;
import com.mmtata.im.server.util.CheckHelper;
import com.mmtata.im.server.util.DateUtils;
import com.mmtata.im.server.util.JsonHelper;

/**
 * 用户基础模块业务处理
 * 
 */
@Service
public class UserBaseService {
	
	private static final Logger logger = Logger.getLogger(UserBaseService.class);
	
	@Autowired
	private UserBaseDao userDao;
	
	@Autowired
	private TradeDao tradeDao;
	
	@Autowired
	private LoginService loginService;
	
	/**
	 * 缓存在线用户列表,暂时使用简单单机内存,有条件时考虑memcache,redis等方案
	 */
	private static Map<String, CacheObject> findUserListCache = new ConcurrentHashMap<String, CacheObject>();
	
	/**
	 * 新用户注册
	 * 包括微信用户和手机用户
	 */
	@Transactional
	public void registerUser(JSONObject reqJson, ResultObject ro) throws Exception {
		logger.debug("注册新用户业务处理:"+reqJson.toString());
		
		long userId = 0;
		Map<String, String> retMap = new HashMap<String,String>();
		
		//1.先判断该用户是否已经注册过了,注册过的话,直接返回原注册用户id
		String openid = reqJson.getString("openid");
		String mobile = reqJson.getString("mobile");
		
		//注册新用户
		UserRegister register = userDao.getRegisterUser(openid, mobile);
		if(register != null) {//已经注册过了
			userId = register.getUser_id();
			logger.info("用户["+openid+"]["+mobile+"]已经注册过了["+userId+"]");
			
			if(StringUtils.isNotBlank(openid)) {
				//微信注册反馈,直接返回注册id
				retMap.put("user_id", String.valueOf(userId));
				ro.setData(retMap);
				//同时登记登录日志
				loginService.login(userId, LoginStatus.IN);
			} else if(StringUtils.isNotBlank(mobile)) {
				//手机用户,提示已经注册过了
				ro.setCode(ErrorCode.FAIL_BIZ_ERROR);
				ro.setMsg("手机号["+mobile+"]已经注册过了,请直接登录");
			}
			
			return;
		}
		
		//2.注册表新增
		UserRegister newRegister = new UserRegister();
		newRegister.setWx_open_id(openid);
		newRegister.setMobile(mobile);
		String pwd = reqJson.getString("password");//是一个md5之后的密文
		if(pwd != null) {
			//密码必须做特殊加密后才能保存
			pwd = CheckHelper.genSavePassword(mobile, pwd);
		}
		newRegister.setPassword(pwd);
		userDao.registerUser(newRegister);
		userId = newRegister.getUser_id();
		logger.info("注册新用户成功:"+userId);
		
		//3.新增用户基础信息
		UserBase newUser = null;
		//先将请求信息读取
		
		newUser = JsonHelper.jsonStr2Obj(reqJson.toJSONString(), UserBase.class);
		newUser.setUser_id(userId);
		newUser.setSex(Sex.getSex(reqJson.getString("sex")));
		newUser.setBirth(DateUtils.getDateFromDayStr(reqJson.getString("birth")));
		
		String head = reqJson.getString("head");
		if(StringUtils.isBlank(head)) {//没有头像,用默认显示
			head = "http://7xjkrr.com1.z0.glb.clouddn.com/ta_avatar_default.png";
		}
		newUser.setHead(head);
		
		//如果有一些个人信息没有初始化的,先给一下默认值
		if(StringUtils.isBlank(newUser.getNick()) && StringUtils.isNotBlank(mobile)) {
			newUser.setNick(mobile);//手机用户由于没有昵称,直接用手机号
		}
		if(newUser.getSex() == null) {
			newUser.setSex(Sex.BOY);//默认是男孩
		}
		if(newUser.getBirth() == null) {
			newUser.setBirth(DateUtils.getDateFromDayStr("1995-07-01"));//这里暂时先给个出生日期
		}
		
		userDao.addNewUser(newUser);
		logger.info("添加新用户信息成功:"+userId);
		
		//4.新增用户标签
		JSONArray tabArray = reqJson.getJSONArray("tab_list");
		if(tabArray != null && tabArray.size() > 0) {
			
			for(Object tabObj : tabArray) {
				JSONObject tabJson = (JSONObject) tabObj;
				TabInfo newTab = new TabInfo();
				newTab.setTab_type(tabJson.getIntValue("tab_type"));
				newTab.setTab_name(tabJson.getString("tab_name"));
				
				userDao.addUserTab(userId, newTab);
			}
			logger.info("添加新用户标签成功:"+tabArray.size());
		}
		
		//5.新增用户相册 (注册时只有头像)
		UserImg img = new UserImg();
		img.setUrl(head);
		img.setUser_id(userId);
		userDao.addUserImg(img);
		
		logger.info("添加新用户头像成功");
		
		//同时开通账户信息
		AcctInfo myAcct = new AcctInfo();
		myAcct.setUser_id(newUser.getUser_id());
		myAcct.setBalance(new BigDecimal(0));
		myAcct.setPay_pw("");//账户二次密码
		myAcct.setWithdraw_amt(new BigDecimal(0));
		myAcct.setStatus(AcctStatus.NORMAL);
		//新增账户信息
		tradeDao.addNewAcctInfo(myAcct);
		logger.debug("新增账户信息成功:"+myAcct.toString());
		
		retMap.put("user_id", String.valueOf(userId));
		ro.setData(retMap);
		return;
	}
	
	/**
	 * 查询用户详细信息
	 */
	public void queryUserDetail(long userId, ResultObject ro) {
		logger.debug("查询用户["+userId+"]详情");
		
		//总返回对象
		Map<String, Object> userMap = new HashMap<String, Object>();
		
		//查询用户的注册信息
		
		//先查询用户基础信息
		UserBase base = userDao.queryUserBase(userId);
		if(base == null) {
			ro.setCode(ErrorCode.FAIL_RET_EMPTY);
			ro.setMsg("查询用户["+userId+"]信息为空");
			return;
		}
		
		//查询一下该用户最近的登录信息
		UserLogin login = loginService.queryLastestLogin(userId, LoginStatus.IN);
		if(login != null) {
			base.setLast_login_time(login.getCreate_time());
		}
		
		userMap.put("base", base);
		
		//查询用户的标签信息
		List<TabInfo> tabList = userDao.queryUserTab(userId);
		if(tabList != null && tabList.size() > 0) {
			userMap.put("tab_list", tabList);
		} else {
			userMap.put("tab_list", Collections.EMPTY_LIST);
		}
		
		
		//查询用户的相册信息
		List<UserImg> imgList = userDao.queryUserImg(userId);
		if(imgList != null && imgList.size() > 0) {
			userMap.put("img_list", imgList);
			base.setHead(imgList.get(0).getUrl());//第一个作为头像
		} else {
			userMap.put("img_list", Collections.EMPTY_LIST);
		}
		
		//查询用户的扩展信息
		UserExtend extend = userDao.queryUserExtendByUserId(userId);
		if(extend != null) {
			userMap.put("extend", extend);
		} 
//		else {
//			userMap.put("extend", "");
//		}
		
		
		logger.debug("查询用户详情返回:"+JsonHelper.toJson(userMap));
		
		//最终结果返回
		ro.setData(userMap);
		
		return;
	}
	
	/**
	 * 修改用户基础信息
	 */
	@Transactional
	public void updateUserBase(JSONObject reqJson, ResultObject ro) throws Exception {
		logger.debug("修改用户基础信息:"+reqJson.toString());
		long userId = reqJson.getLongValue("user_id");
		
		//先修改基础信息
		String nick = reqJson.getString("nick");
		Sex sex = Sex.getSex(reqJson.getString("sex"));
		Date birth = reqJson.getDate("birth");
		String career = reqJson.getString("career");
		String location = reqJson.getString("location");
		String sign = reqJson.getString("sign");
		
		UserBase updateUser = new UserBase();
		updateUser.setUser_id(userId);
		updateUser.setNick(nick);
		updateUser.setSex(sex);
		updateUser.setBirth(birth);
		updateUser.setCareer(career);
		updateUser.setLocation(location);
		updateUser.setSign(sign);
		
		//相册,一旦有提交示为全量修改
		JSONArray imgArray = reqJson.getJSONArray("img_list");
		if(imgArray != null && imgArray.size() > 0) {
			logger.debug("修改用户["+userId+"]相册量:"+imgArray.size());
			//先直接删除掉老的,再重新入库(先简单粗暴做法)
			int delRow = userDao.deleteUserImg(userId);
			logger.debug("删除用户["+userId+"]旧相册量:"+delRow);
			
			//相册的第一张将作为头像
			for(int i = 0; i < imgArray.size(); i ++) {
				JSONObject imgJson = (JSONObject) imgArray.get(i);
				
				UserImg newImg = new UserImg();
				newImg.setUser_id(userId);
				newImg.setIndex(imgJson.getIntValue("index"));
				newImg.setUrl(imgJson.getString("url"));
				newImg.setDel_flag(0);
				
				//重新设置头像
				if(i == 0) {
					updateUser.setHead(newImg.getUrl());
				}
				
				userDao.addUserImg(newImg);
			}
		}
		
		logger.debug("待修改用户信息:"+JsonHelper.toJson(updateUser));
		userDao.updateUser(updateUser);
		
		//标签,一旦有提交示为全量修改
		JSONArray tabArray = reqJson.getJSONArray("tab_list");
		if(tabArray != null && tabArray.size() > 0) {
			logger.debug("修改用户["+userId+"]标签量:"+tabArray.size());
			//先直接删除掉老的,再重新入库(先简单粗暴做法)
			int delRow = userDao.deleteUserTab(userId);
			logger.debug("删除用户["+userId+"]旧标签量:"+delRow);
			for(Object obj : tabArray) {
				JSONObject tabJson = (JSONObject) obj;
				TabInfo newTab = new TabInfo();
				newTab.setTab_name(tabJson.getString("tab_name"));
				newTab.setTab_type(tabJson.getIntValue("tab_type"));
				newTab.setDel_flag(0);
				
				userDao.addUserTab(userId, newTab);
			}
		}
		
		
		return;
	}
	
	/**
	 * 查看用户列表
	 */
	public void queryUserList(JSONObject reqJson, ResultObject ro) {
		int pageIndex = reqJson.getIntValue("page_index");
		int pageSize = reqJson.getIntValue("page_size");
		int sex = reqJson.getIntValue("sex");
		Sex querySex = null;
		if(sex == 1) {
			querySex = Sex.BOY;
		} else if (sex == 2) {
			querySex = Sex.GIRL;
		}
		int offset = (pageIndex-1)*pageSize;
		
		List<UserFind> findList = null;
		
		//先从缓存里找
		String key = pageIndex+"-"+pageSize+"-"+sex;
		if(findUserListCache.containsKey(key)) {
			logger.debug("queryUserList, key["+key+"] go cache!");
			CacheObject cache = findUserListCache.get(key);
			if(cache.getValue() != null && !cache.isExpire()) {
				findList = (List<UserFind>) cache.getValue();
			}
		} 
		//查DB
		if(findList == null) {
			logger.debug("find list req param:"+querySex+","+offset+","+pageSize);
			findList = userDao.findOnlineUserList(querySex, offset, pageSize);
			if(findList != null && findList.size() > 0) {
				//加入缓存
				CacheObject newCache = new CacheObject();
				newCache.setValue(findList);
				newCache.setCreateTime(System.currentTimeMillis());
				newCache.setCacheTime(10*60*1000);//有效时间大概10分钟
				logger.debug("queryUserList put key["+key+"],list["+findList.size()+"]");
				findUserListCache.put(key, newCache);
			}
		}
		
		if(findList == null || findList.size() == 0) {
			ro.setCode(ErrorCode.FAIL_RET_EMPTY);
			ro.setMsg("查询为空");
			return;
		}
		
		
		//总返回对象
		Map<String, Object> userMap = new HashMap<String, Object>();
		userMap.put("page_index", pageIndex);
		userMap.put("page_size", pageSize);
		userMap.put("user_list", findList);
		
		ro.setData(userMap);
		
		return;
	}
	
	/**
	 * 修改用户扩展信息
	 */
	@Transactional
	public void updateUserExtend(UserExtend extend) throws Exception {
		logger.debug("修改用户扩展信息参数:"+extend.toString());
		
		//先判断是新增还是更新
		UserExtend old = userDao.queryUserExtendByUserId(extend.getUser_id());
		if(old != null) {
			//更新
			int row = userDao.updateUserExtend(extend);
			logger.debug("更新成功:"+row);
		} else {
			//新增
			userDao.addNewUserExtend(extend);
			logger.debug("新增成功:"+extend.getPid());
		}
	}
}
