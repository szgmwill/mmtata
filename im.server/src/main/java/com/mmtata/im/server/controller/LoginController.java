package com.mmtata.im.server.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mmtata.im.server.common.ResultObject;
import com.mmtata.im.server.constants.ErrorCode;
import com.mmtata.im.server.service.LoginService;
import com.mmtata.im.server.status.LoginStatus;
import com.mmtata.im.server.util.ExceptionUtils;

/**
 * 用户注册登录模块处理
 * @author Administrator
 *
 */

@Controller
@RequestMapping("/login")
public class LoginController {
	private static final Logger logger = Logger.getLogger(LoginController.class);
	
	@Autowired
	private LoginService loginService;
	
	/**
	 * 测试
	 * @param reqData
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/test")
	@ResponseBody
	public ResultObject action(HttpServletRequest request) {
		JSONObject reqJson = (JSONObject) request.getAttribute("reqJson");
		logger.debug("/test,params:"+reqJson.toString());
		String test = reqJson.getString("test");
		Map<String, String> ret = new HashMap<String, String>();
		ret.put("test", test);
		return ResultObject.createInstance(ret);
	}
	
	/**
	 * 登录登出操作
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/action")
	@ResponseBody
	public ResultObject test(HttpServletRequest request) {
		JSONObject reqJson = (JSONObject) request.getAttribute("reqJson");
		
		ResultObject ret = new ResultObject();
		//1.校验请求参数
		String paramErr = null;
		Long userId = reqJson.getLong("user_id");
		if(userId == null || userId <= 0) {
			paramErr = "用户id必填";
		}
		
		LoginStatus status = LoginStatus.getStatus(reqJson.getString("status"));
		if(status == null) {
			paramErr = "登录操作[status]取值[IN,OUT]";
		}
		
		if(paramErr != null) {
			ret.setCode(ErrorCode.FAIL_PARAM);
			ret.setMsg(paramErr);
			
			return ret;
		}
		
		//2.业务处理
		loginService.login(userId, status);
		
		
		//3.返回处理
		return ret;
	}
	
	/**
	 * 手机用户登录校验
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/mobile_login")
	@ResponseBody
	public ResultObject mobileLogin(HttpServletRequest request) {
		JSONObject reqJson = (JSONObject) request.getAttribute("reqJson");
		
		ResultObject ret = new ResultObject();
		//1.校验请求参数
		String paramErr = null;
		String mobile = reqJson.getString("mobile");
		if(StringUtils.isBlank(mobile)) {
			paramErr = "用户手机号[mobile]必填";
		}
		
		String password = reqJson.getString("password");
		if(StringUtils.isBlank(password)) {
			paramErr = "登录密码[password]必填";
		}
		
		if(paramErr != null) {
			ret.setCode(ErrorCode.FAIL_PARAM);
			ret.setMsg(paramErr);
			
			return ret;
		}
		
		
		
		//2.业务处理
		try {
			loginService.mobileLogin(mobile, password, ret);
		} catch (Exception e) {
			logger.error("手机用户登录操作异常:", e);
			ret.setCode(ErrorCode.FAIL_BIZ_ERROR);
			ret.setMsg("手机用户登录操作异常");
			ret.setSysmsg(ExceptionUtils.traceExceptionMini(e));
			return ret;
		}

		//3.返回处理
		return ret;
	}
}	
