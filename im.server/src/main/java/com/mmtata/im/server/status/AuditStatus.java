package com.mmtata.im.server.status;
/**
 * 提现审核状态
 * @author 张伟锐
 *
 */
public enum AuditStatus {
	NEW,
	PASS,
	FAIL;
	
	public static AuditStatus getStatus(String input) {
		for(AuditStatus status : values()) {
			if(status.name().equalsIgnoreCase(input)) {
				return status;
			}
		}
		
		return null;
	}
}
