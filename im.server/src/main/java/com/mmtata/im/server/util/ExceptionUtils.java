package com.mmtata.im.server.util;

/**
 * 异常处理工具类
 * @author zhangweirui
 *
 */
public class ExceptionUtils {
	/**
	 * 抽出异常中的核心信息<br>
	 * 1.输出异常种类，如nullpoint，time out<br>
	 * 2.如果有异常栈，则输出抛出异常的类、方法、行号
	 * 
	 * @param e
	 * @return
	 */
	public static String traceException(Exception e) {
		if (e == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder(e.toString());
		StackTraceElement[] stackTrace = e.getStackTrace();
		if (stackTrace != null && stackTrace.length > 0) {
			StackTraceElement stackTraceElement = stackTrace[0];
			sb.append(" Class:").append(stackTraceElement.getClassName())
					.append(" Method:")
					.append(stackTraceElement.getMethodName()).append(" Line:")
					.append(stackTraceElement.getLineNumber());
		} else {
			sb.append("no stackTrace");
		}

		return sb.toString();
	}

	/**
	 * 抽出异常中的最小信息集合<br>
	 * 1.仅输出异常种类，如nullpoint，time out<br>
	 * 
	 * @param e
	 * @return
	 */
	public static String traceExceptionMini(Exception e) {
		if (e == null) {
			return "";
		}
		return e.toString();
	}
}
