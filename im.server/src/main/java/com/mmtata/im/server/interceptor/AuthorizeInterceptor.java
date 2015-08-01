package com.mmtata.im.server.interceptor;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mmtata.im.server.common.ResultObject;
import com.mmtata.im.server.constants.ErrorCode;
import com.mmtata.im.server.service.LoginService;
import com.mmtata.im.server.util.JsonHelper;
import com.mmtata.im.server.util.SignUtils;

/**
 * 
 * 调用后台身份认证拦截处理
 *
 */
public class AuthorizeInterceptor extends HandlerInterceptorAdapter {
	
	private static final Logger logger = Logger.getLogger(AuthorizeInterceptor.class);
	
	@Autowired
	private LoginService loginService;
	
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String reqURI = request.getRequestURI();

      	//获取签名必须的参数
      	String client = request.getParameter("client");
      	String timestamp = request.getParameter("timestamp");
      	//业务参数也必须校验必填性和合法性
      	String reqData = request.getParameter("reqData");
      	logger.info("开始认证接口调用方身份:URI["+reqURI+"],params["+getAllParamFromHttpReq(request)+"]");
      	
      	//客户端计算的签名值
      	String signFromClient = request.getParameter("signkey");
      	
      	
      	ResultObject errorRet = new ResultObject();
      	JSONObject reqJsonObj = JsonHelper.genJsonObj(reqData);
      	
      	if(StringUtils.isBlank(reqData)) {
      		errorRet.setCode(ErrorCode.FAIL_PARAM);
      		errorRet.setMsg("业务请求参数[reqData]必填");

      	} else if (reqJsonObj == null) {
      		errorRet.setCode(ErrorCode.FAIL_PARAM);
      		errorRet.setMsg("业务请求参数[reqData]格式不合法:"+reqData);
      	} else {//验证签名
      		
      		//获取请求方法GET/POST
    		String httpMethod = request.getMethod();
    		logger.debug("reqURI["+reqURI+"],httpMethod:"+httpMethod);
    		if("GET".equalsIgnoreCase(httpMethod)) {
    			//GET请求通通暂时不验证
    			
    		} else {
    			//某些接口暂不需要验证
          		if(reqURI.indexOf("/user/register") != -1 || reqURI.indexOf("/admin/") != -1
          				|| reqURI.indexOf("/login/action") != -1) {
          			logger.debug("reqURI:"+reqURI+",no need sign==");
          		} else {
          			//如果该api带了用户id,则要身份认证安全
              		Long userId = reqJsonObj.getLong("user_id");
              		logger.debug("userId:"+userId);
              		//服务端计算出签名值sign
                  	String sign = SignUtils.makeSign(reqURI, client, timestamp, loginService.queryTokenByUserId(userId));
                  	
//                  	if(StringUtils.isEmpty(sign) || !sign.equals(signFromClient)) {
//                  		logger.info("签名不匹配:signC["+signFromClient+"],signS["+sign+"]");
//
//                  		errorRet.setCode(ErrorCode.FAIL_SIGN);
//                  		errorRet.setMsg("签名值[sign]不合法:"+signFromClient);
//                  	}
          		}
    		}
      		
      		
      	}

      	if(errorRet.getCode() != ErrorCode.OK) {
      		// 写到返回
			response.setContentType(ContentType.APPLICATION_JSON.getMimeType());//json返回
			response.getWriter().write(JSON.toJSONString(errorRet));
			
			return false;
      	}

      	//将请求参数预处理好,给后面业务校验方便
      	request.setAttribute("reqJson", reqJsonObj);
      	
		return super.preHandle(request, response, handler);
	}
	
	@Override
	public void afterConcurrentHandlingStarted(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		super.afterConcurrentHandlingStarted(request, response, handler);
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
        
		super.afterCompletion(request, response, handler, ex);
	}
	
	/**
	 * 获取http的所有请求参数
	 */
	private String getAllParamFromHttpReq(HttpServletRequest request) {
		@SuppressWarnings("rawtypes")
		Enumeration paramNames = request.getParameterNames();
		StringBuffer sb = new StringBuffer();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();

			String[] paramValues = request.getParameterValues(paramName);
			if (paramValues.length == 1) {
				String paramValue = paramValues[0];
				if (paramValue.length() != 0) {
					sb.append("&").append(paramName).append("=")
							.append(paramValue);
				}
			}
		}

		return sb.toString();
	}
}
