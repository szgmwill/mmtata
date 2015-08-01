/**
 * 深圳市塔塔互动
 * 作者:张伟锐
 * 2015年5月17日
 */
package com.mmtata.im.server.status;

import org.apache.commons.lang.StringUtils;

public enum LoginStatus {
	IN, OUT;
	
	public static LoginStatus getStatus(String input) {
		if(StringUtils.isNotBlank(input)) {
			for(LoginStatus status : values()) {
				if(status.name().equalsIgnoreCase(input)) {
					return status;
				}
			}
		}
		
		return null;
	}
}
