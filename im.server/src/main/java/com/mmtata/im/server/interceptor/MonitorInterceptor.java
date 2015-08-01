package com.mmtata.im.server.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class MonitorInterceptor extends HandlerInterceptorAdapter {
	private static final Logger logger = Logger.getLogger(MonitorInterceptor.class);
	
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		long reqTime = System.currentTimeMillis();
		MDC.put("reqTime", reqTime);//记录请求开始时间
		
		//为每一个请求生成请求流水号,方便跟综一次完整的请求路径,目前采用线程名+时间戳
      	String bizSeq = Thread.currentThread().getName() + "-" + System.currentTimeMillis();

      	logger.info("开始处理HTTP请求:流水号["+bizSeq+"]");
      		
      	//采用log4j MDC 组件实现自动加载流水号信息,可加上任何其它附带信息
      	MDC.put("bizSeq", bizSeq);//key+value方式,这里的key要同时在log4j.properties里读取出来才可展示
      	
      	//打印出请求参数
      	//TO DO
		
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
		
		//请求完后上报监控数据
		//calculate endtime
        long endTime = System.currentTimeMillis();
        long reqTime = (Long) MDC.get("reqTime");
        
        long timeCost = endTime - reqTime;
        //记录请求url
        String reqURI = request.getRequestURI();
        
        //call CommonCaps.jar 's utility
        logger.debug("HTTP Monitor:reqURI["+reqURI+"],timeCost["+timeCost+"]");
		
        //监控一些未知异常
        if(ex != null) {
        	//上报统计未知异常,不过现在监控系统暂不支持
        	logger.info("请求:["+reqURI+"],出现未知异常:"+ex.getMessage());
        }
        
        //清除变量值
        MDC.clear();
        
		super.afterCompletion(request, response, handler, ex);
	}
}
