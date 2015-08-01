package com.mmtata.im.server.status;
/**
 * 支付渠道
 * @author 张伟锐
 *
 */
public enum PayChannelType {
	ACCT, WX, ZFB, YL, OTHER;
	
	public static PayChannelType getType(String input) {
		for(PayChannelType type : values()) {
			if(type.name().equalsIgnoreCase(input)) {
				return type;
			}
		}
		
		return null;
	}
}
