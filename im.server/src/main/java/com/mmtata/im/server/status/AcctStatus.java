package com.mmtata.im.server.status;
/**
 * 账户状态
 * @author 张伟锐
 *
 */
public enum AcctStatus {
	NORMAL, FREEZE;
	
	public static AcctStatus getStatus(String input) {
		for(AcctStatus status : values()) {
			if(status.name().equalsIgnoreCase(input)) {
				return status;
			}
		}
		
		return null;
	}
}
