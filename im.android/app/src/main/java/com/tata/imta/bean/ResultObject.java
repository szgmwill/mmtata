package com.tata.imta.bean;




/**
 * json 统一标准返回对象
 * 服务端返回对象
 * @author zhangweirui
 *
 */

public class ResultObject {
	/**
	 * 返回错误码
	 */
	private int code = 0;
	/**
	 * 返回错误信息
	 */
	private String msg = "成功";
	/**
	 * 返回具体的业务内容
	 */
	private Object data;
	/**
	 * 返回系统异常信息
	 */
	private String sysmsg;

	public ResultObject() {
	}

	public ResultObject(Object data) {
		this.data = data;
	}

	public ResultObject(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public static ResultObject createErrorInstance(int code, String msg) {
		ResultObject result = new ResultObject(code, msg);
		return result;
	}

	public static ResultObject createInstance(Object object) {
		ResultObject result = new ResultObject();
		result.data = object;
		return result;
	}

	public String getSysmsg() {
		return sysmsg;
	}

	public void setSysmsg(String sysmsg) {
		this.sysmsg = sysmsg;
	}

	
}
