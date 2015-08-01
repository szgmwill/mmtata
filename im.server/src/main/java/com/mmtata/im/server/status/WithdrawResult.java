package com.mmtata.im.server.status;
/**
 * 提现结果
 * @author 张伟锐
 *
 */
public enum WithdrawResult {
	NEW,
	DONE,
	FAIL;
	
	public static WithdrawResult getResult(String input) {
		for(WithdrawResult result : values()) {
			if(result.name().equalsIgnoreCase(input)) {
				return result;
			}
		}
		
		return null;
	}
}
