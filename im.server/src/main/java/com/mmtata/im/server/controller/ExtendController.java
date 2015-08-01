package com.mmtata.im.server.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mmtata.im.server.common.ResultObject;
import com.mmtata.im.server.constants.ErrorCode;
import com.mmtata.im.server.service.ExtendService;
import com.mmtata.im.server.util.ExceptionUtils;

@Controller
@RequestMapping("/extend")
public class ExtendController {
	private static final Logger logger = Logger.getLogger(ExtendController.class);
	
	@Autowired
	private ExtendService service;
	
	/**
	 * 举报用户
	 * @param reqData
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/add_report")
	@ResponseBody
	public ResultObject addReport(HttpServletRequest request) {
		
		JSONObject reqJson = (JSONObject) request.getAttribute("reqJson");
		
		ResultObject ret = new ResultObject();
		//1.校验请求参数
		String paramErr = checkAddUserReport(reqJson);
		if(paramErr != null) {
			ret.setCode(ErrorCode.FAIL_PARAM);
			ret.setMsg(paramErr);
			
			return ret;
		}
		
		//2.业务处理
		try {
			service.addUserReport(reqJson);
		} catch (Exception e) {
			logger.error("用户举报操作异常:", e);
			ret.setCode(ErrorCode.FAIL_BIZ_ERROR);
			ret.setMsg("用户举报操作异常");
			ret.setSysmsg(ExceptionUtils.traceExceptionMini(e));
			return ret;
		}
		
		
		//3.返回处理
		return ret;
	}
	
	private String checkAddUserReport(JSONObject param) {
		Long userId = param.getLong("user_id");
		Long targetId = param.getLong("target_id");
		Integer type = param.getInteger("type");
		
		if(userId == null || userId <= 0) {
			return "举报人用户id必填[user_id]";
		}
		if(targetId == null || targetId <= 0) {
			return "举报目标用户id必填[target_id]";
		}
		if(type == null || type <= 0 || type > 5) {
			return "举报类型必填[type],取值[1,5]";
		}
		
		return null;
	}
}
