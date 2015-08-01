package com.mmtata.im.server.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.mmtata.im.server.common.ResultObject;
import com.mmtata.im.server.constants.ErrorCode;
import com.mmtata.im.server.util.ExceptionUtils;

/**
 * 全局错误异常统一处理入口
 * @author zhangweirui
 *
 */
public class GlobalExceptionHandler implements HandlerExceptionResolver {
	private final static Logger log = Logger.getLogger(GlobalExceptionHandler.class);
	
	/**
	 * 使用fastjson作为全局json格式转换器
	 */
	@Autowired
	private FastJsonHttpMessageConverter fastJsonHttpMessageConverter;


	/**
	 * 针对json异常统一处理返回
	 */
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
		
		//目前的请求应该都是json方式的
		return doResolveExceptionForJson(request, response, exception);
	}
	
	/**
	 * 处理json异常返回
	 * @param exception
	 * @return
	 */
	private ModelAndView doResolveExceptionForJson(HttpServletRequest request, HttpServletResponse response, Exception ex) {
		ResultObject result = new ResultObject();
		result.setCode(ErrorCode.FAIL_ALL);//返回-1的都是异常类别
		
		//这里调试阶段可以先把异常信息找出来,上IDC后最好打印用户友好一点
		//打印出异常的简单信息
		result.setMsg("后台服务器处理失败");
		if(ex != null) {
			result.setSysmsg("异常信息:"+ExceptionUtils.traceExceptionMini(ex));
		}
		
		
		ServletWebRequest webRequest = new ServletWebRequest(request, response);
		
		HttpOutputMessage outputMessage = new ServletServerHttpResponse(webRequest.getResponse());
		
		try {
			fastJsonHttpMessageConverter.write(result, MediaType.APPLICATION_JSON, outputMessage);
		} catch (Exception jsonEx) {
			log.error("Error rendering json response!", jsonEx);
		}
		return new ModelAndView();
	}
	
}
