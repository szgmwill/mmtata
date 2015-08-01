package com.mmtata.im.server.exception;

public class BizException extends Exception {
	
	private int errCode;
	
	public BizException(String message) {
		super(message);
	}
	
	public BizException(int code, String message) {
		super(message);
		this.errCode = code;
	}

	public int getErrCode() {
		return errCode;
	}
	
	
}
