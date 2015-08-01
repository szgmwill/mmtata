/**
 * 深圳市塔塔互动
 * 作者:张伟锐
 * 2015年5月17日
 */
package com.mmtata.im.server.controller;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mmtata.im.server.bean.UserExtend;
import com.mmtata.im.server.common.ResultObject;
import com.mmtata.im.server.constants.ErrorCode;
import com.mmtata.im.server.service.UserBaseService;
import com.mmtata.im.server.status.FeeUnit;
import com.mmtata.im.server.status.Sex;
import com.mmtata.im.server.util.ExceptionUtils;
/**
 * 用户接口
 * 注意:良好的程序必须对请求的相关内容参数等做详细的合法性校验,特别是写接口的入参
 */
@Controller
@RequestMapping("/user")
public class UserController {
	
	private static final Logger logger = Logger.getLogger(UserController.class);
	
	@Autowired
	UserBaseService userService;
	
	
	/**
	 * 用户注册
	 */
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@ResponseBody
	public ResultObject registerUser(HttpServletRequest request) {
		JSONObject reqJson = (JSONObject) request.getAttribute("reqJson");
		
		ResultObject ret = new ResultObject();
		//1.校验请求参数
		String paramErr = checkRegisterParam(reqJson);
		if(paramErr != null) {
			ret.setCode(ErrorCode.FAIL_PARAM);
			ret.setMsg(paramErr);
			
			return ret;
		}
		
		//2.业务处理
		try {
			userService.registerUser(reqJson, ret);
		} catch (Exception e) {
			logger.error("注册新用户操作异常:", e);
			ret.setCode(ErrorCode.FAIL_BIZ_ERROR);
			ret.setMsg("注册新用户操作异常");
			ret.setSysmsg(ExceptionUtils.traceExceptionMini(e));
			return ret;
		}
		
		
		//3.返回处理
		return ret;
	}
	
	/**
	 * 查询用户详细信息
	 * 包括用户的所有信息返回
	 */
	@RequestMapping(value = "/query_info", method = RequestMethod.GET)
	@ResponseBody
	public ResultObject queryUserDetail(HttpServletRequest request) {
		JSONObject reqJson = (JSONObject) request.getAttribute("reqJson");
		
		ResultObject ret = new ResultObject();
		//1.校验请求参数
		Long user_id = reqJson.getLong("user_id");
		if(user_id == null || user_id <= 0) {
			ret.setCode(ErrorCode.FAIL_PARAM);
			ret.setMsg("查询用户id[user_id]必填");
			
			return ret;
		}
		
		//2.业务处理
		try {
			userService.queryUserDetail(user_id, ret);
		} catch (Exception e) {
			logger.error("查询用户详情操作异常:", e);
			ret.setCode(ErrorCode.FAIL_BIZ_ERROR);
			ret.setMsg("查询用户详情操作异常");
			ret.setSysmsg(ExceptionUtils.traceExceptionMini(e));
			return ret;
		}
		
		
		//3.返回处理
		return ret;
	}
	
	/**
	 * 修改用户基础信息,不包含扩展信息
	 * @param reqJson
	 * @return
	 */
	@RequestMapping(value = "/edit_base", method = RequestMethod.POST)
	@ResponseBody
	public ResultObject updateUserBase(HttpServletRequest request) {
		JSONObject reqJson = (JSONObject) request.getAttribute("reqJson");
		
		ResultObject ret = new ResultObject();
		//1.校验请求参数
		String paramErr = checkUpdateUserBase(reqJson);
		if(paramErr != null) {
			ret.setCode(ErrorCode.FAIL_PARAM);
			ret.setMsg(paramErr);
			
			return ret;
		}
		
		//2.业务处理
		try {
			userService.updateUserBase(reqJson, ret);
		} catch (Exception e) {
			logger.error("修改用户基础信息操作异常:", e);
			ret.setCode(ErrorCode.FAIL_BIZ_ERROR);
			ret.setMsg("修改用户基础信息操作异常");
			ret.setSysmsg(ExceptionUtils.traceExceptionMini(e));
			return ret;
		}
		
		
		//3.返回处理
		return ret;
	}
	
	/**
	 * 查看在线用户列表
	 * (发现用户)
	 */
	@RequestMapping(value = "/query_userlist", method = RequestMethod.GET)
	@ResponseBody
	public ResultObject queryUserList(HttpServletRequest request) {
		JSONObject reqJson = (JSONObject) request.getAttribute("reqJson");
		
		ResultObject ret = new ResultObject();
		//1.校验请求参数
		String paramErr = checkQueryUserList(reqJson);
		if(paramErr != null) {
			ret.setCode(ErrorCode.FAIL_PARAM);
			ret.setMsg(paramErr);
			return ret;
		}
		
		//2.业务处理
		try {
			userService.queryUserList(reqJson, ret);
		} catch (Exception e) {
			logger.error("查询用户列表操作异常:", e);
			ret.setCode(ErrorCode.FAIL_BIZ_ERROR);
			ret.setMsg("查询用户列表操作异常");
			ret.setSysmsg(ExceptionUtils.traceExceptionMini(e));
			return ret;
		}
		
		
		//3.返回处理
		return ret;
	}
	
	/**
	 * 修改用户扩展信息
	 */
	@RequestMapping(value = "/edit_extend", method = RequestMethod.POST)
	@ResponseBody
	public ResultObject updateUserExtend(HttpServletRequest request) {
		JSONObject reqJson = (JSONObject) request.getAttribute("reqJson");
		
		ResultObject ret = new ResultObject();
		UserExtend updateUser = new UserExtend();
		//1.校验请求参数
		String paramErr = checkUpdateUserExtend(reqJson, updateUser);
		if(paramErr != null) {
			ret.setCode(ErrorCode.FAIL_PARAM);
			ret.setMsg(paramErr);
			
			return ret;
		}
		
		//2.业务处理
		try {
			userService.updateUserExtend(updateUser);
		} catch (Exception e) {
			logger.error("修改用户扩展信息操作异常:", e);
			ret.setCode(ErrorCode.FAIL_BIZ_ERROR);
			ret.setMsg("修改用户扩展信息操作异常");
			ret.setSysmsg(ExceptionUtils.traceExceptionMini(e));
			return ret;
		}
		
		
		//3.返回处理
		return ret;
	}
	
	private String checkRegisterParam(JSONObject reqJson) {
		//判断是微信注册还是手机注册
		String wxOpenId = reqJson.getString("openid");
		
		String mobile = reqJson.getString("mobile");
		String password = reqJson.getString("password");
		
		if(StringUtils.isBlank(wxOpenId) && StringUtils.isBlank(mobile)) {
			return "微信注册或者手机注册必填其一[openid][mobile]";
		}
		
		if(StringUtils.isNotBlank(mobile) && StringUtils.isBlank(password)) {
			return "手机号注册必须填写密码:[password]";
		}
		
		String sex = reqJson.getString("sex");
		if(sex != null && Sex.getSex(sex) == null) {
			return "用户性别取值[BOY,GIRL]";
		}
		
		JSONArray tablist = reqJson.getJSONArray("tab_list");
		if(tablist != null) {
			
			for(int i=0; i<tablist.size(); i++) {
				JSONObject tab = (JSONObject)tablist.get(i);
				Integer type = tab.getInteger("tab_type");
				if(type == null || type <= 0) {
					return "用户标签类型[tab_type]取值[>0]:"+type;
				} else if (StringUtils.isBlank(tab.getString("tab_name"))) {
					return "用户标签名称[tab_name]必填";
				}
			}
		}
		//其它参数的必要校验:待补充
		return null;
	}
	
	private String checkUpdateUserBase(JSONObject reqJson) {
		Long user_id = reqJson.getLong("user_id");
		if(user_id == null || user_id <= 0) {
			return "用户id[user_id]必填";
		}
		//其它参数的必要校验:待补充
		return null;
	}
	
	private String checkUpdateUserExtend(JSONObject reqJson, UserExtend update) {
		boolean isNeedUpate = false;//是否至少有一项要更新的
		Long user_id = reqJson.getLong("user_id");
		if(user_id == null || user_id <= 0) {
			return "用户id[user_id]必填";
		}
		update.setUser_id(user_id);
		
		BigDecimal fee = reqJson.getBigDecimal("fee");
		if(fee != null) {
			isNeedUpate = true;
			update.setFee(fee);
		}
		String unit = reqJson.getString("fee_unit");
		if(unit != null) {
			FeeUnit feeUnit = FeeUnit.getUnit(unit);
			if(feeUnit == null) {
				return "fee_unit填写有误:"+unit;
			}
			isNeedUpate = true;
			update.setFee_unit(feeUnit);
		}
		
		Integer duration = reqJson.getInteger("fee_duration");
		if(duration != null) {
			isNeedUpate = true;
			update.setFee_duration(duration);
		}
		
		Integer allowFreeChat = reqJson.getInteger("allow_free_chat");
		if(allowFreeChat != null) {
			if(allowFreeChat != 0 && allowFreeChat != 1) {
				return "allow_free_chat填写有误,取值[0,1],实际:"+allowFreeChat;
			}
			isNeedUpate = true;
			update.setAllow_free_chat(allowFreeChat);
		}
		
		String mobile = reqJson.getString("bind_mobile");
		if(mobile != null) {
			isNeedUpate = true;
			update.setBind_mobile(mobile);
		}
		String wxAcct = reqJson.getString("wx_acct");
		if(wxAcct != null) {
			isNeedUpate = true;
			update.setWx_acct(wxAcct);
		}
		
		String zfbAcct = reqJson.getString("zfb_acct");
		if(zfbAcct != null) {
			isNeedUpate = true;
			update.setZfb_acct(zfbAcct);
		}
		
		String zfbNick = reqJson.getString("zfb_nick");
		if(zfbNick != null) {
			isNeedUpate = true;
			update.setZfb_nick(zfbNick);
		}
		
//		Double feedback = reqJson.getDouble("feedback");
//		if(feedback != null) {
//			isNeedUpate = true;
//			update.setFeedback(feedback);
//		}
//		
		if(!isNeedUpate) {
			return "更新信息必须至少填写一项";
		}
		
		return null;
	}
	
	private String checkQueryUserList(JSONObject reqJson) {
//		page_index	int	是	查询页码索引：从第1页开始；
//		page_size	int	是	每页返回条数：默认20；最大不超50；
//		sex	int	否	性别:0-全部；男1、女2
		
		Integer pageIndex = reqJson.getInteger("page_index");
		Integer pageSize = reqJson.getInteger("page_size");
		Integer sex = reqJson.getInteger("sex");
		if(pageIndex == null || pageIndex < 1) {
			return "查询页码page_index必填且从1开始";
		}
		if(pageSize == null || pageSize < 1 || pageSize > 20) {
			return "查询页码pageSize必填且取值[1,20]";
		}
		
		if(sex == null || sex < 0 || sex > 2) {
			return "查询性别必填且取值[0,2]";
		}
		
		return null;
	}
}
