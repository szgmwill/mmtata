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
import com.mmtata.im.server.service.QiniuCloudService;

/**
 * 图片上传接口
 * 使用七牛云存储解决方案
 * @author 张伟锐
 *
 */
@Controller
@RequestMapping("/image")
public class ImageUploadController {
	private static final Logger logger = Logger.getLogger(ImageUploadController.class);
	
	@Autowired
	private QiniuCloudService qnService;
	
	/**
	 * 获取上传服务器凭证
	 */
	@RequestMapping(value = "/uptoken", method = RequestMethod.GET)
	@ResponseBody
	private ResultObject getToken(HttpServletRequest request) {
		JSONObject reqJson = (JSONObject) request.getAttribute("reqJson");
		logger.debug("获取上传服务器凭证:"+reqJson.toString());
		
		ResultObject ret = new ResultObject();
		
		String token = qnService.getUpToken();
		
		if(StringUtils.isEmpty(token)) {
			ret.setCode(ErrorCode.FAIL_RET_EMPTY);
			
			ret.setMsg("查询不到有效上传凭证");
			
		} else {
			Map<String, String> retMap = new HashMap<String, String>();
			retMap.put("uptoken", token);
			ret.setData(retMap);
		}
		return ret;
	}
}
