/**
 * 深圳市塔塔互动
 * 作者:张伟锐
 * 2015年5月18日
 */
package com.mmtata.im.server.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mmtata.im.server.bean.TabInfo;
import com.mmtata.im.server.common.ResultObject;
import com.mmtata.im.server.constants.ErrorCode;
import com.mmtata.im.server.service.AdminService;
import com.mmtata.im.server.util.ExceptionUtils;

/**
 * 运营管理接口
 * 
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
	private static final Logger logger = Logger.getLogger(AdminController.class);
	
	@Autowired
	private AdminService adminService;
	
	/**
	 * 查询当前用户可选择的标签列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/tablist", method = RequestMethod.GET)
	@ResponseBody
	public ResultObject queryTabList(HttpServletRequest request) {
		long startTime = System.currentTimeMillis();
		ResultObject ret = new ResultObject();
		try {
			JSONObject reqJson = (JSONObject) request.getAttribute("reqJson");
			Map<String, Object> retMap = new HashMap<String, Object>();
			logger.debug("查询可用标签列表:"+reqJson.toString());
			
			
			//1.校验请求参数
			Integer tabType = reqJson.getInteger("tab_type");
			if(tabType == null || tabType < 0) {
				ret.setCode(ErrorCode.FAIL_PARAM);
				ret.setMsg("标签类型填写有误,[tab_type]取值[0,3]:"+tabType);
				
				return ret;
			}
			//2.业务处理
			List<TabInfo> retList = adminService.queryTabList(tabType);
			if(retList == null || retList.size() == 0) {
				ret.setCode(ErrorCode.FAIL_RET_EMPTY);
				ret.setMsg("查询不到标签列表");
				
				return ret;
			}
			
			retMap.put("tab_list", retList);
			//3.返回处理
			return ResultObject.createInstance(retMap);
		} catch (Exception e) {
			logger.error("查询标签列表操作异常:", e);
			ret.setCode(ErrorCode.FAIL_BIZ_ERROR);
			ret.setMsg("查询标签列表操作异常");
			ret.setSysmsg(ExceptionUtils.traceExceptionMini(e));
			return ret;
		} finally {
			logger.debug("查询标签列表耗时:["+(System.currentTimeMillis() - startTime)+"]");
		}
		
		
	}
	
	/**
	 * 查询当前版本信息
	 */
	@RequestMapping(value = "/query_version", method = RequestMethod.GET)
	@ResponseBody
	public ResultObject queryVersionUpdate(HttpServletRequest request) {
		long startTime = System.currentTimeMillis();
		ResultObject ret = new ResultObject();
		try {
			adminService.queryVersionUpdate(ret);
		} catch (Exception e) {
			logger.error("查询当前版本信息操作异常:", e);
			ret.setCode(ErrorCode.FAIL_BIZ_ERROR);
			ret.setMsg("查询当前版本信息操作异常");
			ret.setSysmsg(ExceptionUtils.traceExceptionMini(e));
			return ret;
		} finally {
			logger.debug("查询当前版本信息耗时:["+(System.currentTimeMillis() - startTime)+"]");
		}
		
		return ret;
	}
	
}
